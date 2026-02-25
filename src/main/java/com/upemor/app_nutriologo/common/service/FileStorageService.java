package com.upemor.app_nutriologo.common.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file) throws IOException;
    void deleteFile(String fileName);
    Resource loadFileAsResource(String fileName);
}
