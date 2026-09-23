package com.upemor.app_nutriologo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración web de Spring MVC para la gestión y exposición de recursos estáticos.
 * <p>
 * Implementa {@link WebMvcConfigurer} para mapear la ruta pública de acceso a archivos
 * multimedia y documentos subidos (fotografías de perfil, archivos adjuntos de mensajes, etc.)
 * hacia la ubicación física en el sistema de archivos del servidor.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see WebMvcConfigurer
 * @see ResourceHandlerRegistry
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Directorio de almacenamiento de archivos subidos en el servidor.
     * <p>
     * Se inyecta desde la propiedad de configuración {@code file.upload-dir}
     * con un valor por defecto de {@code ./uploads}.
     * </p>
     */
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * Registra los manejadores de recursos estáticos de Spring MVC.
     * <p>
     * Vincula el patrón de URL {@code /uploads/**} con la ruta absoluta del directorio
     * en disco donde residen los archivos subidos ({@code file:/.../uploads/}).
     * </p>
     *
     * @param registry el registro de manejadores de recursos de Spring MVC.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
    }
}

