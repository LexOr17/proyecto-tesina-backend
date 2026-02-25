package com.upemor.app_nutriologo.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

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
                logger.info("Directorio de backups creado en: {}", backupDir.toAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Error al crear el directorio de backups", e);
            throw new RuntimeException("Could not create backup directory!", e);
        }
    }

    private String getDbName() {
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
    }

    public Resource backup() throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "backup_" + timestamp + ".sql";
        Path backupPath = this.backupDir.resolve(fileName);

        logger.info("Iniciando respaldo de la base de datos en: {}", backupPath.toAbsolutePath());

        String command = String.format("mysqldump -u%s -p%s %s -r %s",
                dbUsername, dbPassword, getDbName(), backupPath.toString());

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            logger.info("Respaldo de la base de datos completado con éxito.");
            return new UrlResource(backupPath.toUri());
        } else {
            logger.error("Error al crear el respaldo de la base de datos. Código de salida: {}", exitCode);
            throw new RuntimeException("Error durante el proceso de mysqldump.");
        }
    }

    public void restore(MultipartFile file) throws Exception {
        Path restoreFile = this.backupDir.resolve("restore.sql");
        logger.info("Iniciando restauración desde el archivo: {}", file.getOriginalFilename());
        file.transferTo(restoreFile);

        String command = String.format("mysql -u%s -p%s %s < %s",
                dbUsername, dbPassword, getDbName(), restoreFile.toString());

        Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
        int exitCode = process.waitFor();

        Files.delete(restoreFile);

        if (exitCode == 0) {
            logger.info("Restauración de la base de datos completada con éxito.");
        } else {
            logger.error("Error al restaurar la base de datos. Código de salida: {}", exitCode);
            throw new RuntimeException("Error durante el proceso de restauración de mysql.");
        }
    }
}
