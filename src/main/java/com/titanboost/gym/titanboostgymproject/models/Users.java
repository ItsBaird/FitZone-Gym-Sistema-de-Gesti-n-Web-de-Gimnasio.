package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa a un usuario en el sistema.
 * Un usuario tiene información personal, como nombre, correo electrónico, teléfono, y puede tener múltiples roles y membresías asociadas.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    /**
     * Identificador único del usuario.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    /**
     * Nombre del usuario.
     * Debe contener solo letras y espacios, con una longitud entre 2 y 100 caracteres.
     */
    @NotBlank(message = "El nombre es requerido")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$", message = "El nombre debe contener solo letras y espacios, entre 2 y 100 caracteres")
    private String name;

    /**
     * Apellido del usuario.
     * Debe contener solo letras y espacios, con una longitud entre 2 y 100 caracteres.
     */
    @NotBlank(message = "El apellido es requerido")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$", message = "El apellido debe contener solo letras y espacios, entre 2 y 100 caracteres")
    private String last_name;

    /**
     * Correo electrónico del usuario.
     * Debe tener un formato válido de dirección de correo electrónico.
     */
    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;

    /**
     * Número de teléfono del usuario.
     * Debe contener solo números y tener una longitud de 10 dígitos.
     */
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9]{10}$", message = "Solo se aceptan números en el teléfono y debe contener 10 dígitos")
    private String phone;

    /**
     * Contraseña del usuario.
     * Debe tener al menos 8 caracteres e incluir mayúsculas, minúsculas, números y caracteres especiales.
     */
    @NotBlank(message = "La contraseña es requerida")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y caracteres especiales")
    private String password;

    /**
     * Número de documento del usuario.
     * Debe contener entre 6 y 10 dígitos numéricos.
     */
    @NotBlank(message = "El número de documento es requerido")
    @Pattern(regexp = "^[0-9]{6,10}$", message = "El número de documento debe tener entre 6 y 10 dígitos")
    private String documentNum;

    /**
     * Tipo de identificación del usuario.
     * Se relaciona con la entidad {@link IdentificationTypes}.
     */
    @NotNull(message = "El tipo de identificación es requerido")
    @ManyToOne
    @JoinColumn(name = "identification_type_id", referencedColumnName = "identification_type_id")
    private IdentificationTypes identification_type_id;

    /**
     * Estado de habilitación del usuario.
     * Si está en falso, el usuario no podrá iniciar sesión.
     */
    private boolean enabled;

    /**
     * Lista de roles asignados al usuario.
     * Los roles determinan los permisos y responsabilidades dentro del sistema.
     * La relación es de muchos a muchos con la entidad {@link Roles}.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private List<Roles> roles;

    /**
     * Lista de membresías asociadas al usuario.
     * Un usuario puede tener varias membresías a través de la relación con {@link Users_Memberships}.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Users_Memberships> memberships = new HashSet<>();

}
