package com.uncode.videojuegos.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.entity.Videojuego;
import com.uncode.videojuegos.model.repository.VideojuegoRepository;
import com.uncode.videojuegos.model.service.CategoriaService;
import com.uncode.videojuegos.model.service.EstudioService;
import com.uncode.videojuegos.model.service.VideojuegoService;
import com.uncode.videojuegos.model.service.exception.ServiceException;
import com.uncode.videojuegos.model.service.exception.ServiceExceptionMessages;

public class VideojuegoServiceTest {

    @Mock
    private VideojuegoRepository repository;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private EstudioService estudioService;

    @InjectMocks
    private VideojuegoService service;

    private UUID videojuegoId;
    private UUID categoriaId;
    private UUID estudioId;
    private Videojuego videojuego;
    private String nombre = "videojuego";
    private String rutaimg = "image/path.jpg";
    private float precio = 49.99f;
    private short cantidad = 10;
    private String descripcion = "descripcion";
    private boolean oferta = false;
    private LocalDate lanzamiento = LocalDate.now();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        videojuegoId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
        estudioId = UUID.randomUUID();
        videojuego = Videojuego.builder()
                .id(videojuegoId)
                .nombre(nombre)
                .rutaimg(rutaimg)
                .precio(precio)
                .cantidad(cantidad)
                .descripcion(descripcion)
                .oferta(oferta)
                .lanzamiento(lanzamiento)
                .build();
    }

    @Test
    public void testSaveVideojuego_Success() throws ServiceException {
        when(repository.existsByActivoTrueAndNombre(nombre)).thenReturn(false);
        when(categoriaService.get(categoriaId)).thenReturn(Optional.of(Categoria.builder().build()));
        when(estudioService.get(estudioId)).thenReturn(Optional.of(Estudio.builder().build()));

        service.create(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, categoriaId, estudioId);

        verify(repository).save(any(Videojuego.class));
    }

    @Test
    public void testSaveVideojuego_AlreadyExists() {
        when(repository.existsByActivoTrueAndNombre(nombre)).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.create(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, categoriaId, estudioId);
        });

        assertEquals(ServiceExceptionMessages.exists(Videojuego.class, "nombre", nombre), exception.getMessage());
    }

    @Test
    public void testSaveVideojuego_BlankName() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.create("   ", rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, categoriaId, estudioId);
        });

        assertEquals(ServiceExceptionMessages.blank(Videojuego.class, "nombre"), exception.getMessage());
    }

    @Test
    public void testUpdateVideojuego_Success() throws ServiceException {
        when(repository.existsByIdNotAndActivoTrueAndNombre(videojuegoId, nombre)).thenReturn(false);
        when(repository.findByIdAndActivoTrue(videojuegoId)).thenReturn(Optional.of(videojuego));
        when(categoriaService.get(categoriaId)).thenReturn(Optional.of(Categoria.builder().build()));
        when(estudioService.get(estudioId)).thenReturn(Optional.of(Estudio.builder().build()));

        service.update(videojuegoId, nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, categoriaId, estudioId);

        assertEquals(nombre, videojuego.getNombre());
        assertEquals(rutaimg, videojuego.getRutaimg());
        assertEquals(precio, videojuego.getPrecio());
        assertEquals(cantidad, videojuego.getCantidad());
        assertEquals(descripcion, videojuego.getDescripcion());
        assertEquals(oferta, videojuego.isOferta());
        assertEquals(lanzamiento, videojuego.getLanzamiento());
        verify(repository).save(videojuego);
    }

    @Test
    public void testUpdateVideojuego_NotFound() {
        when(repository.findByIdAndActivoTrue(videojuegoId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(videojuegoId, nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, categoriaId, estudioId);
        });

        assertEquals(ServiceExceptionMessages.notFound(Videojuego.class), exception.getMessage());
    }

    @Test
    public void testDeleteVideojuego_Success() throws ServiceException {
        when(repository.findByIdAndActivoTrue(videojuegoId)).thenReturn(Optional.of(videojuego));

        service.delete(videojuegoId);

        assertFalse(videojuego.isActivo());
        verify(repository).save(videojuego);
    }

    @Test
    public void testDeleteVideojuego_NotFound() {
        when(repository.findByIdAndActivoTrue(videojuegoId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.delete(videojuegoId);
        });

        assertEquals(ServiceExceptionMessages.notFound(Videojuego.class), exception.getMessage());
    }

    @Test
    public void testGetAllVideojuegos() throws ServiceException {
        when(repository.findByActivoTrue()).thenReturn(Set.of(videojuego));

        var videojuegos = service.getAll();

        assertEquals(1, videojuegos.size());
        assertTrue(videojuegos.contains(videojuego));
    }

    @Test
    public void testGetVideojuegoById() throws ServiceException {
        when(repository.findByIdAndActivoTrue(videojuegoId)).thenReturn(Optional.of(videojuego));

        var result = service.get(videojuegoId);

        assertTrue(result.isPresent());
        assertEquals(videojuego, result.get());
    }
}
