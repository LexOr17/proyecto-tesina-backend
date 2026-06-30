package com.upemor.app_nutriologo.usuario.repository;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByTelefono(String telefono);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.apellidoPaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.apellidoMaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Usuario> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByActivoTrue();
}
