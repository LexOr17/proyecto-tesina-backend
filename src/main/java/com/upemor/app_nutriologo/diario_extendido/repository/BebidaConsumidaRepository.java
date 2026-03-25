package com.upemor.app_nutriologo.diario_extendido.repository;

import com.upemor.app_nutriologo.diario_extendido.entity.BebidaConsumida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BebidaConsumidaRepository extends JpaRepository<BebidaConsumida, Long> {
}
