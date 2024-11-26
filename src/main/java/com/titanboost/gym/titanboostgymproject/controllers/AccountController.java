package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.IdentificationTypes;
import com.titanboost.gym.titanboostgymproject.models.RegisterDto;
import com.titanboost.gym.titanboostgymproject.services.IdentificationTypesService;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controlador encargado de gestionar las peticiones relacionadas con la cuenta de usuario.
 * Este controlador maneja el inicio de sesión y la visualización de mensajes de error en caso de credenciales inválidas
 * o cuentas deshabilitadas.
 * Los métodos de este controlador incluyen el manejo de la página de inicio de sesión y la presentación de mensajes
 * de error si se produce un fallo en el proceso de autenticación.
 */
@Controller
public class AccountController {

    @Autowired
    private IdentificationTypesService identificationTypeService;

    @Autowired
    private UsersService usersService;

    /**
     * Maneja la solicitud GET para la página de inicio de sesión.
     * Esta acción se activa cuando el usuario visita la página de login.
     * Si se proporciona un parámetro de error, se muestra un mensaje relacionado con el error.
     *
     * @param error Parámetro opcional que indica si hubo un error de autenticación.
     * @param errorMessage Mensaje de error opcional que puede ser mostrado si se produce un fallo.
     * @param model El objeto Model que se utiliza para pasar atributos a la vista.
     * @return El nombre de la vista de inicio de sesión (login).
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "errorMessage", required = false) String errorMessage,
                        Model model) {

        // Si hay un error de credenciales inválidas o cuenta deshabilitada
        if (error != null) {
            model.addAttribute("errorMessage", errorMessage != null ? errorMessage : "Credenciales inválidas, por favor intenta de nuevo.");
        }

        return "login"; // Retorna la vista de login
    }
}
