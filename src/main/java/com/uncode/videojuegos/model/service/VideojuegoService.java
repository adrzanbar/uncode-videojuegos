package com.uncode.videojuegos.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.entity.Videojuego;
import com.uncode.videojuegos.model.repository.VideojuegoRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;
import com.uncode.videojuegos.model.service.exception.ServiceExceptionMessages;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
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

    private void validate(String nombre, String rutaimg, float precio, short cantidad, String descripcion)
            throws ServiceException {
        try {
            if (nombre.isBlank())
                throw new ServiceException(ServiceExceptionMessages.blank(Videojuego.class, "nombre"));
            if (precio < 0)
                throw new ServiceException(ServiceExceptionMessages.nonNegative("precio"));
            if (cantidad < 0)
                throw new ServiceException(ServiceExceptionMessages.nonNegative("cantidad"));
            if (rutaimg.isBlank())
                throw new ServiceException(ServiceExceptionMessages.blank(Videojuego.class, "rutaimg"));
            if (descripcion.isBlank())
                throw new ServiceException(ServiceExceptionMessages.blank(Videojuego.class, "descripcion"));
        } catch (ServiceException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new ServiceException(ServiceExceptionMessages.$null(Videojuego.class, "nombre"));
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public UUID create(String nombre, String rutaimg, float precio, short cantidad, String descripcion, boolean oferta,
            LocalDate lanzamiento, UUID categoriaId, UUID estudioId) throws ServiceException {
        try {
            validate(nombre, rutaimg, precio, cantidad, descripcion);
            if (repository.existsByActivoTrueAndNombre(nombre)) {
                throw new ServiceException(ServiceExceptionMessages.exists(Videojuego.class, "nombre", nombre));
            }
            var videojuego = Videojuego.builder()
                    .nombre(nombre)
                    .rutaimg(rutaimg)
                    .precio(precio)
                    .cantidad(cantidad)
                    .descripcion(descripcion)
                    .oferta(oferta)
                    .lanzamiento(lanzamiento)
                    .categoria(categoriaService.get(categoriaId)
                            .orElseThrow(
                                    () -> new ServiceException(ServiceExceptionMessages.notFound(Categoria.class))))
                    .estudio(estudioService.get(estudioId)
                            .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Estudio.class))))
                    .build();
            repository.save(videojuego);
            return videojuego.getId();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public void update(UUID id, String nombre, String rutaimg, float precio, short cantidad, String descripcion,
            boolean oferta,
            LocalDate lanzamiento, UUID categoriaId, UUID estudioId) throws ServiceException {
        try {
            validate(nombre, rutaimg, precio, cantidad, descripcion);
            if (repository.existsByIdNotAndActivoTrueAndNombre(id, nombre)) {
                throw new ServiceException(ServiceExceptionMessages.exists(Videojuego.class, "nombre", nombre));
            }
            var videojuego = repository.findByIdAndActivoTrue(id)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Videojuego.class)));
            videojuego.setNombre(nombre);
            videojuego.setRutaimg(rutaimg);
            videojuego.setPrecio(precio);
            videojuego.setCantidad(cantidad);
            videojuego.setDescripcion(descripcion);
            videojuego.setOferta(oferta);
            videojuego.setLanzamiento(lanzamiento);
            videojuego.setCategoria(categoriaService.get(categoriaId)
                    .orElseThrow(
                            () -> new ServiceException(ServiceExceptionMessages.notFound(Categoria.class))));
            videojuego.setEstudio(estudioService.get(estudioId)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Estudio.class))));
            repository.save(videojuego);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public void delete(UUID id) throws ServiceException {
        try {
            var videojuego = repository.findByIdAndActivoTrue(id)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Videojuego.class)));
            videojuego.setActivo(false);
            repository.save(videojuego);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    public Set<Videojuego> getAll() throws ServiceException {
        try {
            return new HashSet<>(repository.findByActivoTrue());
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    public Optional<Videojuego> get(UUID id) throws ServiceException {
        try {
            return repository.findByIdAndActivoTrue(id);
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }

    }
}