package com.uncode.videojuegos.model.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.repository.CategoriaRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    private final ExampleMatcher nombreActivoMatcher = ExampleMatcher.matching()
            .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("activo", ExampleMatcher.GenericPropertyMatchers.exact());

    private void validate(String nombre) throws ServiceException {
        if (nombre.isBlank()) {
            throw new ServiceException("El nombre no puede estar vacío");
        }
    }

    public boolean existsByNombreActivo(String nombre) {
        return repository.exists(Example.of(Categoria.builder().nombre(nombre).build(), nombreActivoMatcher));
    }

    public void save(String nombre) throws ServiceException {
        try {
            validate(nombre);

            if (existsByNombreActivo(nombre)) {
                throw new ServiceException("La categoría ya existe: " + nombre);
            }

            repository.save(Categoria.builder()
                    .nombre(nombre)
                    .build());
        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar la categoría");
        }
    }

    public void update(String nombre, boolean activo) throws ServiceException {
        try {
            validate(nombre);

            if (!existsByNombreActivo(nombre)) {
                throw new ServiceException("La categoría no existe: " + nombre);
            }

            repository.save(Categoria.builder().nombre(nombre).build());
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar la categoría");
        }
    }

    public Optional<Categoria> findByNombreActivo(String nombre) throws ServiceException {
        try {
            return repository.findOne(Example.of(Categoria.builder().nombre(nombre).activo(true).build(), nombreActivoMatcher));
        } catch (Exception e) {
            throw new ServiceException("No se pudo encontrar la categoría: " + nombre);
        }
    }

    public void delete(String nombre) throws ServiceException {
        try {
            findByNombreActivo(nombre).ifPresent(categoria -> {
                categoria.setActivo(false);
                repository.save(categoria);
            });
        } catch (Exception e) {
            throw new ServiceException("No se pudo eliminar la categoría: " + nombre);
        }
    }

    public Set<Categoria> findAll() throws ServiceException {
        try {
            return new HashSet<>(repository.findAll());
        } catch (Exception e) {
            throw new ServiceException("No se pudo encontrar las categorías");
        }
    }
}
