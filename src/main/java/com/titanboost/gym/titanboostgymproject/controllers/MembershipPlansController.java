package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.Branches;
import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import com.titanboost.gym.titanboostgymproject.services.BranchesService;
import com.titanboost.gym.titanboostgymproject.services.MembershipPlansService;
import com.titanboost.gym.titanboostgymproject.services.UploadFilePlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con los planes de membresía.
 * Incluye la creación, edición, visualización, eliminación y filtrado de planes de membresía.
 */
@Controller
@RequestMapping("/admin/membershipPlans")
public class MembershipPlansController {

    @Autowired
    private MembershipPlansService membershipPlansService;

    @Autowired
    private UploadFilePlansService uploadFilePlansService;
    @Autowired
    private BranchesService branchesService;

    /**
     * Muestra la vista para gestionar todos los planes de membresía.
     * Obtiene todos los planes de membresía disponibles y los pasa al modelo para su visualización.
     *
     * @param model El modelo que se pasará a la vista para renderizar la lista de planes.
     * @return El nombre de la vista para gestionar los planes.
     */
    @GetMapping("/gestionarPlanes")
    public String selectAll(Model model) {
        model.addAttribute("membershipPlans", membershipPlansService.getAllPlans());
        return "gestionPlanes/gestionarPlanes";
    }

    /**
     * Muestra el formulario para crear un nuevo plan de membresía.
     * Obtiene todas las sedes disponibles para permitir seleccionar una al crear el plan.
     *
     * @param model El modelo que se pasará a la vista para renderizar el formulario de creación de planes.
     * @return El nombre de la vista para crear un nuevo plan.
     */
    @GetMapping("/crearPlan")
    public String newPlan(Model model) {
        List<Branches> branches = branchesService.getAll();
        model.addAttribute("branches", branches);
        model.addAttribute("membershipPlan", new MembershipPlans());
        return "gestionPlanes/createPlan";
    }

    /**
     * Guarda un nuevo plan de membresía.
     * Este método valida la imagen subida y procesa los datos del plan antes de guardarlo en la base de datos.
     * Si hay errores de validación, devuelve el formulario con los mensajes de error.
     *
     * @param membershipPlan El plan de membresía a guardar.
     * @param result El resultado de la validación del formulario.
     * @param model El modelo que se pasará a la vista.
     * @param image El archivo de imagen asociado al plan.
     * @param flash Los atributos de redirección para mostrar mensajes flash.
     * @param status El estado de la sesión para completar la operación.
     * @return La redirección a la vista de gestión de planes si el plan se guarda correctamente, o el formulario de creación si hay errores.
     * @throws Exception Si ocurre un error al procesar la imagen o al guardar el plan.
     */
    @PostMapping("/save")
    public String saveMembershipPlan(
            @Validated @ModelAttribute("membershipPlan") MembershipPlans membershipPlan,
            BindingResult result,
            Model model,
            @RequestParam("file") MultipartFile image,
            RedirectAttributes flash,
            SessionStatus status) throws Exception {

        // Populate branches for the dropdown
        model.addAttribute("branches", branchesService.getAll());

        // Validación personalizada para la imagen
        if (image.isEmpty()) {
            result.rejectValue("url_image", "required", "Debe seleccionar una imagen.");
        } else {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase()
                    : "";

            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
            if (!allowedExtensions.contains(extension)) {
                result.rejectValue("url_image", "invalid", "Solo se permiten archivos de imagen (jpg, jpeg, png, gif).");
            }

            if (image.getSize() > 5 * 1024 * 1024) { // Máximo 5MB
                result.rejectValue("url_image", "size", "El archivo no debe superar 5MB.");
            }
        }

        // Si hay errores de validación, retorna a la vista
        if (result.hasErrors()) {
            return "gestionPlanes/createPlan";
        }

        // Procesamiento de la imagen si no hay errores
        if (!image.isEmpty()) {
            if (membershipPlan.getMembership_id() > 0 &&
                    membershipPlan.getUrl_image() != null &&
                    !membershipPlan.getUrl_image().isEmpty()) {
                uploadFilePlansService.delete(membershipPlan.getUrl_image());
            }
            String uniqueFileName = uploadFilePlansService.copy(image);
            membershipPlan.setUrl_image(uniqueFileName);
        }

        // Guardar plan de membresía
        membershipPlan.setEnabled(true);
        membershipPlansService.addPlan(membershipPlan);
        status.setComplete();

        return "redirect:/admin/membershipPlans/gestionarPlanes";
    }

    /**
     * Muestra los detalles de un plan de membresía.
     * Si el plan existe, se muestra su información; si no, se redirige a una página de error.
     *
     * @param id El ID del plan de membresía a mostrar.
     * @param model El modelo para pasar los detalles del plan a la vista.
     * @return La vista de detalles del plan si el plan existe, o una página de error si no se encuentra el plan.
     */
    @RequestMapping("/detail/{id}")
    public String goDetail(@PathVariable(value = "id") int id, Model model) {
        Optional<MembershipPlans> membershipPlan = membershipPlansService.getPlanById(id);

        // Desenvolver el Optional y pasarlo al modelo directamente si está presente
        membershipPlan.ifPresent(plan -> model.addAttribute("membershipPlan", plan));

        // Si no está presente, podrías redirigir o mostrar un error
        return membershipPlan.isPresent() ? "gestionPlanes/planDetails" : "errorPage";
    }


    /**
     * Muestra el formulario para editar un plan de membresía.
     * Si el plan no existe, redirige a la lista de planes disponibles.
     *
     * @param membership_id El ID del plan de membresía a editar.
     * @param model El modelo para pasar el plan y las sedes a la vista de edición.
     * @return El nombre de la vista para editar el plan de membresía o la redirección si no se encuentra el plan.
     */
    @GetMapping("/edit/{membership_id}")
    public String showEditPlanForm(@PathVariable("membership_id") int membership_id, Model model) {
        // Obtener el plan de membresía de un Optional
        Optional<MembershipPlans> optionalPlan = membershipPlansService.getPlanById(membership_id);

        // Verificar si el plan existe
        if (optionalPlan.isEmpty()) {
            return "redirect:/admin/membershipPlans";  // Redirigir si no se encuentra el plan
        }

        MembershipPlans plan = optionalPlan.get();  // Obtener el valor dentro del Optional si está presente

        // Obtener las sedes para el dropdown (opciones de sedes)
        List<Branches> branches = branchesService.getAll(); // Obtención de sedes desde el servicio correspondiente
        model.addAttribute("branches", branches);  // Lista de sedes
        model.addAttribute("membershipPlan", plan);  // Plan de membresía a editar

        return "gestionPlanes/updatePlan";  // Vista para actualizar el plan de membresía
    }

    /**
     * Actualiza un plan de membresía existente.
     * Si se sube una nueva imagen, la valida y procesa antes de actualizar el plan en la base de datos.
     * Si hay errores de validación, muestra el formulario de edición nuevamente.
     *
     * @param membershipId El ID del plan de membresía a actualizar.
     * @param updatedPlan El objeto con los datos actualizados del plan.
     * @param result Los resultados de la validación del formulario.
     * @param model El modelo que se pasará a la vista.
     * @param image El archivo de imagen asociado al plan.
     * @param flash Los atributos de redirección para mostrar mensajes flash.
     * @param status El estado de la sesión para completar la operación.
     * @return La redirección a la vista de gestión de planes si el plan se actualiza correctamente, o el formulario de edición si hay errores.
     * @throws Exception Si ocurre un error al procesar la imagen o al actualizar el plan.
     */
    @PostMapping("/edit/{membershipId}")
    public String updateMembershipPlan(
            @PathVariable("membershipId") int membershipId,
            @Validated @ModelAttribute("membershipPlan") MembershipPlans updatedPlan,
            BindingResult result,
            Model model,
            @RequestParam(value = "imageFile", required = false) MultipartFile image,
            RedirectAttributes flash,
            SessionStatus status) throws Exception {

        // Verificar si el plan existe
        Optional<MembershipPlans> existingPlanOpt = membershipPlansService.getPlanById(membershipId);
        if (existingPlanOpt.isEmpty()) {
            flash.addFlashAttribute("error", "El plan no existe.");
            return "redirect:/admin/membershipPlans/gestionarPlanes";
        }
        MembershipPlans existingPlan = existingPlanOpt.get();

        // Mantener la imagen existente y el ID
        updatedPlan.setUrl_image(existingPlan.getUrl_image());
        updatedPlan.setMembership_id(membershipId);

        // Validar la nueva imagen si se subió una
        if (image != null && !image.isEmpty()) {
            try {
                uploadFilePlansService.validateImage(image, result);
            } catch (Exception e) {
                result.rejectValue("url_image", "error", "Error con la imagen: " + e.getMessage());
            }
        }

        // Si hay errores, mostrar el formulario nuevamente
        if (result.hasErrors()) {
            model.addAttribute("branches", branchesService.getAll());
            return "gestionPlanes/updatePlan";
        }

        try {
            // Actualizar campos del plan
            existingPlan.setName(updatedPlan.getName());
            existingPlan.setDescription(updatedPlan.getDescription());
            existingPlan.setPrice(updatedPlan.getPrice());
            existingPlan.setDuration(updatedPlan.getDuration());
            existingPlan.setEnabled(updatedPlan.isEnabled());
            existingPlan.setBranch_id(updatedPlan.getBranch_id());

            // Procesar nueva imagen si se subió
            if (image != null && !image.isEmpty()) {
                if (existingPlan.getUrl_image() != null && !existingPlan.getUrl_image().isEmpty()) {
                    uploadFilePlansService.delete(existingPlan.getUrl_image());
                }
                String uniqueFileName = uploadFilePlansService.copy(image);
                existingPlan.setUrl_image(uniqueFileName);
            }

            membershipPlansService.updatePlan(existingPlan);
            status.setComplete();
            flash.addFlashAttribute("success", "Plan actualizado correctamente.");
            return "redirect:/admin/membershipPlans/gestionarPlanes";

        } catch (Exception e) {
            result.rejectValue("", "error", "Error al actualizar el plan: " + e.getMessage());
            model.addAttribute("branches", branchesService.getAll());
            return "gestionPlanes/updatePlan";
        }
    }

    /**
     * Filtra los planes de membresía según el término de búsqueda proporcionado.
     * Si no se proporciona un término de búsqueda, muestra todos los planes.
     *
     * @param model El modelo que se pasará a la vista para mostrar los planes.
     * @param searchTerm El término de búsqueda para filtrar los planes.
     * @return El nombre de la vista para gestionar los planes con los resultados de la búsqueda.
     */
    //Filtrar Planes de Membresía
    @GetMapping("/filtrarPlanes")
    public String filterMembershipPlans(Model model,
                                        @RequestParam(required = false) String searchTerm) {
        List<MembershipPlans> membershipPlans;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            membershipPlans = membershipPlansService.searchMembershipPlansByName(searchTerm);
        } else {
            membershipPlans = membershipPlansService.getAllPlans();
        }
        model.addAttribute("membershipPlans", membershipPlans);
        return "gestionPlanes/gestionarPlanes";
    }

}




