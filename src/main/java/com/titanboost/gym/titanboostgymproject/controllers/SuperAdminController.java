package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_Memberships;
import com.titanboost.gym.titanboostgymproject.models.Users_MembershipsDto;
import com.titanboost.gym.titanboostgymproject.services.MembershipPlansService;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import com.titanboost.gym.titanboostgymproject.services.Users_MembershipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador encargado de gestionar las membresías de los usuarios para los superadministradores.
 * Proporciona funcionalidades para visualizar, asignar y deshabilitar membresías de usuarios.
 */
@Controller
@RequestMapping("/superAdmin")
public class SuperAdminController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private Users_MembershipsService usersMembershipsService;

    @Autowired
    private MembershipPlansService membershipPlansService;

    /**
     * Muestra la página de gestión de membresías de un usuario específico.
     * Obtiene todas las membresías activas de un usuario y los planes de membresía disponibles.
     *
     * @param userId El ID del usuario cuya información de membresías se gestionará.
     * @param model El modelo que se pasará a la vista para renderizar la información.
     * @return El nombre de la vista para la gestión de membresías del usuario.
     * @throws IllegalArgumentException Si el usuario no se encuentra.
     */
    @GetMapping("/{userId}/memberships")
    public String manageMemberships(@PathVariable int userId, Model model) {
        Users user = usersService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        List<Users_Memberships> userMemberships = usersMembershipsService.findActiveMembershipsByUser(user);
        List<MembershipPlans> allMembershipPlans = membershipPlansService.getAllPlans();

        // Crear un mapa para las fechas formateadas
        Map<Integer, String> formattedPurchaseDates = new HashMap<>();
        Map<Integer, String> formattedExpirationDates = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Users_Memberships membership : userMemberships) {
            String formattedPurchaseDate = membership.getPurchase_date().format(formatter);
            String formattedExpirationDate = membership.getExpiration_date().format(formatter);

            // Usar el ID de la membresía como clave
            formattedPurchaseDates.put(membership.getUser_membership_id(), formattedPurchaseDate);
            formattedExpirationDates.put(membership.getUser_membership_id(), formattedExpirationDate);
        }

        // Agregar los mapas al modelo
        model.addAttribute("formattedPurchaseDates", formattedPurchaseDates);
        model.addAttribute("formattedExpirationDates", formattedExpirationDates);

        model.addAttribute("user", user);
        model.addAttribute("userMemberships", userMemberships);
        model.addAttribute("allMembershipPlans", allMembershipPlans);

        return "gestionUsuarios/usersMemberships";
    }


    /**
     * Asigna un plan de membresía a un usuario.
     * Calcula la fecha de expiración según la duración del plan seleccionado y lo asigna al usuario.
     *
     * @param userId El ID del usuario al que se le asignará la membresía.
     * @param membershipId El ID del plan de membresía que se asignará.
     * @param model El modelo que se pasará a la vista.
     * @return La redirección a la página de gestión de membresías del usuario.
     * @throws IllegalArgumentException Si el usuario o el plan de membresía no se encuentran.
     */
    @PostMapping("/{userId}/memberships/assign")
    public String assignMembership(@PathVariable int userId,
                                   @RequestParam int membershipId,
                                   Model model) {
        // Buscar el usuario
        Users user = usersService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Buscar el plan de membresía seleccionado usando Optional
        Optional<MembershipPlans> membershipPlanOptional = membershipPlansService.getPlanById(membershipId);
        if (membershipPlanOptional.isEmpty()) {
            throw new IllegalArgumentException("Plan de membresía no encontrado");
        }

        MembershipPlans membershipPlan = membershipPlanOptional.get();  // Obtener el plan de membresía

        // Obtener la duración del plan de membresía
        int duration = membershipPlan.getDuration();  // Este valor se obtiene del plan de membresía

        // Calcular la fecha de expiración
        LocalDateTime purchaseDate = LocalDateTime.now();  // La fecha de compra es la fecha actual
        LocalDateTime expirationDate = purchaseDate.plusDays(duration);  // Fecha de expiración calculada

        // Crear el DTO para la membresía
        Users_MembershipsDto usersMembershipsDto = new Users_MembershipsDto();
        usersMembershipsDto.setUser_id(userId);
        usersMembershipsDto.setMembership_id(membershipId);
        usersMembershipsDto.setPurchase_date(purchaseDate);
        usersMembershipsDto.setExpiration_date(expirationDate);

        // Crear la nueva membresía en la base de datos
        usersMembershipsService.createMembership(usersMembershipsDto);

        return "redirect:/superAdmin/" + userId + "/memberships";  // Redirigir al listado de membresías
    }

    /**
     * Deshabilita una membresía de un usuario.
     *
     * @param userId El ID del usuario cuyo plan de membresía será deshabilitado.
     * @param membershipId El ID de la membresía a deshabilitar.
     * @return La redirección a la página de gestión de membresías del usuario.
     */
    @PostMapping("/{userId}/memberships/{membershipId}/disable")
    public String disableMembership(
            @PathVariable int userId,
            @PathVariable int membershipId) {

        // Deshabilitar la membresía utilizando el servicio
        usersMembershipsService.disableMembership(membershipId);

        // Redirigir al listado de membresías del usuario
        return "redirect:/superAdmin/" + userId + "/memberships";  // Ajusta la URL de destino según tu ruta
    }
}
