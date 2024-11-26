package com.titanboost.gym.titanboostgymproject.services;

import com.titanboost.gym.titanboostgymproject.models.*;
import com.titanboost.gym.titanboostgymproject.repositories.IdentificationTypesRepository;
import com.titanboost.gym.titanboostgymproject.repositories.UsersRepository;
import com.titanboost.gym.titanboostgymproject.repositories.Users_MembershipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los usuarios.
 * <p>
 * Esta clase implementa el servicio de detalles de usuario {@link UserDetailsService} para integrarse con
 * Spring Security, permitiendo la autenticación de usuarios. Además, contiene métodos para registrar, actualizar
 * y consultar usuarios, así como para manejar sus membresías activas.
 * </p>
 */
@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private Users_MembershipsRepository usersMembershipsRepository;

    @Autowired
    private RolesService rolesService; // Inyectar el servicio de roles

    @Autowired
    private IdentificationTypesRepository identificationTypesRepository;

    /**
     * Carga un usuario por su dirección de correo electrónico.
     * <p>
     * Este método verifica si el usuario está habilitado y construye un {@link UserDetails} para
     * ser utilizado por Spring Security.
     * </p>
     *
     * @param email La dirección de correo electrónico del usuario.
     * @return Un objeto {@link UserDetails} para el usuario encontrado.
     * @throws UsernameNotFoundException Si el usuario no se encuentra o no está habilitado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email);

        if (user != null) {
            // Verifica si el usuario está habilitado
            if (!user.isEnabled()) {
                throw new DisabledException("Cuenta deshabilitada. Contacte con el administrador.");
            }

            // Construir el usuario de Spring Security
            var springUser = User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRoles().stream()
                            .map(Roles::getName) // Suponiendo que `Roles` tiene un método `getName`
                            .toArray(String[]::new))
                    .build();
            return springUser;
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @return El objeto {@link Users} correspondiente al correo electrónico.
     */
    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    /**
     * Busca un usuario por su número de documento.
     *
     * @param documentNum El número de documento del usuario.
     * @return El objeto {@link Users} correspondiente al número de documento.
     */
    public Users findByDocumentNum(String documentNum) {
        return usersRepository.findByDocumentNum(documentNum);
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param userId El ID del usuario.
     * @return El objeto {@link Users} correspondiente al ID.
     */
    public Users findById(int userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Una lista de todos los usuarios.
     */
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Este método convierte un {@link RegisterDto} a un objeto {@link Users}, codifica la contraseña
     * y asigna el rol predeterminado "Usuario".
     * </p>
     *
     * @param registerDto El DTO que contiene los datos del nuevo usuario.
     * @throws IllegalArgumentException Si no se encuentra el rol "Usuario".
     */
    public void registerUser(RegisterDto registerDto) {
        // Conversión de RegisterDto a Users
        Users newUser = new Users();
        newUser.setName(registerDto.getName());
        newUser.setLast_name(registerDto.getLast_name());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPhone(registerDto.getPhone());
        newUser.setDocumentNum(registerDto.getDocumentNum());
        newUser.setIdentification_type_id(registerDto.getIdentification_type_id());



        // Codificar contraseña
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Asignar el rol predeterminado
        Roles role = rolesService.findByName("Usuario");
        if (role == null) {
            throw new IllegalArgumentException("Error: No se encontró el rol 'Usuario'");
        } // Cambia a tu rol por defecto
        newUser.setRoles(List.of(role)); // Usar lista si es un `List<Roles>`

        //Habilitar el usuario
        newUser.setEnabled(true);

        usersRepository.save(newUser); // Persistir el usuario en la base de datos
    }

    /**
     * Calcula el total de días restantes de las membresías activas de un usuario.
     *
     * @param activeMemberships La lista de membresías activas del usuario.
     * @return El total de días restantes de todas las membresías activas.
     */
    public long calculateTotalDaysRemaining(List<Users_Memberships> activeMemberships) {
        long totalDaysRemaining = 0;
        for (Users_Memberships membership : activeMemberships) {
            if (membership.getExpiration_date().isAfter(LocalDateTime.now())) {
                long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), membership.getExpiration_date());
                totalDaysRemaining += daysRemaining;
            }
        }
        return totalDaysRemaining;
    }

    /**
     * Obtiene las membresías activas de un usuario.
     *
     * @param user El usuario cuyas membresías activas se desean obtener.
     * @return Una lista de las membresías activas del usuario.
     */
    public List<Users_Memberships> findActiveMembershipsByUser(Users user) {
        return usersMembershipsRepository.findByUserAndEnabledTrue(user);
    }

    /**
     * Actualiza la información de un usuario en la base de datos.
     * <p>
     * Este metodo verifica si el correo electrónico ya está en uso por otro usuario y lanza una excepción si es así.
     * con la actualización.
     * </p>
     *
     * @param user El objeto {@link Users} con los nuevos datos del usuario.
     * @throws RuntimeException Si el correo electrónico ya está en uso por otro usuario.
     */
    public void updateUser(Users user) {
        // Verificar si el email ya existe para otro usuario
        Users existingUserWithEmail = usersRepository.findByEmail(user.getEmail());
        if (existingUserWithEmail != null && existingUserWithEmail.getUser_id() != user.getUser_id()) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }

        usersRepository.save(user);
    }

    /**
     * Obtiene todos los tipos de identificación disponibles.
     *
     * @return Una lista de todos los tipos de identificación.
     */
    public List<IdentificationTypes> getAllIdentificationTypes() {
        return identificationTypesRepository.findAll();
    }

    /**
     * Obtiene todos los roles disponibles en el sistema.
     *
     * @return Una lista de todos los roles disponibles.
     */
    public List<Roles> getAllRoles() {
        return rolesService.getAllRoles();
    }

}
