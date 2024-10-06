package com.uncode.videojuegos.model.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uncode.videojuegos.model.entity.Videojuego;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, UUID>{

    boolean existsByActivoTrueAndNombre(String name);

    boolean existsByIdNotAndActivoTrueAndNombre(UUID id, String nombre);

    Optional<Videojuego> findByIdAndActivoTrue(UUID id);

    Set<Videojuego> findByActivoTrue();

}
