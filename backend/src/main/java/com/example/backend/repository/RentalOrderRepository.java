package com.example.backend.repository;

import com.example.backend.model.OrderStatus;
import com.example.backend.model.RentalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RentalOrderRepository extends JpaRepository<RentalOrder, UUID> {
    List<RentalOrder> findByClientId(UUID clientId); // pedidos de um cliente

    // Existe outro pedido "ativo" para o mesmo carro cujo período se sobrepõe a [inicio, fim]?
    // ignoreId exclui o próprio pedido ao modificá-lo (use um UUID qualquer ao criar).
    @Query("""
            select count(o) > 0 from RentalOrder o
            where o.car.id = :carId
              and o.status in :statuses
              and o.id <> :ignoreId
              and o.inicio <= :fim
              and o.fim >= :inicio
            """)
    boolean existsOverlap(@Param("carId") UUID carId,
                          @Param("statuses") Collection<OrderStatus> statuses,
                          @Param("inicio") LocalDate inicio,
                          @Param("fim") LocalDate fim,
                          @Param("ignoreId") UUID ignoreId);
}