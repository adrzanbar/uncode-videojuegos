package com.uncode.videojuegos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.repository.EstudioRepository;
import com.uncode.videojuegos.model.service.EstudioService;
import com.uncode.videojuegos.model.service.exception.ServiceException;
import com.uncode.videojuegos.model.service.exception.ServiceExceptionMessages;

public class EstudioServiceTest {

    @Mock
    private EstudioRepository repository;

    @InjectMocks
    private EstudioService service;

    private UUID estudioId;
    private String nombre = "estudio";
    private String nuevoNombre = "nuevo nombre";
    private Estudio estudio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        estudio = Estudio.builder().id(UUID.randomUUID()).nombre(nombre).build();
    }

    @Test
    public void testSaveEstudio_Success() throws ServiceException {
        when(repository.existsByActivoTrueAndNombre(nombre)).thenReturn(false);
        service.create(nombre);

        verify(repository).save(any(Estudio.class));
    }

    @Test
    public void testSaveEstudio_AlreadyExists() {
        when(repository.existsByActivoTrueAndNombre(nombre)).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.create(nombre);
        });

        assertEquals(ServiceExceptionMessages.exists(Estudio.class, "nombre", nombre), exception.getMessage());
    }

    @Test
    public void testSaveEstudio_BlankName() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.create("    ");
        });

        assertEquals(ServiceExceptionMessages.blank(Estudio.class, "nombre"), exception.getMessage());
    }

    @Test
    public void testUpdateEstudio_Success() throws ServiceException {
        when(repository.existsByIdNotAndActivoTrueAndNombre(estudioId, nuevoNombre)).thenReturn(false);
        when(repository.findByIdAndActivoTrue(estudioId)).thenReturn(Optional.of(estudio));

        service.update(estudioId, nuevoNombre);

        assertEquals(nuevoNombre, estudio.getNombre());
        verify(repository).save(estudio);
    }

    @Test
    public void testUpdateEstudio_NotFound() {
        when(repository.findByIdAndActivoTrue(estudioId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(estudioId, nuevoNombre);
        });

        assertEquals(ServiceExceptionMessages.notFound(Estudio.class), exception.getMessage());
    }

    @Test
    public void testUpdateEstudio_AlreadyExists() {
        when(repository.existsByIdNotAndActivoTrueAndNombre(estudioId, nuevoNombre)).thenReturn(true);
        when(repository.findByIdAndActivoTrue(estudioId)).thenReturn(Optional.of(estudio));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(estudioId, nuevoNombre);
        });

        assertEquals(ServiceExceptionMessages.exists(Estudio.class, "nombre", nuevoNombre), exception.getMessage());
    }

    @Test
    public void testUpdateEstudio_BlankName() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.update(estudioId, "    ");
        });

        assertEquals(ServiceExceptionMessages.blank(Estudio.class, "nombre"), exception.getMessage());
    }

    @Test
    public void testDeleteEstudio_Success() throws ServiceException {
        when(repository.findByIdAndActivoTrue(estudioId)).thenReturn(Optional.of(estudio));

        service.delete(estudioId);

        assertFalse(estudio.isActivo());
        verify(repository).save(estudio);
    }

    @Test
    public void testDeleteEstudio_NotFound() {
        when(repository.findByIdAndActivoTrue(estudioId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            service.delete(estudioId);
        });

        assertEquals(ServiceExceptionMessages.notFound(Estudio.class), exception.getMessage());
    }

    @Test
    public void testGetAllCategories() throws ServiceException {
        when(repository.findByActivoTrue()).thenReturn(Set.of(estudio));

        var categories = service.getAll();

        assertEquals(1, categories.size());
        assertTrue(categories.contains(estudio));
    }

    @Test
    public void testGetEstudioById() throws ServiceException {
        when(repository.findByIdAndActivoTrue(estudioId)).thenReturn(Optional.of(estudio));

        var result = service.get(estudioId);

        assertTrue(result.isPresent());
        assertEquals(estudio, result.get());
    }

    @Test
    public void testGetEstudioByName() throws ServiceException {
        when(repository.findByActivoTrueAndNombre(nombre)).thenReturn(Optional.of(estudio));

        var result = service.get(nombre);

        assertTrue(result.isPresent());
        assertEquals(estudio, result.get());
    }
}
