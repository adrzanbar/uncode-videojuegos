package com.uncode.videojuegos.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.repository.CategoriaRepository;
import com.uncode.videojuegos.model.service.CategoriaService;
import com.uncode.videojuegos.model.service.exception.ServiceException;
import com.uncode.videojuegos.model.service.exception.ServiceExceptionMessages;

public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService categoriaService;

    private UUID categoriaId;
    private String nombre = "categoria";
    private String nuevoNombre = "nuevo nombre";
    private Categoria categoria;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoriaId = UUID.randomUUID();
        categoria = Categoria.builder().id(categoriaId).nombre(nombre).build();
    }

    @Test
    public void testSaveCategoria_Success() throws ServiceException {
        when(repository.existsByActivoTrueAndNombre(nombre)).thenReturn(false);
        categoriaService.save(nombre);

        verify(repository).save(any(Categoria.class));
    }

    @Test
    public void testSaveCategoria_AlreadyExists() {
        when(repository.existsByActivoTrueAndNombre(nombre)).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            categoriaService.save(nombre);
        });

        assertEquals(ServiceExceptionMessages.exists(Categoria.class, "nombre", nombre), exception.getMessage());
    }

    @Test
    public void testSaveCategoria_BlankName() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            categoriaService.save("    ");
        });

        assertEquals(ServiceExceptionMessages.blank(Categoria.class, "nombre"), exception.getMessage());
    }

    @Test
    public void testUpdateCategoria_Success() throws ServiceException {
        when(repository.existsByIdNotAndActivoTrueAndNombre(categoriaId, nuevoNombre)).thenReturn(false);
        when(repository.findByIdAndActivoTrue(categoriaId)).thenReturn(Optional.of(categoria));

        categoriaService.update(categoriaId, nuevoNombre);

        assertEquals(nuevoNombre, categoria.getNombre());
        verify(repository).save(categoria);
    }

    @Test
    public void testUpdateCategoria_NotFound() {
        when(repository.findByIdAndActivoTrue(categoriaId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            categoriaService.update(categoriaId, nuevoNombre);
        });

        assertEquals(ServiceExceptionMessages.notFound(Categoria.class), exception.getMessage());
    }

    @Test
    public void testUpdateCategoria_AlreadyExists() {
        when(repository.existsByIdNotAndActivoTrueAndNombre(categoriaId, nuevoNombre)).thenReturn(true);
        when(repository.findByIdAndActivoTrue(categoriaId)).thenReturn(Optional.of(categoria));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            categoriaService.update(categoriaId, nuevoNombre);
        });

        assertEquals(ServiceExceptionMessages.exists(Categoria.class, "nombre", nuevoNombre), exception.getMessage());
    }

    @Test
    public void testUpdateCategoria_BlankName() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            categoriaService.update(categoriaId, "    ");
        });

        assertEquals(ServiceExceptionMessages.blank(Categoria.class, "nombre"), exception.getMessage());
    }

    @Test
    public void testDeleteCategoria_Success() throws ServiceException {
        when(repository.findByIdAndActivoTrue(categoriaId)).thenReturn(Optional.of(categoria));

        categoriaService.delete(categoriaId);

        assertFalse(categoria.isActivo());
        verify(repository).save(categoria);
    }

    @Test
    public void testDeleteCategoria_NotFound() {
        when(repository.findByIdAndActivoTrue(categoriaId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            categoriaService.delete(categoriaId);
        });

        assertEquals(ServiceExceptionMessages.notFound(Categoria.class), exception.getMessage());
    }

    @Test
    public void testGetAllCategories() throws ServiceException {
        when(repository.findByActivoTrue()).thenReturn(Set.of(categoria));

        var categories = categoriaService.getAll();

        assertEquals(1, categories.size());
        assertTrue(categories.contains(categoria));
    }

    @Test
    public void testGetCategoriaById() throws ServiceException {
        when(repository.findByIdAndActivoTrue(categoriaId)).thenReturn(Optional.of(categoria));

        var result = categoriaService.get(categoriaId);

        assertTrue(result.isPresent());
        assertEquals(categoria, result.get());
    }

    @Test
    public void testGetCategoriaByName() throws ServiceException {
        when(repository.findByActivoTrueAndNombre(nombre)).thenReturn(Optional.of(categoria));

        var result = categoriaService.get(nombre);

        assertTrue(result.isPresent());
        assertEquals(categoria, result.get());
    }
}
