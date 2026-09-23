package com.upemor.app_nutriologo.config;

import com.upemor.app_nutriologo.administrador.entity.Administrador;
import com.upemor.app_nutriologo.usuario.enums.Genero;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Componente de inicialización de datos del sistema que se ejecuta durante el arranque de la aplicación.
 * <p>
 * Implementa {@link CommandLineRunner} para verificar automáticamente la existencia
 * de una cuenta de administrador principal en la base de datos. Si no existe, crea y
 * persiste un usuario con rol {@link Rol#ADMINISTRADOR} y privilegios de {@code SUPER_ADMIN},
 * garantizando el acceso inicial a la plataforma.
 * </p>
 * <p>
 * Los datos del administrador inicial se configuran desde {@code application.yaml}
 * bajo la sección {@code app.admin}, lo que permite modificarlos sin recompilar el proyecto.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see CommandLineRunner
 * @see Administrador
 * @see UsuarioRepository
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    /**
     * Repositorio para la verificación y persistencia de usuarios.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Codificador de contraseñas para encriptar la contraseña inicial con BCrypt.
     */
    private final PasswordEncoder passwordEncoder;

    /** Correo del administrador inicial, configurable desde {@code app.admin.correo}. */
    @Value("${app.admin.correo}")
    private String adminCorreo;

    /** Contraseña en texto plano del administrador inicial, configurable desde {@code app.admin.password}. */
    @Value("${app.admin.password}")
    private String adminPassword;

    /** Nombre del administrador inicial, configurable desde {@code app.admin.nombre}. */
    @Value("${app.admin.nombre}")
    private String adminNombre;

    /** Apellido paterno del administrador inicial, configurable desde {@code app.admin.apellido-paterno}. */
    @Value("${app.admin.apellido-paterno}")
    private String adminApellidoPaterno;

    /** Teléfono del administrador inicial, configurable desde {@code app.admin.telefono}. */
    @Value("${app.admin.telefono}")
    private String adminTelefono;

    /**
     * Método ejecutado tras la inicialización del contexto de Spring Boot.
     * <p>
     * Comprueba si el correo configurado en {@code app.admin.correo} ya se encuentra registrado.
     * En caso negativo, instancia una entidad {@link Administrador} con los datos leídos desde
     * {@code application.yaml}, encripta su contraseña y la guarda en la base de datos.
     * </p>
     *
     * @param args argumentos pasados por línea de comandos al arrancar la aplicación.
     * @throws Exception si ocurre algún error durante la inserción de los datos iniciales.
     */
    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByCorreo(adminCorreo)) {
            Administrador admin = new Administrador();
            admin.setNombre(adminNombre);
            admin.setApellidoPaterno(adminApellidoPaterno);
            admin.setCorreo(adminCorreo);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRol(Rol.ADMINISTRADOR);
            admin.setGenero(Genero.MASCULINO);
            admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            admin.setFechaRegistro(LocalDateTime.now());
            admin.setTelefono(adminTelefono);
            admin.setTipoAdmin("SUPER_ADMIN");

            usuarioRepository.save(admin);
        }
    }
}

