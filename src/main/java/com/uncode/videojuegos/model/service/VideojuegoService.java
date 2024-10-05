package com.uncode.videojuegos.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.entity.Videojuego;
import com.uncode.videojuegos.model.repository.VideojuegoRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository repository;

    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    EstudioService estudioService;

    private final ExampleMatcher nombreActivoMatcher = ExampleMatcher.matching()
            .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("activo", ExampleMatcher.GenericPropertyMatchers.exact());

    private void validate(String name, float precio, short cantidad, String nombreCategoria, String nombreEstudio)
            throws ServiceException {
        if (name.isBlank())
            throw new ServiceException("El nombre no puede estar vacío");
        if (precio < 0)
            throw new ServiceException("El precio debe ser no negativo");
        if (cantidad < 0)
            throw new ServiceException("La cantidad debe ser no negativa");
        if (nombreCategoria.isBlank())
            throw new ServiceException("La categoría no puede estar vacía");
        if (nombreEstudio.isBlank())
            throw new ServiceException("El estudio no puede estar vacío");
    }

    public boolean existsByNombreActivo(String name) {
        return repository
                .exists(Example.of(Videojuego.builder().nombre(name).activo(true).build(), nombreActivoMatcher));
    }

    public void save(String name, String rutaimg, float precio, short cantidad, String descripcion, boolean oferta,
            LocalDate lanzamiento, String nombreCategoria, String nombreEstudio) throws ServiceException {
        try {
            validate(name, precio, cantidad, nombreCategoria, nombreEstudio);

            if (existsByNombreActivo(name))
                throw new ServiceException("El videojuego ya existe");

            repository.save(Videojuego.builder()
                    .nombre(name)
                    .rutaimg(rutaimg)
                    .precio(precio)
                    .cantidad(cantidad)
                    .descripcion(descripcion)
                    .oferta(oferta)
                    .lanzamiento(lanzamiento)
                    .categoria(categoriaService.findByNombreActivo(name)
                            .orElse(Categoria.builder().nombre(nombreCategoria).build()))
                    .estudio(estudioService.findByNombreActivo(name)
                            .orElse(Estudio.builder().nombre(nombreEstudio).build()))
                    .build());
        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar el videojuego");
        }
    }

    public void update(String name, String rutaimg, float precio, short cantidad, String descripcion, boolean oferta,
            LocalDate lanzamiento, String nombreCategoria, String nombreEstudio) throws ServiceException {
        try {
            validate(name, precio, cantidad, nombreCategoria, nombreEstudio);

            if (!existsByNombreActivo(name))
                throw new ServiceException("El videojuego no existe");

            repository.save(Videojuego.builder()
                    .nombre(name)
                    .rutaimg(rutaimg)
                    .precio(precio)
                    .cantidad(cantidad)
                    .descripcion(descripcion)
                    .oferta(oferta)
                    .lanzamiento(lanzamiento)
                    .categoria(categoriaService.findByNombreActivo(name)
                            .orElse(Categoria.builder().nombre(nombreCategoria).build()))
                    .estudio(estudioService.findByNombreActivo(name)
                            .orElse(Estudio.builder().nombre(nombreEstudio).build()))
                    .build());
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar el videojuego");
        }
    }

    public Optional<Videojuego> findByNombreActivo(String name) throws ServiceException {
        try {
            return repository
                    .findOne(Example.of(Videojuego.builder().nombre(name).build(), nombreActivoMatcher));
        } catch (Exception e) {
            throw new ServiceException("No se pudo encontrar el videojuego");
        }
    }

    public void delete(String name) throws ServiceException {
        try {
            findByNombreActivo(name).ifPresent((videojuego) -> {
                videojuego.setActivo(false);
                repository.save(videojuego);
            });
        } catch (Exception e) {
            throw new ServiceException("No se pudo eliminar el videojuego");
        }
    }

    public Set<Videojuego> findAll() throws ServiceException {
        try {
            return new HashSet<>(repository.findAll());
        } catch (Exception e) {
            throw new ServiceException("No se pudo encontrar los videojuegos");
        }
    }
}
