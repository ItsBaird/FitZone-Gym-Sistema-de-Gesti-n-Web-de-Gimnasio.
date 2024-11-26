package com.titanboost.gym.titanboostgymproject.services;

import com.titanboost.gym.titanboostgymproject.models.News;
import com.titanboost.gym.titanboostgymproject.repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las noticias en la aplicación.
 * <p>
 * Este servicio proporciona métodos para obtener todas las noticias, crear nuevas noticias,
 * obtener noticias por su ID, actualizar noticias existentes y buscar noticias por título.
 * </p>
 */
@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    /**
     * Obtiene todas las noticias almacenadas en la base de datos.
     *
     * @return Una lista de objetos {@link News} que representan todas las noticias almacenadas.
     */
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    /**
     * Crea una nueva noticia y la guarda en la base de datos.
     *
     * @param news El objeto {@link News} que representa la noticia a crear.
     */
    public void createNews(News news) {
        newsRepository.save(news);
    }

    /**
     * Obtiene una noticia a partir de su ID.
     *
     * @param id El ID de la noticia a obtener.
     * @return Un {@link Optional} que contiene la noticia encontrada si existe, o {@link Optional#empty()} si no se encuentra.
     */
    public Optional<News> getNewsById(int id) {
        return newsRepository.findById(id);
    }

    /**
     * Actualiza una noticia existente en la base de datos.
     *
     * @param news El objeto {@link News} con los datos actualizados que se desea guardar.
     */
    public void updateNew(News news) {
        newsRepository.save(news);
    }

    /**
     * Busca noticias que contengan un título específico, ignorando mayúsculas y minúsculas.
     *
     * @param title El título o término de búsqueda utilizado para encontrar noticias.
     * @return Una lista de objetos {@link News} que contienen el término de búsqueda en su título.
     */
    public List<News> searchNewsByTitle(String title) {
        return newsRepository.findByTitleContainingIgnoreCase(title);
    }

}
