package com.upemor.app_nutriologo.notificaciones.repository;

import com.upemor.app_nutriologo.notificaciones.entity.NotificacionUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificacionUsuarioRepository extends JpaRepository<NotificacionUsuario, Long> {

       /**
        * Obtiene todas las notificaciones de un usuario, ordenadas de más reciente a
        * más antigua.
        */
       Page<NotificacionUsuario> findByDestinatarioIdOrderByCreadoEnDesc(Long destinatarioId, Pageable pageable);

       /**
        * Cuenta las notificaciones no leídas de un usuario (para el badge del
        * frontend).
        */
       long countByDestinatarioIdAndLeidaFalse(Long destinatarioId);

       /**
        * Marca todas las notificaciones no leídas de un usuario como leídas en una
        * sola query.
        */
       @Modifying
       @Query("UPDATE NotificacionUsuario nu SET nu.leida = true, nu.fechaLeido = :ahora " +
                     "WHERE nu.destinatario.id = :usuarioId AND nu.leida = false")
       int marcarTodasLeidasPorUsuario(@Param("usuarioId") Long usuarioId,
                     @Param("ahora") LocalDateTime ahora);
}
