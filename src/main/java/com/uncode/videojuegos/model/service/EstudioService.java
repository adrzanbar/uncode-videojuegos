package com.uncode.videojuegos.model.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.repository.EstudioRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import jakarta.transaction.Transactional;

@Service
public class EstudioService {

    @Autowired
    private EstudioRepository repository;

    public void validate(String nombre) throws ServiceException {
        if (nombre.isBlank()) {
            throw new ServiceException("El nombre no puede estar vacío");
        }
    }

    @Transactional
    public void save(String nombre) throws ServiceException {
        validate(nombre);
        if (repository.existsByActivoTrueAndNombre(nombre)) {
            throw new ServiceException("El estudio ya existe: " + nombre);
        }
        repository.save(Estudio.builder()
                .nombre(nombre)
                .build());
    }

    @Transactional
    public void update(UUID id, String nombre) throws ServiceException {
        validate(nombre);
        if (repository.existsByIdNotAndActivoTrueAndNombre(id, nombre)) {
            throw new ServiceException("El estudio ya existe: " + nombre);
        }
        var estudio = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ServiceException("No se pudo encontrar el estudio: " + id));
        estudio.setNombre(nombre);
        repository.save(estudio);
    }

    @Transactional
    public void delete(UUID id) throws ServiceException {
        var estudio = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ServiceException("No se pudo encontrar el estudio: " + id));
        estudio.setActivo(false);
        repository.save(estudio);
    }

    public Set<Estudio> getAll() throws ServiceException {
        return new HashSet<>(repository.findByActivoTrue());
    }

    public Optional<Estudio> get(UUID id) throws ServiceException {
        return repository.findByIdAndActivoTrue(id);
    }

    public Optional<Estudio> get(String nombre) throws ServiceException {
        return repository.findByActivoTrueAndNombre(nombre);
    }
}