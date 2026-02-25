package com.upemor.app_nutriologo.paciente.repository;

import com.upemor.app_nutriologo.paciente.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    @Query("SELECT p FROM Paciente p WHERE p.nutriologo.id = :nutriologoId")
    Page<Paciente> findByNutriologoId(@Param("nutriologoId") Long nutriologoId, Pageable pageable);
}
