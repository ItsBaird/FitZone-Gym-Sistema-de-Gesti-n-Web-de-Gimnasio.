package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_Memberships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad {@link Users_Memberships}.
 * Proporciona operaciones CRUD y consultas personalizadas para gestionar la relación
 * entre usuarios y planes de membresía. Este repositorio utiliza Spring Data JPA
 * para interactuar con la base de datos.
 */
public interface Users_MembershipsRepository extends JpaRepository<Users_Memberships, Integer> {

    /**
     * Obtiene una lista de membresías expiradas que aún están habilitadas.
     *
     * @param currentDate la fecha y hora actual utilizada como referencia para verificar las membresías expiradas.
     * @return una lista de {@link Users_Memberships} que han expirado pero siguen habilitadas.
     */
    @Query("SELECT um FROM Users_Memberships um WHERE um.expiration_date < :currentDate AND um.enabled = true")
    List<Users_Memberships> findExpiredMemberships(LocalDateTime currentDate);

    /**
     * Busca todas las membresías activas asociadas a un usuario específico.
     *
     * @param user el usuario para el que se desean buscar las membresías activas.
     * @return una lista de {@link Users_Memberships} que están activas para el usuario proporcionado.
     */
    // Método para obtener membresías activas
    List<Users_Memberships> findByUserAndEnabledTrue(Users user);
}
