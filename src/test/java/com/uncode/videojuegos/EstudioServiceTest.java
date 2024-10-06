package com.uncode.videojuegos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.repository.EstudioRepository;
import com.uncode.videojuegos.model.service.EstudioService;
import com.uncode.videojuegos.model.service.exception.ServiceException;

class EstudioServiceTest {

    @InjectMocks
    private EstudioService service;

    @Mock
    private EstudioRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_NewEstudio() throws ServiceException {
        var nombre = "estudio";

        when(repository.exists(any())).thenReturn(false);
        when(repository.save(any(Estudio.class))).thenReturn(Estudio.builder().nombre(nombre).build());

        service.save(nombre);

        verify(repository).save(any(Estudio.class));
    }

    @Test
    void testSave_ExistingEstudio() {
        var nombre = "estudio";

        when(repository.exists(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.save(nombre);
        });

        assertEquals("El estudio ya existe: " + nombre, exception.getMessage());
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
    void testUpdate_ExistingEstudio() throws ServiceException {
        var nombre = "estudio";

        when(repository.exists(any())).thenReturn(true);
        when(repository.save(any())).thenReturn(Estudio.builder().nombre(nombre).build());

        service.update(nombre, true);

        verify(repository).save(any(Estudio.class));
    }

    @Test
    void testUpdate_NonExistingEstudio() {
        var nombre = "estudio";

        when(repository.exists(any())).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(nombre, true);
        });

        assertEquals("El estudio no existe: " + nombre, exception.getMessage());
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
    void testFindByNombreActivo_ExistingEstudio() throws ServiceException {
        var nombre = "estudio";
        var estudio = Estudio.builder().nombre(nombre).build();

        when(repository.findOne(any())).thenReturn(Optional.of(estudio));

        var result = service.findByNombreActivo(nombre);

        assertTrue(result.isPresent());
        assertEquals(nombre, result.get().getNombre());
    }

    @Test
    void testFindByNombreActivo_NonExistingEstudio() throws ServiceException {
        var nombre = "estudio";

        when(repository.findOne(any())).thenReturn(Optional.empty());

        var result = service.findByNombreActivo(nombre);

        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_ExistingEstudio() throws ServiceException {
        var nombre = "estudio";
        var estudio = Estudio.builder().nombre(nombre).build();

        when(repository.findOne(any())).thenReturn(Optional.of(estudio));

        service.delete(nombre);

        verify(repository).save(estudio);
        assertFalse(estudio.isActivo());
    }

    @Test
    void testDelete_NonExistingEstudio() throws ServiceException {
        var nombre = "estudio";

        when(repository.findOne(any())).thenReturn(Optional.empty());

        service.delete(nombre);

        verify(repository, never()).save(any());
    }

    @Test
    void testFindAll() throws ServiceException {
        var estudios = Set.of(
                Estudio.builder().nombre("estudio 1").build(),
                Estudio.builder().nombre("estudio 2").build());

        when(repository.findAll()).thenReturn(new ArrayList<>(estudios));

        var result = service.findAll();

        assertEquals(2, result.size());
    }
}
