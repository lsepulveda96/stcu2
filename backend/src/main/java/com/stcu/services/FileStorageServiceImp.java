package com.stcu.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;

import javax.management.RuntimeErrorException;

@Service
public class FileStorageServiceImp implements FileStorageService {

    @Value("${app.file.upload-dir}")
    private String path;

    private Path fileStoreLocation;

    @Override
    public void init() {
        try {
            System.err.println("+++++++++  File storge value: " + path);
            this.fileStoreLocation = Paths.get(path);
            //Files.createDirectories(this.fileStoreLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not initialize folder " + path + "  to upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            String name_file = currentTimeMillis + "-" + file.getOriginalFilename();
            Path targetLocation = this.fileStoreLocation.resolve(name_file);
            Files.copy(file.getInputStream(), targetLocation);
            return name_file;
        } catch (Exception ex) {
            if (ex instanceof FileAlreadyExistsException)
                throw new RuntimeException("A file of that name already exists.");
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            // Path file = root.resolve(filename);
            System.err.println("+++++++++ Archivo a descargar : " + filename);
            Path file = this.fileStoreLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            // Path file = root.resolve(filename);
            Path file = this.fileStoreLocation.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        // FileSystemUtils.deleteRecursively(root.toFile());
        FileSystemUtils.deleteRecursively(this.fileStoreLocation.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.fileStoreLocation, 1)
                    .filter(path -> !path.equals(this.fileStoreLocation)).map(this.fileStoreLocation::relativize);
        } catch (IOException ex) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}