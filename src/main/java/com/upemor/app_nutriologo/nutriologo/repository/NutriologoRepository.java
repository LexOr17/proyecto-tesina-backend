package com.upemor.app_nutriologo.nutriologo.repository;

import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NutriologoRepository extends JpaRepository<Nutriologo, Long> {

    @Query("SELECT new com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO(n.id, n.nombre, n.apellidoPaterno) FROM Nutriologo n")
    List<NutriologoSelectorDTO> selectorNutriologo(Sort nombre);

}
