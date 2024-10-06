package com.uncode.videojuegos.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.entity.Videojuego;
import com.uncode.videojuegos.model.repository.VideojuegoRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository repository;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private EstudioService estudioService;

    private void validate(String nombre, float precio, short cantidad)
            throws ServiceException {
        if (nombre.isBlank())
            throw new ServiceException("El nombre no puede estar vacío");
        if (precio < 0)
            throw new ServiceException("El precio debe ser no negativo");
        if (cantidad < 0)
            throw new ServiceException("La cantidad debe ser no negativa");

    }

    @Transactional
    public void save(String nombre, float precio, short cantidad, String nombreCategoria, String nombreEstudio)
            throws ServiceException {
        validate(nombre, precio, cantidad);
        categoriaService.validate(nombreCategoria);
        estudioService.validate(nombreEstudio);
        if (repository.existsByActivoTrueAndNombre(nombre)) {
            throw new ServiceException("El videojuego ya existe: " + nombre);
        }
        repository.save(Videojuego.builder()
                .nombre(nombre)
                .precio(precio)
                .cantidad(cantidad)
                .categoria(categoriaService.get(nombreCategoria)
                        .orElseGet(() -> Categoria.builder().nombre(nombreCategoria).build()))
                .estudio(estudioService.get(nombreEstudio)
                        .orElseGet(() -> Estudio.builder().nombre(nombreEstudio).build()))
                .build());
    }

    @Transactional
    public void update(UUID id, String nombre, float precio, short cantidad, String nombreCategoria,
            String nombreEstudio) throws ServiceException {
        validate(nombre, precio, cantidad);
        categoriaService.validate(nombreCategoria);
        estudioService.validate(nombreEstudio);
        if (repository.existsByIdNotAndActivoTrueAndNombre(id, nombre)) {
            throw new ServiceException("El videojuego ya existe: " + nombre);
        }
        var videojuego = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ServiceException("No se pudo encontrar el videojuego: " + id));
        videojuego.setNombre(nombre);
        videojuego.setPrecio(precio);
        videojuego.setCantidad(cantidad);
        videojuego.setCategoria(categoriaService.get(nombreCategoria)
                .orElseGet(() -> Categoria.builder().nombre(nombreCategoria).build()));
        videojuego.setEstudio(estudioService.get(nombreEstudio)
                .orElseGet(() -> Estudio.builder().nombre(nombreEstudio).build()));
        repository.save(videojuego);
    }

    @Transactional
    public void delete(UUID id) throws ServiceException {
        var videojuego = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ServiceException("No se pudo encontrar el videojuego: " + id));
        videojuego.setActivo(false);
        repository.save(videojuego);
    }

    public Set<Videojuego> getAll() throws ServiceException {
        return new HashSet<>(repository.findByActivoTrue());
    }

    public Optional<Videojuego> get(UUID id) throws ServiceException {
        return repository.findByIdAndActivoTrue(id);
    }
}