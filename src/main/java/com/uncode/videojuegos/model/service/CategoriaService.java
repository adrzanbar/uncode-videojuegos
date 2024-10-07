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
import com.uncode.videojuegos.model.service.exception.ServiceExceptionMessages;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public void validate(String nombre) throws ServiceException {
        try {
            if (nombre.isBlank()) {
                throw new ServiceException(ServiceExceptionMessages.blank(Categoria.class, "nombre"));
            }
        } catch (ServiceException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new ServiceException(ServiceExceptionMessages.$null(Categoria.class, "nombre"));
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public UUID create(String nombre) throws ServiceException {
        try {
            validate(nombre);
            if (repository.existsByActivoTrueAndNombre(nombre)) {
                throw new ServiceException(ServiceExceptionMessages.exists(Categoria.class, "nombre", nombre));
            }
            var categoria = Categoria.builder()
            .nombre(nombre)
            .build();
            repository.save(categoria);
            return categoria.getId();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public void update(UUID id, String nombre) throws ServiceException {
        try {
            validate(nombre);
            if (repository.existsByIdNotAndActivoTrueAndNombre(id, nombre)) {
                throw new ServiceException(ServiceExceptionMessages.exists(Categoria.class, "nombre", nombre));
            }
            var categoria = repository.findByIdAndActivoTrue(id)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Categoria.class)));
            categoria.setNombre(nombre);
            repository.save(categoria);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }

    }

    @Transactional
    public void delete(UUID id) throws ServiceException {
        try {
            var categoria = repository.findByIdAndActivoTrue(id)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Categoria.class)));
            categoria.setActivo(false);
            repository.save(categoria);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }

    }

    public Set<Categoria> getAll() throws ServiceException {
        try {
            return new HashSet<>(repository.findByActivoTrue());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    public Optional<Categoria> get(UUID id) throws ServiceException {
        try {
            return repository.findByIdAndActivoTrue(id);
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    public Optional<Categoria> get(String nombre) throws ServiceException {
        try {
            return repository.findByActivoTrueAndNombre(nombre);
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }
}
