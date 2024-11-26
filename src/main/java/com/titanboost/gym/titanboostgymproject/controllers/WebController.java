package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import com.titanboost.gym.titanboostgymproject.models.News;
import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_Memberships;
import com.titanboost.gym.titanboostgymproject.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador responsable de manejar las rutas públicas y administrativas de la aplicación.
 * Este controlador gestiona las páginas de inicio, planes de membresía, noticias, imágenes y otros recursos,
 * así como las vistas de error y detalles sobre las sedes.
 */
@Controller
public class WebController {

    @Autowired
    private MembershipPlansService membershipPlansService;

    @Autowired
    private UploadFilePlansService uploadFilePlansService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private UploadFileNewsService uploadFileNewsService;

    /**
     * Muestra la página de inicio de la aplicación.
     *
     * @return El nombre de la vista de inicio.
     */
    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    /**
     * Muestra el panel de administración.
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista del panel de administración.
     */
    @GetMapping("/admin/adminDashboard")
    public String adminDashboard(Model model) {
        return "adminDashboard";
    }

    /**
     * Muestra los planes de membresía habilitados, organizados por sede.
     * Se filtran los planes habilitados y luego se separan según las sedes (Sede Norte y Sede Sur).
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista de planes de membresía.
     */
    @RequestMapping("/public/membershipPlans")
    public String showPlans(Model model) {
        // Obtener todos los planes de membresía
        List<MembershipPlans> allPlans = membershipPlansService.getAllPlans();

        // Filtrar los planes que están habilitados (enabled == true)
        List<MembershipPlans> enabledPlans = allPlans.stream()
                .filter(MembershipPlans::isEnabled)
                .collect(Collectors.toList());

        // Filtrar los planes por sede
        List<MembershipPlans> nortePlans = enabledPlans.stream()
                .filter(plan -> plan.getBranch_id().getName().equals("Sede Norte"))
                .collect(Collectors.toList());

        List<MembershipPlans> surPlans = enabledPlans.stream()
                .filter(plan -> plan.getBranch_id().getName().equals("Sede Sur"))
                .collect(Collectors.toList());

        // Agregar los planes por sede al modelo
        model.addAttribute("nortePlans", nortePlans);
        model.addAttribute("surPlans", surPlans);

        return "planes"; // Nombre de la vista
    }

    /**
     * Permite ver la imagen asociada a un plan de membresía.
     * Este método maneja la carga de imágenes de planes desde el servidor y las devuelve como respuesta.
     *
     * @param filename El nombre del archivo de imagen.
     * @return La respuesta con la imagen del plan de membresía.
     */
    @GetMapping(value = "/uploads/{filename}")
    public ResponseEntity<Resource> goImagePlans(@PathVariable String filename) {
        Resource resource = null;
        try {
            resource = uploadFilePlansService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Permite ver la imagen asociada a una noticia.
     * Este método maneja la carga de imágenes de noticias desde el servidor y las devuelve como respuesta.
     *
     * @param filename El nombre del archivo de imagen de la noticia.
     * @return La respuesta con la imagen de la noticia.
     */
    @GetMapping(value = "/uploads/news/{filename}")
    public ResponseEntity<Resource> goImageNews(@PathVariable String filename) {
        Resource resource = null;
        try {
            resource = uploadFileNewsService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Muestra una lista de todas las noticias habilitadas, ordenadas por su ID en orden descendente.
     *
     * @param model el modelo que contiene los datos para la vista
     * @return el nombre de la vista que mostrará las noticias
     */
    @GetMapping("/public/news")
    public String showNews(Model model) {
        // Obtener todas las noticias habilitadas y ordenarlas por `new_id` en orden descendente
        List<News> enabledNews = newsService.getAllNews().stream()
                .filter(News::isEnabled) // Filtrar solo las noticias habilitadas
                .sorted(Comparator.comparingInt(News::getNew_id).reversed()) // Ordenar por ID descendente
                .collect(Collectors.toList());

        // Agregar las noticias habilitadas al modelo
        model.addAttribute("enabledNews", enabledNews);

        return "news"; // Nombre de la vista
    }

    /**
     * Busca noticias habilitadas cuyo título contenga el texto proporcionado, ignorando mayúsculas y minúsculas.
     * Las noticias encontradas se ordenan por su ID en orden descendente y se pasan al modelo.
     *
     * @param title el texto a buscar en los títulos de las noticias
     * @param model el modelo que contiene los datos para la vista
     * @return el nombre de la vista que mostrará los resultados de la búsqueda
     */
    @GetMapping("/public/news/search")
    public String searchNews(@RequestParam("title") String title, Model model) {
        // Buscar noticias habilitadas que contengan el título ingresado (ignorando mayúsculas/minúsculas)
        List<News> matchingNews = newsService.getAllNews().stream()
                .filter(news -> news.isEnabled() && news.getTitle().toLowerCase().contains(title.toLowerCase()))
                .sorted(Comparator.comparingInt(News::getNew_id).reversed()) // Mantener orden descendente
                .collect(Collectors.toList());

        // Agregar las noticias filtradas al modelo
        model.addAttribute("enabledNews", matchingNews);

        return "news";
    }


    /**
     * Muestra la página de acceso denegado (403).
     *
     * @return El nombre de la vista de acceso denegado.
     */
    @GetMapping("/public/403")
    public String accessDenied() {
        return "403";
    }

    /**
     * Muestra los detalles de una noticia específica.
     * Si la noticia existe, se pasa al modelo; de lo contrario, se redirige a la página de error.
     *
     * @param id El identificador de la noticia.
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista de detalles de la noticia.
     */
    @GetMapping("/public/newsDetails/{id}")
    public String showNewsDetails(@PathVariable("id") int id, Model model) {
        Optional<News> news = newsService.getNewsById(id);
        if (news.isPresent()) {
            model.addAttribute("news", news.get());
            return "gestionNoticias/newsDetails";
        } else {
            // Si la noticia no existe, puedes redirigir a una página de error o lista
            return "redirect:/public/errorPage";
        }
    }

    /**
     * Muestra la página de error general.
     *
     * @return El nombre de la vista de error.
     */
    @GetMapping("/public/errorPage")
    public String errorPage() {
        return "errorPage";
    }


    /**
     * Muestra la página de información sobre la organización.
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista de la página "Sobre Nosotros".
     */
    @GetMapping("/public/sobreNosotros")
    public String sobreNosotros(Model model) {
        return "sobreNosotros";
    }

    /**
     * Muestra la página de la Sede Norte.
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista de la página de la Sede Norte.
     */
    @GetMapping("/public/sedeNorte")
    public String sedeNorte(Model model) {
        return "sedeNorte";
    }

    /**
     * Muestra la página de la Sede Sur.
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista de la página de la Sede Sur.
     */
    @GetMapping("/public/sedeSur")
    public String sedeSur(Model model) {
        return "sedeSur";
    }

}
