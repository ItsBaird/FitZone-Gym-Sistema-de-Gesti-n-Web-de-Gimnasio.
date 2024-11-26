package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representa los diferentes tipos de identificación que pueden ser utilizados por los usuarios del sistema.
 * Esta clase está mapeada a la tabla "identification_types" en la base de datos.
 */
@Entity
@Table(name = "identification_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentificationTypes {

    /**
     * Identificador único del tipo de identificación.
     * Se genera automáticamente al guardar la entidad en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identification_type_id;

    /**
     * Nombre del tipo de identificación.
     * Por ejemplo: "Cédula de ciudadanía", "Pasaporte", "Tarjeta de identidad".
     */
    private String name;

    /**
     * Lista de usuarios asociados a este tipo de identificación.
     * Relación de uno a muchos mapeada por el atributo "identification_type_id" en la entidad {@link Users}.
     */
    @OneToMany(mappedBy = "identification_type_id")
    private List<Users> users;
}