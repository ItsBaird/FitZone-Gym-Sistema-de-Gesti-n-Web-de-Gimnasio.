package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio para la entidad {@link MembershipPlans}.
 * Proporciona operaciones CRUD y funcionalidades personalizadas para gestionar
 * los planes de membresía en el sistema. Integra Spring Data JPA para interactuar
 * con la base de datos.
 */
public interface MembershipPlansRepository extends JpaRepository<MembershipPlans, Integer> {

    /**
     * Busca los planes de membresía cuyo nombre contenga una cadena específica,
     * ignorando mayúsculas y minúsculas.
     *
     * @param name la cadena parcial del nombre a buscar.
     * @return una lista de planes de membresía que coinciden con el criterio de búsqueda.
     */
    List<MembershipPlans> findByNameContainingIgnoreCase(String name);
}
