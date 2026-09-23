package com.upemor.app_nutriologo.medidas_antropometricas.repository;

import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedidaAntropometricaRepository extends JpaRepository<MedidaAntropometrica, Long> {

    List<MedidaAntropometrica> findByPacienteIdOrderByFechaMedicionAsc(Long pacienteId);

    List<MedidaAntropometrica> findByPacienteIdAndFechaMedicionBetweenOrderByFechaMedicionAsc(Long pacienteId, java.time.LocalDate inicio, java.time.LocalDate fin);

    Optional<MedidaAntropometrica> findTopByPacienteIdOrderByFechaMedicionDesc(Long pacienteId);

}
