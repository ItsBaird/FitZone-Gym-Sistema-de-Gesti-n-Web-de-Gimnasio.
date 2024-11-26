package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.IdentificationTypes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad {@link IdentificationTypes}.
 * Proporciona operaciones CRUD y funcionalidad adicional para gestionar los tipos de identificación
 * del sistema mediante la integración con Spring Data JPA.
 */
public interface IdentificationTypesRepository extends JpaRepository<IdentificationTypes, Integer> {

}
