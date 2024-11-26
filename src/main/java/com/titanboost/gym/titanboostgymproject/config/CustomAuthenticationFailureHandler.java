package com.titanboost.gym.titanboostgymproject.config;

import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URLEncoder;

/**
 * Handler personalizado para gestionar los fallos de autenticación en el sistema.
 * <p>
 * Este componente se encarga de interceptar los fallos de autenticación y proporcionar mensajes
 * de error adecuados dependiendo de la causa del fallo. Si el usuario está deshabilitado, se
 * muestra un mensaje diferente, indicando que debe contactar con el administrador.
 * </p>
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UsersService usersService;

    /**
     * Maneja el fallo de autenticación y redirige al usuario a la página de login con un mensaje de error.
     * <p>
     * Este método se invoca cuando la autenticación falla. Si el usuario existe y está deshabilitado,
     * se muestra un mensaje de error indicando que su cuenta ha sido deshabilitada. Si las credenciales
     * son incorrectas, se muestra un mensaje de error general.
     * </p>
     *
     * @param request La solicitud HTTP que contiene los parámetros de la autenticación fallida.
     * @param response La respuesta HTTP utilizada para redirigir al usuario a la página de login.
     * @param exception La excepción que se lanzó durante el proceso de autenticación.
     * @throws IOException Si ocurre un error durante la redirección.
     * @throws ServletException Si ocurre un error en la gestión del servlet.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        Users user = usersService.findByEmail(email);

        String errorMessage = "Credenciales inválidas, por favor intenta de nuevo.";

        // Si el usuario existe y está deshabilitado, cambiar el mensaje de error
        if (user != null && !user.isEnabled()) {
            errorMessage = "Tu cuenta ha sido deshabilitada. Por favor, contacta con un administrador.";
        }

        // Redirige a la página de login con el mensaje de error adecuado
        response.sendRedirect("/login?error=true&errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}
