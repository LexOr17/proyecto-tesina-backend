package com.upemor.app_nutriologo.administrador.repository;

import com.upemor.app_nutriologo.administrador.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de acceso a datos para la entidad {@link Administrador}.
 * <p>
 * Proporciona métodos CRUD estándar heredados de {@link JpaRepository} para la gestión de usuarios administradores.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

}
