package com.upemor.app_nutriologo.sugerencia_plantilla.repository;

import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SugerenciaPlantillaRepository extends JpaRepository<SugerenciaPlantilla, Long> {

    /** Sugerencias enviadas por un paciente concreto */
    Page<SugerenciaPlantilla> findByPacienteId(Long pacienteId, Pageable pageable);

    /** Todas las sugerencias recibidas por un nutriólogo */
    Page<SugerenciaPlantilla> findByNutriologoId(Long nutriologoId, Pageable pageable);

    /** Sugerencias filtradas por nutriólogo y estado (ej. PENDIENTE) */
    Page<SugerenciaPlantilla> findByNutriologoIdAndEstado(Long nutriologoId, EstadoSugerencia estado,
            Pageable pageable);
}
