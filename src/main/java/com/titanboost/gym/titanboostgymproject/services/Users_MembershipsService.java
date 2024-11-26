package com.titanboost.gym.titanboostgymproject.services;

import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_Memberships;
import com.titanboost.gym.titanboostgymproject.models.Users_MembershipsDto;
import com.titanboost.gym.titanboostgymproject.repositories.MembershipPlansRepository;
import com.titanboost.gym.titanboostgymproject.repositories.UsersRepository;
import com.titanboost.gym.titanboostgymproject.repositories.Users_MembershipsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar las membresías de los usuarios.
 * <p>
 * Este servicio permite crear nuevas membresías para los usuarios, obtener las membresías activas
 * de un usuario específico y deshabilitar una membresía existente. Utiliza los repositorios de
 * {@link Users_MembershipsRepository}, {@link MembershipPlansRepository} y {@link UsersRepository}
 * para interactuar con la base de datos.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class Users_MembershipsService {

    private final Users_MembershipsRepository usersMembershipsRepository;
    private final MembershipPlansRepository membershipPlansRepository;
    private final UsersRepository usersRepository;
    private final Users_MembershipsRepository users_MembershipsRepository;

    /**
     * Crea una nueva membresía para un usuario.
     * <p>
     * Este metodo valida que el usuario y el plan de membresía existan, y luego crea
     * una nueva instancia de {@link Users_Memberships} en la base de datos.
     * </p>
     *
     * @param usersMembershipsDto El DTO que contiene los detalles de la membresía a crear.
     * @throws IllegalArgumentException Si el usuario o el plan de membresía no existen.
     */
    public void createMembership(Users_MembershipsDto usersMembershipsDto) {
        // Validar que el usuario existe
        Users user = usersRepository.findById(usersMembershipsDto.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar que el plan de membresía existe
        MembershipPlans membershipPlan = membershipPlansRepository.findById(usersMembershipsDto.getMembership_id())
                .orElseThrow(() -> new IllegalArgumentException("Plan de membresía no encontrado"));

        // Crear la entidad Users_Memberships
        Users_Memberships usersMemberships = new Users_Memberships();
        usersMemberships.setUser(user);
        usersMemberships.setMembership(membershipPlan);
        usersMemberships.setPurchase_date(usersMembershipsDto.getPurchase_date());
        usersMemberships.setExpiration_date(usersMembershipsDto.getExpiration_date());
        usersMemberships.setEnabled(true);  // Se asume que la membresía está habilitada al momento de la compra

        // Guardar en la base de datos
        usersMembershipsRepository.save(usersMemberships);
    }

    /**
     * Obtiene las membresías activas de un usuario.
     *
     * @param user El usuario cuyo historial de membresías activas se desea obtener.
     * @return Una lista de objetos {@link Users_Memberships} correspondientes a las membresías activas del usuario.
     */
    public List<Users_Memberships> findActiveMembershipsByUser(Users user) {
        return usersMembershipsRepository.findByUserAndEnabledTrue(user);
    }

    /**
     * Desactiva una membresía de usuario.
     * <p>
     * Este méodo cambiará el estado de la membresía a desactivada, lo que significa que el usuario ya no podrá utilizarla.
     * </p>
     *
     * @param userMembershipId El ID de la membresía de usuario que se desea desactivar.
     * @throws IllegalArgumentException Si la membresía no es encontrada.
     */
    public void disableMembership(int userMembershipId) {
        // Buscar la membresía
        Users_Memberships userMembership = usersMembershipsRepository.findById(userMembershipId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada"));

        // Cambiar su estado a inhabilitada
        userMembership.setEnabled(false);

        // Guardar los cambios en la base de datos
        usersMembershipsRepository.save(userMembership);
    }
}
