package com.upemor.app_nutriologo.plantillas.repository;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {
}
