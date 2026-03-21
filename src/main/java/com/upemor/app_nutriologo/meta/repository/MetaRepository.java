package com.upemor.app_nutriologo.meta.repository;

import com.upemor.app_nutriologo.meta.entity.Meta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Integer> {

    // NUEVO: Fundamental para que el paciente o el nutriólogo vean solo las metas que corresponden
    Page<Meta> findByPacienteId(Long pacienteId, Pageable pageable);

    // NUEVO (Opcional pero útil): Por si el nutriólogo quiere ver "todas las metas que he asignado hoy"
    Page<Meta> findByNutriologoId(Long nutriologoId, Pageable pageable);
}
