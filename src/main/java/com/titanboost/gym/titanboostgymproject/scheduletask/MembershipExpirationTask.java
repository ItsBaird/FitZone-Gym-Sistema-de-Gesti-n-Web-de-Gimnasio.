package com.titanboost.gym.titanboostgymproject.scheduletask;

import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_Memberships;
import com.titanboost.gym.titanboostgymproject.repositories.Users_MembershipsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tarea programada para gestionar la expiración de membresías de usuarios.
 * Esta clase revisa periódicamente las membresías expiradas y las desactiva
 * automáticamente. La tarea se ejecuta a través de un cron job configurado para
 * ejecutarse todos los días a la medianoche.
 */
@Component
public class MembershipExpirationTask {

    /**
     * Repositorio para gestionar las operaciones de la entidad {@link Users_Memberships}.
     */
    @Autowired
    private Users_MembershipsRepository users_membershipsRepository;

    /**
     * Tarea programada para verificar y desactivar las membresías expiradas.
     * Este método busca todas las membresías activas cuya fecha de expiración
     * haya pasado y las marca como desactivadas.
     *
     * <p><b>Cron:</b> Se ejecuta todos los días a medianoche (00:00:00) según la
     * configuración {@code "0 0 0 * * ?"}. </p>
     */
    @Scheduled(cron = "0 0 0 * * ?") // Ejecuta a medianoche cada día
    public void checkExpiredMemberships() {
        // Obtiene la fecha y hora actuales.
        LocalDateTime currentDate = LocalDateTime.now();

        // Encuentra las membresías activas que ya hayan expirado.
        List<Users_Memberships> expiredMemberships = users_membershipsRepository.findExpiredMemberships(currentDate);

        // Itera sobre las membresías expiradas, las desactiva y guarda los cambios.
        for (Users_Memberships membership : expiredMemberships) {
            membership.setEnabled(false);
            users_membershipsRepository.save(membership);
        }
    }

}
