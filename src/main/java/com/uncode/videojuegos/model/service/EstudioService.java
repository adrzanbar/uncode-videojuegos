package com.uncode.videojuegos.model.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.repository.EstudioRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import jakarta.transaction.Transactional;

@Service
public class EstudioService {

    @Autowired
    private EstudioRepository repository;

    private final ExampleMatcher nombreActivoMatcher = ExampleMatcher.matching()
            .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("activo", ExampleMatcher.GenericPropertyMatchers.exact());

    private void validate(String nombre) throws ServiceException {
        if (nombre.isBlank()) {
            throw new ServiceException("El nombre no puede estar vacío");
        }
    }

    public boolean existsByNombreActivo(String nombre) {
        return repository.exists(Example.of(Estudio.builder().nombre(nombre).build(), nombreActivoMatcher));
    }

    @Transactional
    public void save(String nombre) throws ServiceException {
        validate(nombre);
        if (existsByNombreActivo(nombre)) {
            throw new ServiceException("El estudio ya existe: " + nombre);
        }
        try {
            repository.save(Estudio.builder()
                    .nombre(nombre)
                    .build());
        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar el estudio");
        }
    }

    @Transactional
    public void update(String nombre, boolean activo) throws ServiceException {
        validate(nombre);
        if (!existsByNombreActivo(nombre)) {
            throw new ServiceException("El estudio no existe: " + nombre);
        }
        try {
            repository.save(Estudio.builder().nombre(nombre).build());
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar el estudio");
        }
    }

    public Optional<Estudio> findByNombreActivo(String nombre) throws ServiceException {
        try {
            return repository
                    .findOne(Example.of(Estudio.builder().nombre(nombre).build(), nombreActivoMatcher));
        } catch (Exception e) {
            throw new ServiceException("No se pudo encontrar el estudio: " + nombre);
        }
    }

    @Transactional
    public void delete(String nombre) throws ServiceException {
        try {
            findByNombreActivo(nombre).ifPresent(estudio -> {
                estudio.setActivo(false);
                repository.save(estudio);
            });
        } catch (Exception e) {
            throw new ServiceException("No se pudo eliminar el estudio: " + nombre);
        }
    }

    public Set<Estudio> findAll() throws ServiceException {
        try {
            return new HashSet<>(repository.findAll());
        } catch (Exception e) {
            throw new ServiceException("No se pudo encontrar los estudios");
        }
    }
}
