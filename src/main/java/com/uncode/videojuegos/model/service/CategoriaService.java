package com.uncode.videojuegos.model.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.repository.CategoriaRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public void validate(String nombre) throws ServiceException {
        if (nombre.isBlank()) {
            throw new ServiceException("El nombre no puede estar vacío");
        }
    }

    @Transactional
    public void save(String nombre) throws ServiceException {
        validate(nombre);
        if (repository.existsByActivoTrueAndNombre(nombre)) {
            throw new ServiceException("La categoría ya existe: " + nombre);
        }
        repository.save(Categoria.builder()
                .nombre(nombre)
                .build());
    }

    @Transactional
    public void update(UUID id, String nombre) throws ServiceException {
        validate(nombre);
        if (repository.existsByIdNotAndActivoTrueAndNombre(id, nombre)) {
            throw new ServiceException("La categoría ya existe: " + nombre);
        }
        var categoria = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ServiceException("No se pudo encontrar la categoría: " + id));
        categoria.setNombre(nombre);
        repository.save(categoria);
    }

    @Transactional
    public void delete(UUID id) throws ServiceException {
        var categoria = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ServiceException("No se pudo encontrar la categoría: " + id));
        categoria.setActivo(false);
        repository.save(categoria);
    }

    public Set<Categoria> getAll() throws ServiceException {
        return new HashSet<>(repository.findByActivoTrue());
    }

    public Optional<Categoria> get(UUID id) throws ServiceException {
        return repository.findByIdAndActivoTrue(id);
    }

    public Optional<Categoria> get(String nombre) throws ServiceException {
        return repository.findByActivoTrueAndNombre(nombre);
    }
}
