package com.uncode.videojuegos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uncode.videojuegos.model.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {

}
