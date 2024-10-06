package com.uncode.videojuegos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.uncode.videojuegos.model.entity.Videojuego;
import com.uncode.videojuegos.model.repository.VideojuegoRepository;
import com.uncode.videojuegos.model.service.CategoriaService;
import com.uncode.videojuegos.model.service.EstudioService;
import com.uncode.videojuegos.model.service.VideojuegoService;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

class VideojuegoServiceTest {

    @InjectMocks
    private VideojuegoService service;

    @Mock
    private VideojuegoRepository repository;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private EstudioService estudioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private final String nombre = "videojuego";
    private final String rutaimg = "rutaimg";
    private final float precio = 12f;
    private final short cantidad = (short) 12;
    private final String descripcion = "descripcion";
    private final boolean oferta = false;
    private final LocalDate lanzamiento = LocalDate.now();
    private final String nombreCategoria = "categoria";
    private final String nombreEstudio = "estudio";

    @Test
    void testSave_NewVideojuego() throws ServiceException {
        when(repository.exists(any())).thenReturn(false);
        when(categoriaService.findByNombreActivo(nombreCategoria)).thenReturn(Optional.empty());
        when(estudioService.findByNombreActivo(nombreEstudio)).thenReturn(Optional.empty());
        when(repository.save(any(Videojuego.class))).thenReturn(Videojuego.builder().nombre(nombre).build());

        service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                nombreEstudio);

        verify(repository).save(any(Videojuego.class));
    }

    @Test
    void testSave_ExistingVideojuego() {
        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("El videojuego ya existe: " + nombre, exception.getMessage());
    }

    @Test
    void testSave_NegativePrecio() {
        var precio = -10.0f;

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("El precio debe ser no negativo", exception.getMessage());
    }

    @Test
    void testSave_NegativeCantidad() {
        var cantidad = (short) -5;

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("La cantidad debe ser no negativa", exception.getMessage());
    }

    @Test
    void testSave_BlankNombreCategoria() {
        var nombreCategoria = "    ";

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("La categoría no puede estar vacía", exception.getMessage());
    }

    @Test
    void testSave_BlankNombreEstudio() {
        var nombreEstudio = "    ";

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("El estudio no puede estar vacío", exception.getMessage());
    }

    @Test
    void testUpdate_NegativePrecio() {
        var precio = -10.0f;

        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("El precio debe ser no negativo", exception.getMessage());
    }

    @Test
    void testUpdate_NegativeCantidad() {
        var cantidad = (short) -5;

        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("La cantidad debe ser no negativa", exception.getMessage());
    }

    @Test
    void testUpdate_BlankNombreCategoria() {
        var nombreCategoria = "    ";

        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("La categoría no puede estar vacía", exception.getMessage());
    }

    @Test
    void testUpdate_BlankNombreEstudio() {
        var nombreEstudio = "    ";

        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("El estudio no puede estar vacío", exception.getMessage());
    }

    @Test
    void testSave_BlankNombre() {
        var nombre = "   ";

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                    nombreEstudio);
        });

        assertEquals("El nombre no puede estar vacío", exception.getMessage());
    }

    @Test
    void testUpdate_ExistingVideojuego() throws ServiceException {
        when(repository.exists(any())).thenReturn(true);
        when(categoriaService.findByNombreActivo(nombreCategoria)).thenReturn(Optional.empty());
        when(estudioService.findByNombreActivo(nombreEstudio)).thenReturn(Optional.empty());
        when(repository.save(any(Videojuego.class))).thenReturn(Videojuego.builder().nombre(nombre).build());

        service.update(nombre, rutaimg, precio, cantidad, descripcion, oferta, lanzamiento, nombreCategoria,
                nombreEstudio);

        verify(repository).save(any(Videojuego.class));
    }

    @Test
    void testUpdate_NonExistingVideojuego() {
        when(repository.exists(any())).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, "img/path", 10.0f, (short) 5, "desc", false, LocalDate.now(), "Action", "Studio A");
        });

        assertEquals("El videojuego no existe: " + nombre, exception.getMessage());
    }

    @Test
    void testFindByNombreActivo_ExistingVideojuego() throws ServiceException {
        when(repository.findOne(any())).thenReturn(Optional.of(Videojuego.builder().nombre(nombre).build()));

        var result = service.findByNombreActivo(nombre);

        assertTrue(result.isPresent());
        assertEquals(nombre, result.get().getNombre());
    }

    @Test
    void testFindByNombreActivo_NonExistingVideojuego() throws ServiceException {
        when(repository.findOne(any())).thenReturn(Optional.empty());

        var result = service.findByNombreActivo(nombre);

        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_ExistingVideojuego() throws ServiceException {
        var videojuego = Videojuego.builder().nombre(nombre).build();
        when(repository.findOne(any())).thenReturn(Optional.of(videojuego));

        service.delete(nombre);

        verify(repository).save(videojuego);
        assertFalse(videojuego.isActivo());
    }

    @Test
    void testDelete_NonExistingVideojuego() throws ServiceException {
        when(repository.findOne(any())).thenReturn(Optional.empty());

        service.delete(nombre);

        verify(repository, never()).save(any());
    }

    @Test
    void testFindAll() throws ServiceException {
        var videojuegos = Set.of(
                Videojuego.builder().nombre("videojuego 1").build(),
                Videojuego.builder().nombre("videojuego 2").build());

        when(repository.findAll()).thenReturn(new ArrayList<>(videojuegos));

        var result = service.findAll();

        assertEquals(2, result.size());
    }
}
