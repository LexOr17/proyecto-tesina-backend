package com.upemor.app_nutriologo.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/backup")
public class DatabaseController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/descarga")
    public ResponseEntity<Resource> backupDatabase() {
        logger.info("Solicitud de respaldo de base de datos recibida.");
        Resource resource = null;
        try {
            resource = databaseService.backup();
            Path path = resource.getFile().toPath();

            ByteArrayResource byteArrayResource = new ByteArrayResource(Files.readAllBytes(path));
            Files.delete(path);
            logger.info("Respaldo {} enviado y eliminado del servidor.", resource.getFilename());


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(byteArrayResource.contentLength())
                    .body(byteArrayResource);
        } catch (Exception e) {
            logger.error("Error durante la solicitud de respaldo.", e);
            if (resource != null && resource.exists()) {
                try {
                    Files.delete(resource.getFile().toPath());
                    logger.warn("Archivo de respaldo temporal {} eliminado después de un error.", resource.getFilename());
                } catch (Exception ex) {
                    logger.error("Error al eliminar el archivo de respaldo temporal {} después de un fallo.", resource.getFilename(), ex);
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/restaurar")
    public ResponseEntity<Void> restoreDatabase(@RequestParam("file") MultipartFile file) {
        logger.info("Solicitud de restauración de base de datos recibida con el archivo: {}", file.getOriginalFilename());
        try {
            databaseService.restore(file);
            logger.info("Restauración completada con éxito.");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error durante la solicitud de restauración.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
