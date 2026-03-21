package com.upemor.app_nutriologo.plantillas.repository;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

    // Para que el nutriólogo vea sus propias plantillas creadas
    List<Plantilla> findByUsuarioId(Long usuarioId);

    // Versión paginada (recomendada para el frontend)
    Page<Plantilla> findByUsuarioId(Long usuarioId, Pageable pageable);
}