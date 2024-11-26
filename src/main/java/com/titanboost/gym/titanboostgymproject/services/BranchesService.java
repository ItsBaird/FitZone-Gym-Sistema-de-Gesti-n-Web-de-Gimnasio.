package com.titanboost.gym.titanboostgymproject.services;


import com.titanboost.gym.titanboostgymproject.models.Branches;
import com.titanboost.gym.titanboostgymproject.repositories.BranchesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que proporciona operaciones relacionadas con la entidad {@link Branches}.
 */
@Service
public class BranchesService {

    @Autowired
    private BranchesRepository branchesRepository;

    /**
     * Recupera todos los registros de las sedes.
     *
     * @return una lista de todas las sedes disponibles en la base de datos.
     */
    //Mostrar todos los registros
    public List<Branches> getAll() {
        return branchesRepository.findAll();
    }

}
