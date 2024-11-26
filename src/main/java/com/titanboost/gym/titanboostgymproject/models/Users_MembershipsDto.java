package com.titanboost.gym.titanboostgymproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para representar la relación entre un usuario y su plan de membresía.
 * Esta clase se utiliza para transferir los datos relevantes de la relación entre los usuarios y sus
 * membresías, sin exponer directamente las entidades de la base de datos.
 *
 * @author itsBaird
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users_MembershipsDto {

    /**
     * El identificador único del usuario.
     *
     * @see Users
     */
    private int user_id;  // ID del usuario

    /**
     * El identificador único del plan de membresía.
     *
     * @see MembershipPlans
     */
    private int membership_id;  // ID del plan de membresía

    /**
     * La fecha y hora en la que el usuario adquirió el plan de membresía.
     *
     * @see LocalDateTime
     */
    private LocalDateTime purchase_date;

    /**
     * La fecha y hora en la que el plan de membresía del usuario expirará.
     *
     * @see LocalDateTime
     */
    private LocalDateTime expiration_date;
}
