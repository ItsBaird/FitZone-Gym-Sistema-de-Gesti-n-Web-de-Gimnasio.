package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
/**
 * Representa una noticia en el sistema.
 * Las noticias pueden contener un título, contenido, una imagen opcional, y un estado de habilitación.
 */
@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    /**
     * Identificador único de la noticia.
     * Se genera automáticamente al guardar la entidad en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int new_id;

    /**
     * Título de la noticia.
     * Es obligatorio y debe tener entre 5 y 100 caracteres.
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    private String title;

    /**
     * Contenido de la noticia.
     * Es obligatorio y debe tener al menos 10 caracteres.
     */
    @NotBlank(message = "El contenido es obligatorio")
    @Size(min = 10, message = "El contenido debe tener al menos 10 caracteres")
    private String content;

    /**
     * URL de la imagen asociada a la noticia.
     */
    private String url_image;

    /**
     * Estado de habilitación de la noticia.
     * Indica si la noticia está activa o desactivada en el sistema.
     */
    private boolean enabled;
}
