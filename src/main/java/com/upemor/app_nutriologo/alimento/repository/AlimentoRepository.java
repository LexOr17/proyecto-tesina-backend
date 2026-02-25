package com.upemor.app_nutriologo.alimento.repository;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlimentoRepository extends JpaRepository<Alimento, Long> {
    Optional<Alimento> findByNombre(String nombre);
    List<Alimento> findByCategoria(String categoria);
    boolean existsByNombre(String nombre);
}
