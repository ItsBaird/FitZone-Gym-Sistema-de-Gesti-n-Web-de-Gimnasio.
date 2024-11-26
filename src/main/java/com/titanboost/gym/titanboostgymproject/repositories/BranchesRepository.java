package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.Branches;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad {@link Branches}.
 * Proporciona operaciones CRUD y funcionalidad adicional para gestionar las sedes
 * del sistema mediante la integración con Spring Data JPA.
 */
public interface BranchesRepository extends JpaRepository<Branches, Integer> {
}
