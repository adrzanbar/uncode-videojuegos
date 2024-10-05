package com.uncode.videojuegos.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Estudio {

    @Id
    @EqualsAndHashCode.Include
    private String nombre;
    @Builder.Default
    private boolean activo = true;
}
