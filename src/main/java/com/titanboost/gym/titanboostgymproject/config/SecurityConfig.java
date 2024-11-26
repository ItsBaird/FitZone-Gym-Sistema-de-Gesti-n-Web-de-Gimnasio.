package com.titanboost.gym.titanboostgymproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad de la aplicación utilizando Spring Security.
 * <p>
 * Esta clase configura la seguridad web de la aplicación, incluyendo la protección de rutas, la configuración
 * de la autenticación, y la gestión de los fallos de autenticación. También se establece el mecanismo de codificación
 * de contraseñas y las reglas para el acceso a las distintas rutas según los roles de los usuarios.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    /**
     * Constructor que inyecta el manejador de fallos de autenticación personalizado.
     *
     * @param authenticationFailureHandler El manejador personalizado de fallos de autenticación
     */
    public SecurityConfig(CustomAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    /**
     * Configura el filtro de seguridad para manejar las solicitudes HTTP y establecer reglas de autorización.
     * <p>
     * Este método configura las rutas que serán accesibles sin autenticación y aquellas que requieren un rol
     * específico para ser accedidas. También se establece la configuración del formulario de inicio de sesión
     * y la página de error en caso de denegación de acceso.
     * </p>
     *
     * @param http El objeto {@link HttpSecurity} que se configura para establecer las reglas de seguridad.
     * @return Un {@link SecurityFilterChain} con las configuraciones de seguridad.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.
                authorizeRequests(auth -> auth
                        .requestMatchers("/static/**","/img/**","/javascript/**","/css/**","/uploads/**","uploads/news/**").permitAll()
                        .requestMatchers("/","/login","/formRegister", "/register","/public/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("Administrador","SuperAdministrador")
                        .requestMatchers("/user/**").hasAnyRole("Administrador","Usuario","SuperAdministrador")
                        .requestMatchers("/superAdmin/**").hasRole("SuperAdministrador")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/",true)
                        .failureUrl("/login?error=true")
                        .failureHandler(authenticationFailureHandler) // Asignamos el manejador de fallos
                        .permitAll()

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL para el logout
                        .logoutSuccessUrl("/login?logout")  // Redirección tras el logout
                        .deleteCookies("JSESSIONID")  // Eliminar cookies
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/public/403")
                )
                .build();
    }

    /**
     * Configura el codificador de contraseñas para usar BCrypt.
     * <p>
     * Este método configura el algoritmo BCrypt para la codificación de contraseñas de los usuarios.
     * </p>
     *
     * @return Un objeto {@link PasswordEncoder} que implementa la codificación BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
