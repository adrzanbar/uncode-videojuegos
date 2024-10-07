package com.uncode.videojuegos.model.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.repository.EstudioRepository;
import com.uncode.videojuegos.model.service.exception.ServiceException;
import com.uncode.videojuegos.model.service.exception.ServiceExceptionMessages;

import jakarta.transaction.Transactional;

@Service
public class EstudioService {

    @Autowired
    private EstudioRepository repository;

    public void validate(String nombre) throws ServiceException {
        try {
            if (nombre.isBlank()) {
                throw new ServiceException(ServiceExceptionMessages.blank(Estudio.class, "nombre"));
            }
        } catch (ServiceException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new ServiceException(ServiceExceptionMessages.$null(Estudio.class, "nombre"));
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public UUID create(String nombre) throws ServiceException {
        try {
            validate(nombre);
            if (repository.existsByActivoTrueAndNombre(nombre)) {
                throw new ServiceException(ServiceExceptionMessages.exists(Estudio.class, "nombre", nombre));
            }
            var estudio = Estudio.builder()
                    .nombre(nombre)
                    .build();
            repository.save(estudio);
            return estudio.getId();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    @Transactional
    public void update(UUID id, String nombre) throws ServiceException {
        try {
            validate(nombre);
            if (repository.existsByIdNotAndActivoTrueAndNombre(id, nombre)) {
                throw new ServiceException(ServiceExceptionMessages.exists(Estudio.class, "nombre", nombre));
            }
            var estudio = repository.findByIdAndActivoTrue(id)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Estudio.class)));
            estudio.setNombre(nombre);
            repository.save(estudio);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }

    }

    @Transactional
    public void delete(UUID id) throws ServiceException {
        try {
            var estudio = repository.findByIdAndActivoTrue(id)
                    .orElseThrow(() -> new ServiceException(ServiceExceptionMessages.notFound(Estudio.class)));
            estudio.setActivo(false);
            repository.save(estudio);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }

    }

    public Set<Estudio> getAll() throws ServiceException {
        try {
            return new HashSet<>(repository.findByActivoTrue());
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    public Optional<Estudio> get(UUID id) throws ServiceException {
        try {
            return repository.findByIdAndActivoTrue(id);
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }

    public Optional<Estudio> get(String nombre) throws ServiceException {
        try {
            return repository.findByActivoTrueAndNombre(nombre);
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionMessages.ANY);
        }
    }
}
