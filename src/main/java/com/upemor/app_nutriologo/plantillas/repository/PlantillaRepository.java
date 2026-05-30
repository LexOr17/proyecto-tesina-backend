package com.upemor.app_nutriologo.plantillas.repository;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

    // Para que el nutriólogo vea sus propias plantillas creadas
    List<Plantilla> findByUsuarioId(Long usuarioId);

    // Versión paginada (recomendada para el frontend)
    Page<Plantilla> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT p FROM Plantilla p WHERE " +
           "(:keyword IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:tipoComida IS NULL OR p.tipoComida = :tipoComida)")
    Page<Plantilla> buscarPorNombreYTipoComida(@org.springframework.data.repository.query.Param("keyword") String keyword, 
                                               @org.springframework.data.repository.query.Param("tipoComida") com.upemor.app_nutriologo.plantillas.enums.TipoComida tipoComida, 
                                               Pageable pageable);
}