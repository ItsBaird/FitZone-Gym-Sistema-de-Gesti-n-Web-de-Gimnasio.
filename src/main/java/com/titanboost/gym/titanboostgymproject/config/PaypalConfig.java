package com.titanboost.gym.titanboostgymproject.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de PayPal para la integración con la API de PayPal.
 * <p>
 * Esta clase proporciona la configuración necesaria para autenticar y conectar con la API de PayPal
 * utilizando las credenciales (ID de cliente y secreto) y el modo de operación especificados en el archivo
 * de propiedades. El método `apiContext` crea y configura un {@link APIContext} que se utilizará para
 * realizar las operaciones de pago con la API de PayPal.
 * </p>
 */
@Configuration
public class PaypalConfig {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;


    /**
     * Crea y configura un contexto de API para la integración con PayPal.
     * <p>
     * Este método utiliza las credenciales del cliente y el modo de operación (sandbox) proporcionado
     * en el archivo de propiedades para crear un {@link APIContext}. Este contexto es esencial para interactuar
     * con los servicios de PayPal y realizar transacciones de pago.
     * </p>
     *
     * @return Un objeto {@link APIContext} que contiene las credenciales y configuraciones necesarias para
     *         interactuar con la API de PayPal.
     */
    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
