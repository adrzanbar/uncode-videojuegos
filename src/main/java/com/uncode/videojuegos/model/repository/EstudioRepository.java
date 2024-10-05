package com.uncode.videojuegos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uncode.videojuegos.model.entity.Estudio;

@Repository
public interface EstudioRepository extends JpaRepository<Estudio, String>{

}
