package com.titanboost.gym.titanboostgymproject.services;

import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.titanboost.gym.titanboostgymproject.repositories.MembershipPlansRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar los planes de membresía de la aplicación.
 * <p>
 * Este servicio ofrece métodos para obtener todos los planes de membresía, agregar nuevos planes,
 * obtener un plan específico por su ID, actualizar planes existentes y buscar planes por nombre.
 * </p>
 */
@Service
public class MembershipPlansService {

    @Autowired
    private MembershipPlansRepository membershipPlansRepository;

    /**
     * Obtiene todos los planes de membresía almacenados en la base de datos.
     *
     * @return Una lista de objetos {@link MembershipPlans} que representan todos los planes de membresía.
     */
    public List<MembershipPlans> getAllPlans() {
        return membershipPlansRepository.findAll();
    }

    /**
     * Agrega un nuevo plan de membresía a la base de datos.
     *
     * @param membershipPlan El objeto {@link MembershipPlans} que representa el nuevo plan de membresía.
     */
    public void addPlan(MembershipPlans membershipPlan) {
        membershipPlansRepository.save(membershipPlan);
    }


    /**
     * Obtiene un plan de membresía a partir de su ID.
     *
     * @param id El ID del plan de membresía a buscar.
     * @return Un {@link Optional} que contiene el plan de membresía encontrado si existe, o {@link Optional#empty()} si no se encuentra.
     */
    public Optional<MembershipPlans> getPlanById(int id) {

        return membershipPlansRepository.findById(id);
    }


    /**
     * Actualiza un plan de membresía existente en la base de datos.
     *
     * @param membershipPlan El objeto {@link MembershipPlans} con los datos actualizados que se desean guardar.
     */
    public void updatePlan(MembershipPlans membershipPlan) {
        membershipPlansRepository.save(membershipPlan); // Guardar los cambios en la base de datos
    }


    /**
     * Busca planes de membresía por nombre, ignorando mayúsculas y minúsculas.
     *
     * @param searchTerm El término de búsqueda utilizado para encontrar planes de membresía.
     * @return Una lista de objetos {@link MembershipPlans} que contienen el término de búsqueda en su nombre.
     */
    public List<MembershipPlans> searchMembershipPlansByName(String searchTerm) {
        return membershipPlansRepository.findByNameContainingIgnoreCase(searchTerm);
    }

}
