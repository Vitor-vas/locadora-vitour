package com.example.backend.service;

import com.example.backend.dto.CreditDto;
import com.example.backend.dto.OrderDto;
import com.example.backend.dto.OrderRequest;
import com.example.backend.model.*;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.ClientRepository;
import com.example.backend.repository.RentalOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

// Regras de negócio dos pedidos de aluguel:
//  - CLIENTE: cria pedidos para si, modifica enquanto PENDENTE, cancela (PENDENTE/APROVADO) e consulta só os próprios.
//  - AGENTE: vê todos, modifica (PENDENTE/APROVADO), avalia (aprova/reprova), executa o contrato e cancela.
// Fluxo: PENDENTE -> APROVADO | REPROVADO ; APROVADO -> CONTRATADO ; PENDENTE/APROVADO -> CANCELADO.
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    // Estados que ocupam o carro no período (usados na checagem de disponibilidade).
    private static final List<OrderStatus> ACTIVE = List.of(OrderStatus.PENDENTE, OrderStatus.APROVADO, OrderStatus.CONTRATADO);
    private static final UUID NO_ORDER = new UUID(0, 0);

    private final RentalOrderRepository orders;
    private final ClientRepository clients;
    private final CarRepository cars;
    private final AppUserRepository users;

    @Transactional(readOnly = true)
    public List<OrderDto> list(AppUser caller) {
        List<RentalOrder> rows = isAgent(caller) ? orders.findAll() : orders.findByClientId(ownClient(caller).getId());
        return rows.stream()
                .sorted(Comparator.comparing(RentalOrder::getCriadoEm).reversed())
                .map(OrderDto::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderDto get(UUID id, AppUser caller) {
        RentalOrder o = find(id);
        assertCanAccess(o, caller);
        return OrderDto.from(o);
    }

    public OrderDto create(OrderRequest r, AppUser caller) {
        Client client = isAgent(caller)
                ? clients.findById(need(r.clientId(), "clientId"))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado"))
                : ownClient(caller); // cliente nunca cria pedido para outra pessoa
        Car car = findCar(need(r.carId(), "carId"));
        LocalDate inicio = need(r.inicio(), "inicio"), fim = need(r.fim(), "fim");
        validatePeriod(inicio, fim);
        assertAvailable(car, inicio, fim, NO_ORDER);

        RentalOrder o = new RentalOrder(); // status PENDENTE e criadoEm vêm dos valores padrão da entidade
        o.setClient(client); o.setCar(car); o.setInicio(inicio); o.setFim(fim);
        applyCredit(o, r.credito());
        return OrderDto.from(orders.save(o));
    }

    // PUT parcial. Se vier carId, é uma modificação de dados (o crédito vem junto: ausente = removido).
    // Se vier status, é uma mudança de estado (com parecer opcional, aplicado só para agentes).
    public OrderDto update(UUID id, OrderRequest r, AppUser caller) {
        RentalOrder o = find(id);
        assertCanAccess(o, caller);
        if (r.carId() != null) editData(o, r, caller);
        if (r.status() != null && r.status() != o.getStatus()) changeStatus(o, r.status(), r.parecer(), caller);
        else if (r.parecer() != null) saveParecer(o, r.parecer(), caller);
        return OrderDto.from(o); // alterações gravadas ao final da transação
    }

    // ---------- modificação de dados ----------
    private void editData(RentalOrder o, OrderRequest r, AppUser caller) {
        boolean editable = isAgent(caller)
                ? o.getStatus() == OrderStatus.PENDENTE || o.getStatus() == OrderStatus.APROVADO
                : o.getStatus() == OrderStatus.PENDENTE;
        if (!editable)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido " + o.getStatus() + " não pode mais ser modificado");

        LocalDate inicio = need(r.inicio(), "inicio"), fim = need(r.fim(), "fim");
        validatePeriod(inicio, fim);
        Car car = findCar(r.carId());
        assertAvailable(car, inicio, fim, o.getId());

        if (isAgent(caller) && r.clientId() != null)
            o.setClient(clients.findById(r.clientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado")));
        o.setCar(car); o.setInicio(inicio); o.setFim(fim);
        applyCredit(o, r.credito());
    }

    // ---------- mudança de estado ----------
    private void changeStatus(RentalOrder o, OrderStatus target, String parecer, AppUser caller) {
        OrderStatus now = o.getStatus();
        switch (target) {
            case CANCELADO -> {
                if (now != OrderStatus.PENDENTE && now != OrderStatus.APROVADO) throw invalid(now, target);
            }
            case APROVADO, REPROVADO -> {
                requireAgent(caller, "Somente agentes podem avaliar pedidos");
                if (now != OrderStatus.PENDENTE) throw invalid(now, target);
            }
            case CONTRATADO -> {
                requireAgent(caller, "Somente agentes podem executar o contrato");
                if (now != OrderStatus.APROVADO) throw invalid(now, target);
            }
            default -> throw invalid(now, target); // não existe volta para PENDENTE
        }
        if (parecer != null) saveParecer(o, parecer, caller);
        o.setStatus(target);
    }

    private void saveParecer(RentalOrder o, String parecer, AppUser caller) {
        if (!isAgent(caller)) return; // o front reenvia o parecer ao cancelar; cliente não altera parecer
        if (o.getStatus() != OrderStatus.PENDENTE && o.getStatus() != OrderStatus.APROVADO)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido encerrado: o parecer não pode ser alterado");
        if (parecer.length() > 1000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parecer: máximo de 1000 caracteres");
        o.setParecer(parecer.isBlank() ? null : parecer.trim());
    }

    // ---------- helpers ----------
    private void applyCredit(RentalOrder o, CreditDto c) {
        if (c == null) { o.setCreditoBanco(null); o.setCreditoValor(null); return; }
        AppUser banco = users.findById(need(c.bancoId(), "credito.bancoId"))
                .filter(u -> u.getAgentType() == AgentType.BANCO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Banco inválido para o crédito"));
        if (c.valor() == null || c.valor().signum() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor do crédito deve ser maior que zero");
        o.setCreditoBanco(banco);
        o.setCreditoValor(c.valor());
    }

    private void validatePeriod(LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data final deve ser posterior à inicial");
    }

    private void assertAvailable(Car car, LocalDate inicio, LocalDate fim, UUID ignoreId) {
        if (orders.existsOverlap(car.getId(), ACTIVE, inicio, fim, ignoreId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Automóvel indisponível no período selecionado");
    }

    private static <T> T need(T value, String field) {
        if (value == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + ": obrigatório");
        return value;
    }

    private static ResponseStatusException invalid(OrderStatus from, OrderStatus to) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Transição não permitida: " + from + " -> " + to);
    }

    private static boolean isAgent(AppUser u) { return u.getRole() == Role.AGENTE; }

    private static void requireAgent(AppUser u, String msg) {
        if (!isAgent(u)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
    }

    private static Client ownClient(AppUser u) {
        if (u.getClient() == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário sem perfil de cliente");
        return u.getClient();
    }

    // Agente acessa qualquer pedido; cliente só os próprios.
    private void assertCanAccess(RentalOrder o, AppUser caller) {
        if (isAgent(caller)) return;
        if (caller.getClient() == null || !caller.getClient().getId().equals(o.getClient().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
    }

    private RentalOrder find(UUID id) {
        return orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private Car findCar(UUID id) {
        return cars.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Automóvel não encontrado"));
    }
}