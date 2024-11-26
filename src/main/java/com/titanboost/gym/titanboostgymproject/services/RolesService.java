package com.titanboost.gym.titanboostgymproject.services;

import com.titanboost.gym.titanboostgymproject.models.Roles;
import com.titanboost.gym.titanboostgymproject.repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de gestionar los roles en la aplicación.
 * <p>
 * Este servicio proporciona métodos para buscar roles por nombre, obtener todos los roles disponibles
 * y obtener roles por sus IDs.
 * </p>
 */
@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    /**
     * Busca un rol en la base de datos por su nombre.
     *
     * @param name El nombre del rol a buscar.
     * @return El objeto {@link Roles} que representa el rol encontrado, o {@code null} si no se encuentra un rol con ese nombre.
     */
    public Roles findByName(String name) {
        return rolesRepository.findByName(name);
    }

    /**
     * Obtiene todos los roles almacenados en la base de datos.
     *
     * @return Una lista de objetos {@link Roles} que representan todos los roles disponibles en la aplicación.
     */
    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    /**
     * Obtiene una lista de roles a partir de una lista de IDs proporcionada.
     *
     * @param roleIds Una lista de IDs de roles que se desean obtener.
     * @return Una lista de objetos {@link Roles} correspondientes a los IDs proporcionados.
     */
    public List<Roles> getRolesByIds(List<Integer> roleIds) {
        return rolesRepository.findAllById(roleIds);
    }
}

