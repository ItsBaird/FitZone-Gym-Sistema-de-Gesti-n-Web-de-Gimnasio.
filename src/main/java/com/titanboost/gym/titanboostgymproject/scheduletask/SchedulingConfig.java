package com.titanboost.gym.titanboostgymproject.scheduletask;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración para habilitar tareas programadas en la aplicación.
 * Esta clase configura el soporte para la ejecución de tareas programadas utilizando
 * anotaciones como {@code @Scheduled} en Spring.
 * Es requerida para activar la programación de tareas periódicas.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
