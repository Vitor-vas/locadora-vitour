package com.example.backend.config;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Cria os dados de demonstração (os mesmos contas do front) quando o banco está vazio.
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final AppUserRepository users;
    private final ClientRepository clients;
    private final CarRepository cars;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            Client maria = new Client();
            maria.setRg("MG-12.345.678"); maria.setCpf("123.456.789-00"); maria.setNome("Maria Souza");
            maria.setEndereco("Rua das Flores, 100 - BH"); maria.setProfissao("Engenheira");
            Employer e = new Employer();
            e.setEntidade("Construtora Alfa"); e.setRendimento(new BigDecimal("8500"));
            maria.getEmpregadores().add(e);
            clients.save(maria);
            users.save(user("cliente", "cliente123", Role.CLIENTE, null, maria));
            users.save(user("banco", "banco123", Role.AGENTE, AgentType.BANCO, null));
            users.save(user("empresa", "empresa123", Role.AGENTE, AgentType.EMPRESA, null));
        }
        if (cars.count() == 0) {
            cars.save(car("MAT-0001", 2023, "Toyota", "Corolla", "ABC1D23", Owner.EMPRESA));
            cars.save(car("MAT-0002", 2022, "Honda", "Civic", "DEF4G56", Owner.BANCO));
            cars.save(car("MAT-0003", 2024, "VW", "Polo", "HIJ7K89", Owner.CLIENTE));
        }
    }

    private AppUser user(String name, String pass, Role role, AgentType type, Client client) {
        AppUser u = new AppUser();
        u.setUsername(name); u.setPasswordHash(encoder.encode(pass));
        u.setRole(role); u.setAgentType(type); u.setClient(client);
        return u;
    }

    private Car car(String matricula, int ano, String marca, String modelo, String placa, Owner owner) {
        Car c = new Car();
        c.setMatricula(matricula); c.setAno(ano); c.setMarca(marca); c.setModelo(modelo); c.setPlaca(placa); c.setProprietario(owner);
        return c;
    }
}
