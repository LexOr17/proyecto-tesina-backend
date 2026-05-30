package com.upemor.app_nutriologo.alimento.repository;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlimentoRepository extends JpaRepository<Alimento, Long> {
    Optional<Alimento> findByNombre(String nombre);
    List<Alimento> findByCategoria(String categoria);
    boolean existsByNombre(String nombre);
    
    @Query("SELECT a FROM Alimento a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.categoria) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Alimento> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
