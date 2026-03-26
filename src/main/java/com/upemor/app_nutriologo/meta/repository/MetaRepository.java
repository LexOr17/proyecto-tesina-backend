package com.upemor.app_nutriologo.meta.repository;

import com.upemor.app_nutriologo.meta.entity.Meta;
import com.upemor.app_nutriologo.meta.enums.EstadoMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {

    // Búsqueda principal para el listado del paciente/nutriólogo
    Page<Meta> findByPacienteId(Long pacienteId, Pageable pageable);

    // Listado por nutriólogo
    Page<Meta> findByNutriologoId(Long nutriologoId, Pageable pageable);

    // CORREGIDO: Filtro por estado (CUMPLIDA, PENDIENTE, etc.)
    Page<Meta> findByPacienteIdAndEstado(Long pacienteId, EstadoMeta estado, Pageable pageable);

    // Validación de seguridad
    Optional<Meta> findByIdAndPacienteId(Long id, Long pacienteId);

    // CORREGIDO: Conteo de metas según su estado
    // Ejemplo de uso: countByPacienteIdAndEstado(id, EstadoMeta.PENDIENTE)
    long countByPacienteIdAndEstado(Long pacienteId, EstadoMeta estado);

    // Conteo de metas por estado para un paciente específico
    @Query("SELECT m.estado, COUNT(m) FROM Meta m WHERE m.paciente.id = :pacienteId GROUP BY m.estado")
    List<Object[]> countMetasByStatusForPaciente(@Param("pacienteId") Long pacienteId);
}
