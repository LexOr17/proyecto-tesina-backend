package com.upemor.app_nutriologo.notificaciones.repository;

import com.upemor.app_nutriologo.notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
