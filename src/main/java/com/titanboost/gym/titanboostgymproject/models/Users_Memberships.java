package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa la relación entre los usuarios y los planes de membresía.
 * Esta clase almacena la información sobre la membresía adquirida por un usuario,
 * incluyendo detalles sobre la fecha de compra, fecha de expiración y el estado de la membresía.
 */
@Entity
@Table(name = "users_memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users_Memberships {

    /**
     * Identificador único de la relación entre un usuario y un plan de membresía.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_membership_id;

    /**
     * Usuario que adquirió la membresía.
     * Relación de muchos a uno con la entidad {@link Users}.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    /**
     * Plan de membresía adquirido por el usuario.
     * Relación de muchos a uno con la entidad {@link MembershipPlans}.
     */
    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = false)
    private MembershipPlans membership;

    /**
     * Fecha en que el usuario adquirió la membresía.
     * No puede ser nula.
     */
    @Column(nullable = false)
    private LocalDateTime purchase_date;

    /**
     * Fecha de expiración de la membresía.
     * No puede ser nula.
     */
    @Column(nullable = false)
    private LocalDateTime expiration_date;

    /**
     * Estado de la membresía, indicando si está habilitada o deshabilitada.
     * No puede ser nula.
     */
    @Column(nullable = false)
    private boolean enabled;
}
