package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad {@link Users}.
 * Proporciona operaciones CRUD y consultas personalizadas relacionadas con la gestión
 * de usuarios en la base de datos. Este repositorio utiliza Spring Data JPA
 * para interactuar con la capa de persistencia.
 */
public interface UsersRepository extends JpaRepository<Users, Integer> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email el correo electrónico del usuario que se desea buscar.
     * @return una instancia de {@link Users} correspondiente al correo electrónico proporcionado,
     * o {@code null} si no se encuentra ningún usuario con ese correo.
     */
    public Users findByEmail(String email);

    /**
     * Busca un usuario por su número de documento.
     *
     * @param documentNum el número de documento del usuario que se desea buscar.
     * @return una instancia de {@link Users} correspondiente al número de documento proporcionado,
     * o {@code null} si no se encuentra ningún usuario con ese número de documento.
     */
    public Users findByDocumentNum(String documentNum);
}
