package com.upemor.app_nutriologo.sugerencia_plantilla.service;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaRequestDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaRevisionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SugerenciaPlantillaService {

    /** PACIENTE: envía una sugerencia nueva o basada en una plantilla existente */
    SugerenciaPlantillaResponseDTO enviarSugerencia(SugerenciaPlantillaRequestDTO requestDTO);

     /**
     * PACIENTE: edita una sugerencia propia que aún está en estado PENDIENTE.
     * Reemplaza por completo nombre, tipo de comida, motivo y lista de alimentos.
     */
    SugerenciaPlantillaResponseDTO actualizarSugerencia(Long id, SugerenciaPlantillaRequestDTO requestDTO);

    /**
     * PACIENTE: devuelve las plantillas del plan alimenticio activo del paciente
     * autenticado, para que pueda seleccionar una como base para su sugerencia.
     */
    List<PlantillaResponseDTO> obtenerPlantillasDelPlanActual();

    /** PACIENTE: consulta su propio historial de sugerencias */
    Page<SugerenciaPlantillaResponseDTO> obtenerSugerenciasDePaciente(Pageable pageable);

    /** NUTRIÓLOGO: ve todas las sugerencias recibidas de sus pacientes */
    Page<SugerenciaPlantillaResponseDTO> obtenerSugerenciasParaNutriologo(Pageable pageable);

    /** NUTRIÓLOGO: filtra sólo las sugerencias con estado PENDIENTE */
    Page<SugerenciaPlantillaResponseDTO> obtenerPendientesParaNutriologo(Pageable pageable);

    /** AMBOS: detalle de una sugerencia concreta */
    SugerenciaPlantillaResponseDTO obtenerSugerenciaPorId(Long id);

    /** NUTRIÓLOGO: aprueba o rechaza una sugerencia */
    SugerenciaPlantillaResponseDTO revisarSugerencia(Long id, SugerenciaRevisionDTO revisionDTO);

    /** PACIENTE: cancela su propia sugerencia mientras siga en PENDIENTE */
    void eliminarSugerencia(Long id);
}

