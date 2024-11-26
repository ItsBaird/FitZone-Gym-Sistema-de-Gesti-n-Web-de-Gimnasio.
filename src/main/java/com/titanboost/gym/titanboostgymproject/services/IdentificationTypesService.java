package com.titanboost.gym.titanboostgymproject.services;

import com.titanboost.gym.titanboostgymproject.models.IdentificationTypes;
import com.titanboost.gym.titanboostgymproject.repositories.IdentificationTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de gestionar los tipos de identificación en el sistema.
 * <p>
 * Esta clase proporciona métodos para obtener todos los tipos de identificación
 * registrados y buscar un tipo de identificación por su ID.
 * </p>
 */
@Service
public class IdentificationTypesService {
    @Autowired
    private IdentificationTypesRepository identificationTypesRepository;

    /**
     * Obtiene todos los tipos de identificación registrados.
     *
     * @return Una lista de objetos {@link IdentificationTypes} que representan todos los tipos de identificación.
     */
    //Obetener todos los tipos de identificacion
    public List<IdentificationTypes> getAllIdentificationTypes() {
        return identificationTypesRepository.findAll();
    }

    /**
     * Obtiene un tipo de identificación por su ID.
     *
     * @param id El identificador único del tipo de identificación.
     * @return Un objeto {@link IdentificationTypes} si se encuentra el tipo de identificación,
     *         o {@code null} si no se encuentra un tipo con el ID proporcionado.
     */
    //Obtener un tipo de identificacion por su id
    public IdentificationTypes getIdentificationTypeById(int id) {
        return identificationTypesRepository.findById(id).orElse(null);
    }

}
