package com.upemor.app_nutriologo.paciente.repository;

import com.upemor.app_nutriologo.paciente.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
