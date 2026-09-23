package com.upemor.app_nutriologo.common.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new InvalidOperationException("No se pudo crear el directorio donde se almacenarán los archivos subidos.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // Normalizar el nombre del archivo
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Generar un nombre único para evitar colisiones
        String fileExtension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFileName.substring(i);
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Verificar si el nombre del archivo contiene caracteres inválidos
            if (fileName.contains("..")) {
                throw new BadRequestException("Lo sentimos! El nombre del archivo contiene una ruta inválida " + fileName);
            }

            // Copiar archivo a la ubicación de destino (reemplazando archivo existente con el mismo nombre)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new IOException("No se pudo almacenar el archivo " + fileName + ". Por favor intente de nuevo!", ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("No se pudo eliminar el archivo: " + fileName, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Archivo no encontrado " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Archivo no encontrado " + fileName, ex);
        }
    }
}
