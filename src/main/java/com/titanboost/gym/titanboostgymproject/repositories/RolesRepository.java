package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Roles}.
 * Proporciona operaciones CRUD y funcionalidades personalizadas para gestionar
 * los roles en el sistema. Este repositorio utiliza Spring Data JPA para interactuar
 * con la base de datos.
 */
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    /**
     * Busca un rol en la base de datos por su nombre exacto.
     * @param name el nombre del rol que se desea buscar.
     * @return el rol que coincide con el nombre proporcionado o {@code null} si no se encuentra.
     */
    public Roles findByName(String name);

}
