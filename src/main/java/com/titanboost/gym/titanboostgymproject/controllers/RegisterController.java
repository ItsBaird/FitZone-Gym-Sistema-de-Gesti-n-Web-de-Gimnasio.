package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.IdentificationTypes;
import com.titanboost.gym.titanboostgymproject.models.RegisterDto;
import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.services.IdentificationTypesService;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

/**
 * Controlador encargado de gestionar el registro de nuevos usuarios.
 * Proporciona las funcionalidades para mostrar el formulario de registro, validar los datos y registrar al usuario en el sistema.
 */
@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    UsersService usersService;

    @Autowired
    IdentificationTypesService identificationTypesService;

    /**
     * Muestra el formulario de registro de usuario.
     * Obtiene todos los tipos de identificación disponibles y los pasa al modelo para su visualización.
     *
     * @param model El modelo que se pasará a la vista para renderizar el formulario de registro.
     * @return El nombre de la vista para el formulario de registro.
     */
    @GetMapping
    public String register(Model model) {
        List<IdentificationTypes> identificationTypes = identificationTypesService.getAllIdentificationTypes();
        model.addAttribute("identificationTypes", identificationTypes);
        model.addAttribute("registerDto", new RegisterDto());
        model.addAttribute("success", false);
        return "register";
    }

    /**
     * Procesa los datos del formulario de registro.
     * Realiza validaciones de contraseñas, correo y documento, y si no hay errores, registra al usuario en el sistema.
     * Si el registro es exitoso, muestra un mensaje de éxito y redirige al administrador si el usuario tiene el rol adecuado.
     *
     * @param model El modelo que se pasará a la vista.
     * @param registerDto Los datos del usuario a registrar.
     * @param result Los resultados de la validación del formulario.
     * @param principal El usuario autenticado, si existe.
     * @return La vista de registro con mensajes de error si los hay, o redirección en caso de éxito.
     */
    @PostMapping
    public String register(Model model, @Valid @ModelAttribute RegisterDto registerDto,
                           BindingResult result, Principal principal) {
        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            result.addError(new FieldError("registerDto", "passwordConfirm", "Las contraseñas no coinciden"));
        }

        // Validación de existencia de correo
        if (usersService.findByEmail(registerDto.getEmail()) != null) {
            result.addError(new FieldError("registerDto", "email", "El correo ya existe"));
        }

        // Validación de existencia de documento
        if (usersService.findByDocumentNum(registerDto.getDocumentNum()) != null) {
            result.addError(new FieldError("registerDto", "documentNum", "El número de documento ya existe"));
        }

        if (result.hasErrors()) {
            List<IdentificationTypes> identificationTypes = identificationTypesService.getAllIdentificationTypes();
            model.addAttribute("identificationTypes", identificationTypes);
            return "register"; // Si hay errores, vuelve al formulario
        }

        try {
            // Llamada al servicio para registrar el usuario
            usersService.registerUser(registerDto);
            model.addAttribute("registerDto", new RegisterDto()); // Reiniciar formulario
            model.addAttribute("success", true);

            // Verificar autenticación y roles
            if (principal != null) { // Si el usuario tiene sesión activa
                Users currentUser = usersService.findByEmail(principal.getName());
                if (currentUser != null && currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase("Administrador")
                                || role.getName().equalsIgnoreCase("SuperAdministrador"))) {
                    return "redirect:/admin/users/gestionarUsuarios"; // Redirige a la gestión de usuarios
                }
            }

        } catch (Exception ex) {
            result.addError(new FieldError("registerDto", "name", "Error al registrar el usuario: " + ex.getMessage()));
        }

        List<IdentificationTypes> identificationTypes = identificationTypesService.getAllIdentificationTypes();
        model.addAttribute("identificationTypes", identificationTypes);
        return "register"; // Redirige al formulario si no hay sesión activa
    }
}
