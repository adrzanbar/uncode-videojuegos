package com.uncode.videojuegos.model.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uncode.videojuegos.model.entity.Estudio;

@Repository
public interface EstudioRepository extends JpaRepository<Estudio, UUID>{

    boolean existsByActivoTrueAndNombre(String nombre);

    boolean existsByIdNotAndActivoTrueAndNombre(UUID id, String nombre);

    Optional<Estudio> findByIdAndActivoTrue(UUID id);

    Set<Estudio> findByActivoTrue();

    Optional<Estudio> findByActivoTrueAndNombre(String nombre);

}
