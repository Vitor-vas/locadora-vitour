package com.example.backend.repository;

import com.example.backend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {
    boolean existsByPlaca(String placa);
    boolean existsByMatricula(String matricula);
}