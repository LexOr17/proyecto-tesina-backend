package com.upemor.app_nutriologo.database;

import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class DatabaseService {

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    private final Path backupDir = Paths.get("backups");

    public DatabaseService() {
        try {
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
                log.info("Directorio de backups creado en: {}", backupDir.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error al crear el directorio de backups", e);
            throw new InvalidOperationException("No se pudo crear el directorio de respaldos de base de datos.", e);
        }
    }

    private String getDbName() {
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
    }

    public Resource backup() throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "backup_" + timestamp + ".sql";
        Path backupPath = this.backupDir.resolve(fileName);

        log.info("Iniciando respaldo de la base de datos en: {}", backupPath.toAbsolutePath());

        ProcessBuilder processBuilder = new ProcessBuilder(
                "mysqldump",
                "-u" + dbUsername,
                "-p" + dbPassword,
                getDbName(),
                "-r",
                backupPath.toString()
        );
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            log.info("Respaldo de la base de datos completado con éxito.");
            return new UrlResource(backupPath.toUri());
        } else {
            log.error("Error al crear el respaldo de la base de datos. Código de salida: {}", exitCode);
            throw new InvalidOperationException("Error durante el proceso de respaldo de la base de datos (mysqldump).");
        }
    }

    public void restore(MultipartFile file) throws Exception {
        Path restoreFile = this.backupDir.resolve("restore.sql");
        log.info("Iniciando restauración desde el archivo: {}", file.getOriginalFilename());
        file.transferTo(restoreFile);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "mysql",
                "-u" + dbUsername,
                "-p" + dbPassword,
                getDbName()
        );
        processBuilder.redirectInput(restoreFile.toFile());
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        Files.delete(restoreFile);

        if (exitCode == 0) {
            log.info("Restauración de la base de datos completada con éxito.");
        } else {
            log.error("Error al restaurar la base de datos. Código de salida: {}", exitCode);
            throw new InvalidOperationException("Error durante el proceso de restauración de la base de datos (mysql).");
        }
    }
}
