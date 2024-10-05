package com.uncode.videojuegos.model.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Videojuego {

    @Id
    @EqualsAndHashCode.Include
    private String nombre;
    private String rutaimg;
    private float precio;
    private short cantidad;
    private String descripcion;
    private boolean oferta;
    private LocalDate lanzamiento;
    @Builder.Default
    private boolean activo = true;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Categoria categoria;
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Estudio estudio;
}
