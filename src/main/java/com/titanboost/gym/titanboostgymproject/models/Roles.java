package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representa los roles dentro del sistema.
 * Cada rol define un conjunto de permisos o responsabilidades asignables a los usuarios.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Roles {

    /**
     * Identificador único del rol.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int role_id;

    /**
     * Nombre del rol.
     * Por ejemplo, "ADMIN" o "USER".
     */
    private String name;

    /**
     * Lista de usuarios asociados a este rol.
     * La relación es de muchos a muchos y está mapeada por el atributo "roles" en la entidad {@link Users}.
     */
    @ManyToMany(mappedBy = "roles")
    private List<Users> users;
}
