package com.titanboost.gym.titanboostgymproject.controllers;

import com.titanboost.gym.titanboostgymproject.models.IdentificationTypes;
import com.titanboost.gym.titanboostgymproject.models.Roles;
import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_Memberships;
import com.titanboost.gym.titanboostgymproject.services.IdentificationTypesService;
import com.titanboost.gym.titanboostgymproject.services.RolesService;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador encargado de gestionar las operaciones de administración de usuarios.
 * Permite la visualización de los usuarios, su edición y búsqueda, así como la visualización de detalles
 * y la gestión de roles y permisos asociados a cada usuario.
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private IdentificationTypesService identificationTypeService;

    @Autowired
    private RolesService rolesService; // Inyectar el servicio de roles


    /**
     * Muestra la vista de gestión de usuarios.
     * Este método obtiene y muestra a los usuarios de acuerdo con los roles del usuario autenticado,
     * como SuperAdministrador o Administrador.
     *
     * @param model El modelo para pasar atributos a la vista.
     * @param authentication El objeto de autenticación para obtener el usuario autenticado.
     * @return El nombre de la vista para gestionar los usuarios.
     */
    @GetMapping("/gestionarUsuarios")
    public String gestionarUsuarios(Model model, Authentication authentication) {
        // Obtener el nombre del usuario autenticado
        String email = authentication.getName();
        Users authenticatedUser = usersService.findByEmail(email);

        // Verificar los roles del usuario autenticado
        boolean isAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Administrador"));
        boolean isSuperAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SuperAdministrador"));

        List<Users> users;

        if (isSuperAdmin) {
            // Si es superadministrador, mostrar todos los usuarios
            users = usersService.getAllUsers();
        } else if (isAdmin) {
            // Si es administrador, mostrar solo los usuarios (no administradores ni superadministradores)
            users = usersService.getAllUsers().stream()
                    .filter(user -> user.getRoles().stream()
                            .noneMatch(role -> role.getName().equals("Administrador") || role.getName().equals("SuperAdministrador")))
                    .collect(Collectors.toList());
        } else {
            // Si no es ni administrador ni superadministrador, no mostrar nada o mostrar solo el usuario
            users = Collections.singletonList(authenticatedUser);
        }

        // Agregar los usuarios filtrados al modelo
        model.addAttribute("users", users);
        return "gestionUsuarios/gestionarUsuarios"; // Vista para mostrar los usuarios
    }

    /**
     * Muestra los detalles de un usuario específico.
     * Este método muestra la información del usuario, incluyendo sus roles y las membresías activas.
     *
     * @param userId El ID del usuario a mostrar.
     * @param model El modelo para pasar atributos a la vista.
     * @return El nombre de la vista para mostrar los detalles del usuario.
     */
    @GetMapping("/userDetails/{userId}")
    public String viewUserDetails(@PathVariable("userId") int userId, Model model) {
        Users user = usersService.findById(userId);

        if (user == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/users/gestionarUsuarios";
        }

        // Obtener las membresías activas del usuario
        List<Users_Memberships> activeMemberships = usersService.findActiveMembershipsByUser(user);

        // Calcular los días restantes de las membresías activas
        long totalDaysRemaining = usersService.calculateTotalDaysRemaining(activeMemberships);

        boolean hasActiveMemberships = totalDaysRemaining > 0;

        // Obtener los roles del usuario
        List<Roles> roles = user.getRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles); // Añadir los roles al modelo
        model.addAttribute("totalDaysRemaining", totalDaysRemaining);
        model.addAttribute("hasActiveMemberships", hasActiveMemberships);

        return "gestionUsuarios/userDetails";
    }


    /**
     * Realiza una búsqueda de usuarios por correo electrónico.
     * Si se encuentra un usuario con el correo especificado, redirige a la vista de detalles del usuario.
     * Si no, muestra un mensaje de error.
     *
     * @param email El correo electrónico del usuario a buscar.
     * @param model El modelo para pasar atributos a la vista.
     * @param redirectAttributes Atributos para redirigir con mensajes flash.
     * @return La redirección a la vista de gestión de usuarios.
     */
    @PostMapping("/search")
    public String searchUserByEmail(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes) {
        try {
            Users user = usersService.findByEmail(email);
            if (user != null) {
                // Si encuentra el usuario, redirige a la vista de detalles
                return "redirect:/admin/users/userDetails/" + user.getUser_id();
            } else {
                // Si no encuentra el usuario, añade mensaje de error
                redirectAttributes.addFlashAttribute("error", "No se encontró ningún usuario con ese email");
                return "redirect:/admin/users/gestionarUsuarios";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al buscar el usuario");
            return "redirect:/admin/users/gestionarUsuarios";
        }
    }

    /**
     * Muestra el formulario para editar un usuario.
     * Verifica si el usuario autenticado tiene permisos para editar al usuario seleccionado,
     * especialmente si está intentando editar usuarios con roles elevados.
     *
     * @param userId El ID del usuario que se desea editar.
     * @param model El modelo para pasar atributos a la vista.
     * @param principal El objeto Principal que contiene la información del usuario autenticado.
     * @return El nombre de la vista para editar al usuario o redirección si no tiene permisos.
     */
    @GetMapping("/edit/{userId}")
    public String showEditUserForm(@PathVariable("userId") int userId, Model model, Principal principal) {
        // Obtener el usuario a editar por ID
        Users userToEdit = usersService.findById(userId);
        if (userToEdit == null) {
            return "redirect:/admin/users/gestionarUsuarios"; // Redirigir si el usuario no existe
        }

        // Obtener el usuario autenticado
        Users currentUser = usersService.findByEmail(principal.getName());
        boolean isSuperAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SuperAdministrador"));

        // Verificar si el usuario autenticado intenta editar un usuario con roles elevados
        boolean isEditingElevatedRole = userToEdit.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("Administrador") || role.getName().equalsIgnoreCase("SuperAdministrador"));

        // Redirigir si no tiene permisos
        if (!isSuperAdmin && isEditingElevatedRole && currentUser.getUser_id() != userId) {
            return "redirect:/public/403";
        }

        // Obtener los tipos de identificación para el formulario
        List<IdentificationTypes> identificationTypes = usersService.getAllIdentificationTypes();

        // Obtener roles disponibles para el usuario autenticado
        List<Roles> allRoles = usersService.getAllRoles();
        if (!isSuperAdmin) {
            allRoles = allRoles.stream()
                    .filter(role -> !role.getName().equalsIgnoreCase("SuperAdministrador"))
                    .toList(); // Excluir roles elevados para usuarios no SuperAdministradores
        }

        model.addAttribute("user", userToEdit);
        model.addAttribute("identificationTypes", identificationTypes);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("isSuperAdmin", isSuperAdmin);

        return "gestionUsuarios/updateUser"; // Vista para actualizar usuario
    }


    /**
     * Actualiza la información de un usuario existente.
     * Este método maneja la actualización de datos como el nombre, apellido, teléfono, y roles de un usuario.
     * Solo los SuperAdministradores pueden modificar ciertos campos como el correo electrónico.
     *
     * @param userId El ID del usuario a actualizar.
     * @param updatedUser El objeto que contiene los datos actualizados del usuario.
     * @param result Los resultados de validación del formulario.
     * @param roleIds Los IDs de los roles seleccionados para el usuario.
     * @param model El modelo para pasar atributos a la vista.
     * @return La redirección a la vista de detalles del usuario o el formulario de edición si ocurre un error.
     */
    @PostMapping("/edit/{userId}")
    public String updateUser(
            @PathVariable("userId") int userId,
            @Valid @ModelAttribute("user") Users updatedUser,
            BindingResult result,
            @RequestParam(name = "roleIds", required = false) List<Integer> roleIds,
            Model model) {

        // Buscar el usuario a actualizar
        Users existingUser = usersService.findById(userId);
        if (existingUser == null) {
            return "redirect:/admin/users/gestionarUsuarios";
        }

        // Verificar si el usuario en sesión es SuperAdministrador
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_SUPERADMIN"));
        model.addAttribute("isSuperAdmin", isSuperAdmin);

        // Cargar tipos de identificación
        List<IdentificationTypes> identificationTypes = identificationTypeService.getAllIdentificationTypes();
        model.addAttribute("identificationTypes", identificationTypes);

        // Cargar todos los roles
        List<Roles> allRoles = rolesService.getAllRoles();
        model.addAttribute("allRoles", allRoles);

        // Preparar los roles (asignar roles seleccionados)
        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = existingUser.getRoles().stream()
                    .map(role -> role.getRole_id())
                    .collect(Collectors.toList());
        }

        // Obtener los roles seleccionados
        List<Roles> roles = rolesService.getRolesByIds(roleIds);
        updatedUser.setRoles(roles);
        model.addAttribute("roles", roles);

        // Actualizar otros datos del usuario
        existingUser.setName(updatedUser.getName());
        existingUser.setLast_name(updatedUser.getLast_name());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setDocumentNum(updatedUser.getDocumentNum());
        existingUser.setIdentification_type_id(updatedUser.getIdentification_type_id());
        existingUser.setEnabled(updatedUser.isEnabled());

        // Actualizar email solo si es SuperAdministrador y el email es diferente
        if (isSuperAdmin && !existingUser.getEmail().equals(updatedUser.getEmail())) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Actualizar roles
        existingUser.setRoles(updatedUser.getRoles());

        // Guardar usuario actualizado
        try {
            usersService.updateUser(existingUser);
            return "redirect:/admin/users/userDetails/" + userId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al actualizar el usuario: " + e.getMessage());

            // Mantener los datos para el formulario
            model.addAttribute("user", updatedUser);

            return "gestionUsuarios/updateUser";
        }
    }
}





