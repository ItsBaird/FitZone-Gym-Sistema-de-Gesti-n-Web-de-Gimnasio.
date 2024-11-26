package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.Branches;
import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import com.titanboost.gym.titanboostgymproject.models.News;
import com.titanboost.gym.titanboostgymproject.services.NewsService;
import com.titanboost.gym.titanboostgymproject.services.UploadFileNewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar operaciones de noticias en el panel de administración.
 * Maneja operaciones CRUD para artículos de noticias, incluyendo creación, edición
 * y listado de noticias.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/news")
public class NewsControllers {

    /** Servicio para manejar operaciones de base de datos relacionadas con noticias */
    private final NewsService newsService;

    /** Servicio para gestionar cargas de archivos de imágenes de noticias */
    private final UploadFileNewsService uploadFileNewsService;

    /**
     * Muestra la página de gestión de noticias con una lista de todos los artículos.
     *
     * @param model Modelo de Spring MVC para añadir atributos a la vista
     * @return Nombre de la vista para la página de gestión de noticias
     */
    @GetMapping("/gestionarNoticias")
    public String gestionarNoticias(Model model) {
        List<News> noticias = newsService.getAllNews();
        model.addAttribute("noticias", noticias);
        return "gestionNoticias/gestionarNoticias";
    }

    /**
     * Muestra el formulario para crear una nueva noticia.
     *
     * @param model Modelo de Spring MVC para añadir un nuevo objeto News
     * @return Nombre de la vista para el formulario de creación de noticias
     */
    @GetMapping("/crearNoticia")
    public String formCreateNew(Model model) {
        model.addAttribute("news", new News());
        return "gestionNoticias/createNew";
    }

    /**
     * Guarda una nueva noticia con validación de carga de imagen.
     *
     * @param news Objeto News validado del formulario
     * @param result Resultado de validación del enlace
     * @param model Modelo de Spring MVC para añadir atributos de error
     * @param image Archivo de imagen cargado
     * @param flash Atributos de redirección para mensajes
     * @param status Estado de la sesión
     * @return Redirección a la página de gestión de noticias
     * @throws Exception Excepción general para manejo de errores
     */
    @PostMapping("/save")
    public String saveNew(@Validated @ModelAttribute("news") News news, BindingResult result, Model model,
                          @RequestParam("file") MultipartFile image, RedirectAttributes flash, SessionStatus status)
            throws Exception {

        // Validación de campos obligatorios
        if (result.hasErrors()) {
            model.addAttribute("error", "Corrija los errores en el formulario");
            return "gestionNoticias/createNew";
        }

        // Validación de que el archivo no esté vacío
        if (image.isEmpty()) {
            model.addAttribute("error", "Debe cargar una imagen.");
            return "gestionNoticias/createNew";
        }

        // Validar que el archivo sea una imagen
        if (!image.getContentType().startsWith("image/")) {
            model.addAttribute("error", "El archivo debe ser una imagen.");
            return "gestionNoticias/createNew";
        }

        // Guardar la imagen
        String uniqueFileName = uploadFileNewsService.copy(image);
        news.setUrl_image(uniqueFileName);

        // Configurar la noticia como habilitada y guardar en la base de datos
        news.setEnabled(true);
        newsService.createNews(news);
        status.setComplete();

        flash.addFlashAttribute("success", "Noticia creada exitosamente");
        return "redirect:/admin/news/gestionarNoticias";
    }

    /**
     * Muestra el formulario de edición para una noticia específica.
     *
     * @param id Identificador de la noticia a editar
     * @param model Modelo de Spring MVC para añadir la noticia
     * @return Vista del formulario de edición o redirección a la lista de noticias
     */
    // Método para mostrar el formulario de edición de una noticia
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Optional<News> newsOptional = newsService.getNewsById(id);

        if (newsOptional.isPresent()) {
            model.addAttribute("news", newsOptional.get());
            return "gestionNoticias/updateNew"; // Vista de Thymeleaf con el formulario
        } else {
            // Si no se encuentra la noticia, redirigir a la lista de noticias
            return "redirect:/admin/news/gestionarNoticias";
        }
    }

    /**
     * Actualiza una noticia existente, incluyendo la posibilidad de cambiar la imagen.
     *
     * @param id Identificador de la noticia a actualizar
     * @param updatedNews Objeto News con los datos actualizados
     * @param imageFile Archivo de imagen opcional para actualización
     * @param bindingResult Resultado de validación
     * @param redirectAttributes Atributos para mensajes de redirección
     * @return Redirección a la página de gestión de noticias
     */

    @PostMapping("/update/{id}")
    public String updateNews(
            @PathVariable("id") int id,
            @ModelAttribute News updatedNews,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult bindingResult, // Para manejar errores de validación
            RedirectAttributes redirectAttributes) {

        try {
            // Verificar si hay errores de validación
            if (bindingResult.hasErrors()) {
                // Si hay errores, redirigir de nuevo a la página de edición
                return "gestionNoticias/updateNew"; // Asegúrate de que la URL coincide con la vista de edición
            }

            // Obtener la noticia existente
            Optional<News> existingNewsOpt = newsService.getNewsById(id);

            if (existingNewsOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Noticia no encontrada");
                return "redirect:/admin/news/gestionarNoticias";
            }

            News existingNews = existingNewsOpt.get();

            // Actualizar los campos básicos
            existingNews.setTitle(updatedNews.getTitle());
            existingNews.setContent(updatedNews.getContent());
            existingNews.setEnabled(updatedNews.isEnabled());

            // Manejar la actualización de la imagen
            if (imageFile != null && !imageFile.isEmpty()) {
                // Eliminar la imagen anterior si existe
                if (existingNews.getUrl_image() != null && !existingNews.getUrl_image().isEmpty()) {
                    uploadFileNewsService.delete(existingNews.getUrl_image());
                }

                // Validación: Asegurar que el archivo sea una imagen
                if (!imageFile.getContentType().startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("error", "El archivo debe ser una imagen.");
                    return "redirect:/admin/news/edit/" + id;
                }

                // Guardar la nueva imagen y obtener el nombre del archivo
                String uniqueFilename = uploadFileNewsService.copy(imageFile);
                existingNews.setUrl_image(uniqueFilename);
            }

            // Guardar los cambios
            newsService.updateNew(existingNews);

            redirectAttributes.addFlashAttribute("success", "Noticia actualizada correctamente");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
            return "redirect:/admin/news/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la noticia: " + e.getMessage());
            return "redirect:/admin/news/edit/" + id;
        }

        return "redirect:/admin/news/gestionarNoticias";
    }

    /**
     * Filtra las noticias por un término de búsqueda.
     *
     * @param model Modelo de Spring MVC para añadir la lista de noticias
     * @param searchTerm Término de búsqueda para filtrar noticias por título
     * @return Vista de gestión de noticias con la lista filtrada
     */
    @GetMapping("/filtrarNoticias")
    public String listNews(Model model, @RequestParam(required = false) String searchTerm) {
        List<News> noticias;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            noticias = newsService.searchNewsByTitle(searchTerm);
        } else {
            noticias = newsService.getAllNews(); // Comportamiento original
        }
        model.addAttribute("noticias", noticias);
        return "gestionNoticias/gestionarNoticias";
    }
}
