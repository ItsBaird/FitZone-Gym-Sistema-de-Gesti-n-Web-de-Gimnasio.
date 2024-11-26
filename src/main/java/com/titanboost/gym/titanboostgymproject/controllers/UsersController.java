package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.*;
import com.titanboost.gym.titanboostgymproject.services.IdentificationTypesService;
import com.titanboost.gym.titanboostgymproject.services.RolesService;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador encargado de gestionar las vistas y operaciones relacionadas con los detalles del usuario.
 * Este controlador permite visualizar la información del usuario autenticado, incluyendo sus roles (si es Administrador o SuperAdministrador) y membresías activas.
 */
@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;


    /**
     * Muestra los detalles del usuario autenticado.
     * Obtiene información sobre el usuario, sus roles y las membresías activas asociadas, calculando también
     * los días restantes de sus membresías activas.
     *
     * @param userDetails Los detalles del usuario autenticado proporcionados por Spring Security.
     * @param model El modelo que se pasará a la vista para renderizar la información.
     * @return El nombre de la vista para mostrar los detalles del usuario.
     */
    @GetMapping("/user/userDetails")
    public String viewUserDetails(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Obtener el usuario autenticado por email
        Users user = usersService.findByEmail(userDetails.getUsername());

        // Obtener las membresías activas del usuario
        List<Users_Memberships> activeMemberships = usersService.findActiveMembershipsByUser(user);

        // Calcular el total de días restantes en todas las membresías activas
        long totalDaysRemaining = usersService.calculateTotalDaysRemaining(activeMemberships);

        boolean hasActiveMemberships = totalDaysRemaining > 0;

        // Pasar al modelo los datos del usuario y sus roles
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles()); // Pasar los roles del usuario
        model.addAttribute("activeMemberships", activeMemberships);
        model.addAttribute("totalDaysRemaining", totalDaysRemaining);
        model.addAttribute("hasActiveMemberships", hasActiveMemberships);

        return "gestionUsuarios/userDetails"; // Vista para mostrar los detalles del usuario
    }
}

