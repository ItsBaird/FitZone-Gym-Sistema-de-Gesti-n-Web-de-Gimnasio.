package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Representa una sede del gimnasio, que incluye información como su nombre, dirección, teléfono y correo electrónico.
 * Esta clase está mapeada a la tabla "branches" en la base de datos.
 */
@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branches {

    /**
     * Identificador único de la sede.
     * Se genera automáticamente al guardar la entidad en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int branch_id;

    /**
     * Nombre de la sede.
     */
    private String name;

    /**
     * Dirección física de la sede.
     */
    private String address;

    /**
     * Número de teléfono de contacto de la sede.
     */
    private String phone;

    /**
     * Dirección de correo electrónico de la sede.
     */
    private String email;

    /**
     * Lista de planes de membresía asociados a la sede.
     * Se establece la relación de uno a muchos, mapeada por el atributo "branch_id" en la entidad {@link MembershipPlans}.
     */
    @OneToMany(mappedBy = "branch_id")
    @ToString.Exclude
    private List<MembershipPlans> membershipPlans;
}