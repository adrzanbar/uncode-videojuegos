package com.uncode.videojuegos.model.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uncode.videojuegos.model.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    public Set<Categoria> findByActivoTrue();
    public boolean existsByActivoTrueAndNombre(String nombre);    
    public boolean existsByIdNotAndActivoTrueAndNombre(UUID id, String nombre);
    public Optional<Categoria> findByIdAndActivoTrue(UUID id);
    public Optional<Categoria> findByActivoTrueAndNombre(String nombre);
}
