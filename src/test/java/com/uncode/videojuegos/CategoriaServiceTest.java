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

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.repository.CategoriaRepository;
import com.uncode.videojuegos.model.service.CategoriaService;
import com.uncode.videojuegos.model.service.exception.ServiceException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService service;

    @Mock
    private CategoriaRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_NewCategoria() throws ServiceException {
        var nombre = "categoria";

        when(repository.exists(any())).thenReturn(false);
        when(repository.save(any(Categoria.class))).thenReturn(Categoria.builder().nombre(nombre).build());

        service.save(nombre);

        verify(repository).save(any(Categoria.class));
    }

    @Test
    void testSave_ExistingCategoria() {
        var nombre = "categoria";

        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre);
        });

        assertEquals("La categoría ya existe: " + nombre, exception.getMessage());
    }

    @Test
    void testSave_BlankNombre() {
        var nombre = "    ";

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre);
        });

        assertEquals("El nombre no puede estar vacío", exception.getMessage());
    }

    @Test
    void testUpdate_ExistingCategoria() throws ServiceException {
        var nombre = "categoria";

        when(repository.exists(any())).thenReturn(true);
        when(repository.save(any())).thenReturn(Categoria.builder().nombre(nombre).build());

        service.update(nombre, true);

        verify(repository).save(any(Categoria.class));
    }

    @Test
    void testUpdate_NonExistingCategoria() {
        var nombre = "categoria";

        when(repository.exists(any())).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, true);
        });

        assertEquals("La categoría no existe: " + nombre, exception.getMessage());
    }

    @Test
    void testUpdate_BlankName() {
        var blankName = "   ";

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(blankName, true);
        });

        assertEquals("El nombre no puede estar vacío", exception.getMessage());
    }

    @Test
    void testFindByNombreActivo_ExistingCategoria() throws ServiceException {
        var nombre = "categoria";
        var categoria = Categoria.builder().nombre(nombre).build();

        when(repository.findOne(any())).thenReturn(Optional.of(categoria));

        var result = service.findByNombreActivo(nombre);

        assertTrue(result.isPresent());
        assertEquals(nombre, result.get().getNombre());
    }

    @Test
    void testFindByNombreActivo_NonExistingCategoria() throws ServiceException {
        var nombre = "categoria";

        when(repository.findOne(any())).thenReturn(Optional.empty());

        var result = service.findByNombreActivo(nombre);

        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_ExistingCategoria() throws ServiceException {
        var nombre = "categoria";
        var categoria = Categoria.builder().nombre(nombre).build();

        when(repository.findOne(any())).thenReturn(Optional.of(categoria));

        service.delete(nombre);

        verify(repository).save(categoria);
        assertFalse(categoria.isActivo());
    }

    @Test
    void testDelete_NonExistingCategoria() throws ServiceException {
        var nombre = "categoria";

        when(repository.findOne(any())).thenReturn(Optional.empty());

        service.delete(nombre);

        verify(repository, never()).save(any());
    }

    @Test
    void testFindAll() throws ServiceException {
        var categorias = Set.of(
                Categoria.builder().nombre("categoria 1").build(),
                Categoria.builder().nombre("categoria 2").build());

        when(repository.findAll()).thenReturn(new ArrayList<>(categorias));

        var result = service.findAll();

        assertEquals(2, result.size());
    }
}