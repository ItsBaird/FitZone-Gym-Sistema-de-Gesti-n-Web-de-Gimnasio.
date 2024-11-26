package com.titanboost.gym.titanboostgymproject.repositories;

import com.titanboost.gym.titanboostgymproject.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio para la entidad {@link News}.
 * Proporciona operaciones CRUD y funcionalidades personalizadas para gestionar
 * las noticias en el sistema. Integra Spring Data JPA para interactuar con la base de datos.
 */
public interface NewsRepository extends JpaRepository<News, Integer> {

    /**
     * Busca las noticias cuyo título contenga una cadena específica,
     * ignorando mayúsculas y minúsculas.
     *
     * @param title la cadena parcial del título a buscar.
     * @return una lista de noticias que coinciden con el criterio de búsqueda.
     */
    List<News> findByTitleContainingIgnoreCase(String title);
}
