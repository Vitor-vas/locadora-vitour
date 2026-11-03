package com.example.backend.service;

import com.example.backend.dto.CarDto;
import com.example.backend.model.Car;
import com.example.backend.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CarService {
    private final CarRepository cars;

    @Transactional(readOnly = true)
    public List<CarDto> list() { return cars.findAll().stream().map(CarDto::from).toList(); }

    @Transactional(readOnly = true)
    public CarDto get(UUID id) { return CarDto.from(find(id)); }

    public CarDto create(CarDto dto) {
        if (cars.existsByPlaca(dto.placa()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Placa já cadastrada");
        if (cars.existsByMatricula(dto.matricula()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada");
        Car c = new Car();
        dto.copyTo(c);
        return CarDto.from(cars.save(c));
    }

    public CarDto update(UUID id, CarDto dto) {
        Car c = find(id);
        if (!c.getPlaca().equals(dto.placa()) && cars.existsByPlaca(dto.placa()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Placa já cadastrada");
        if (!c.getMatricula().equals(dto.matricula()) && cars.existsByMatricula(dto.matricula()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada");
        dto.copyTo(c);
        return CarDto.from(c); // gravado ao final da transação
    }

    public void delete(UUID id) {
        cars.delete(find(id));
        cars.flush(); // força o DELETE agora: falha com 409 se o carro estiver em algum pedido
    }

    private Car find(UUID id) {
        return cars.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Automóvel não encontrado"));
    }
}