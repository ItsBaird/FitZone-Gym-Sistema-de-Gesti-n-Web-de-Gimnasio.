package com.titanboost.gym.titanboostgymproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO (Data Transfer Object) para registrar un nuevo usuario.
 * Contiene los datos necesarios para realizar el registro en el sistema, con validaciones incluidas.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    /**
     * Nombre del usuario.
     * Es obligatorio y debe contener solo letras y espacios, entre 2 y 100 caracteres.
     */
    @NotBlank(message = "El nombre es requerido")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$", message = "El nombre debe contener solo letras y espacios, entre 2 y 100 caracteres")
    private String name;

    /**
     * Apellido del usuario.
     * Es obligatorio y debe contener solo letras y espacios, entre 2 y 100 caracteres.
     */
    @NotBlank(message = "El apellido es requerido")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$", message = "El apellido debe contener solo letras y espacios, entre 2 y 100 caracteres")
    private String last_name;

    /**
     * Correo electrónico del usuario.
     * Es obligatorio y debe tener un formato válido de correo electrónico.
     */
    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;

    /**
     * Número de teléfono del usuario.
     * Es obligatorio y debe contener exactamente 10 dígitos.
     */
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9]{10}$", message = "El número de teléfono debe contener 10 dígitos")
    private String phone;

    /**
     * Contraseña del usuario.
     * Es obligatorio y debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales.
     */
    @NotBlank(message = "La contraseña es requerida")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y caracteres especiales")
    private String password;

    /**
     * Confirmación de la contraseña del usuario.
     * Es obligatoria y debe coincidir con la contraseña.
     */
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String passwordConfirm;

    /**
     * Número de documento del usuario.
     * Es obligatorio y debe tener entre 6 y 10 dígitos.
     */
    @NotBlank(message = "El número de documento es requerido")
    @Pattern(regexp = "^[0-9]{6,10}$", message = "El número de documento debe tener entre 6 y 10 dígitos")
    private String documentNum;

    /**
     * Tipo de identificación del usuario.
     * Es obligatorio y debe ser una referencia válida a {@link IdentificationTypes}.
     */
    @NotNull(message = "El tipo de identificación es requerido")
    private IdentificationTypes identification_type_id;
}
