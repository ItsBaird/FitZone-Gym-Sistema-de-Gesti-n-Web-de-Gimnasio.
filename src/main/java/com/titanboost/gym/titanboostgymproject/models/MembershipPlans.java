package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa los planes de membresía ofrecidos por el sistema.
 * Cada plan de membresía está asociado a una sede específica y tiene atributos como nombre, descripción, precio y duración.
 */

@Entity
@Table(name = "membership_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlans {

    /**
     * Identificador único del plan de membresía.
     * Se genera automáticamente al guardar la entidad en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int membership_id;

    /**
     * Nombre del plan de membresía.
     * Debe tener un máximo de 255 caracteres y no puede ser nulo.
     */
    @NotBlank(message = "El nombre no puede ser nulo.")
    @Size(max = 255, message = "El nombre no debe exceder los 255 caracteres.")
    private String name;

    /**
     * Descripción detallada del plan de membresía.
     * Debe tener al menos 10 caracteres y no puede ser nula.
     */
    @NotBlank(message = "La descripción no puede ser nula.")
    @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres.")
    private String description;

    /**
     * Precio del plan de membresía.
     * Debe tener hasta 8 dígitos enteros y 2 decimales. No puede ser nulo.
     */
    @NotNull(message = "El precio no puede ser nulo.")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener hasta 8 dígitos enteros y 2 decimales.")
    private BigDecimal price;


    /**
     * URL de la imagen asociada al plan de membresía.
     */
    private String url_image;

    /**
     * Duración del plan de membresía en días.
     * Debe ser al menos 1 día y no puede ser nula.
     */
    @NotNull(message = "La duración no puede ser nula.")
    @Min(value = 1, message = "La duración debe ser al menos 1 día.")
    private int duration;

    /**
     * Sede asociada al plan de membresía.
     * No puede ser nula y está relacionada con la entidad {@link Branches}.
     */
    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "branch_id")
    @NotNull(message = "Debe seleccionar una sede.")
    @ToString.Exclude
    private Branches branch_id;

    /**
     * Estado del plan de membresía.
     * Indica si el plan está habilitado o deshabilitado.
     */
    private boolean enabled;

    /**
     * Conjunto de usuarios asociados al plan de membresía.
     * Relación de uno a muchos con la entidad {@link Users_Memberships}.
     * La eliminación de un plan de membresía elimina también las asociaciones relacionadas.
     */
    @OneToMany(mappedBy = "membership", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Users_Memberships> users = new HashSet<>();
}
