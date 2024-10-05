package com.uncode.videojuegos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uncode.videojuegos.model.entity.Videojuego;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, String>{

}
