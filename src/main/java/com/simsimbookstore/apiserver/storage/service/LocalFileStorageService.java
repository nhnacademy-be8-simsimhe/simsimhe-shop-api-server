package com.simsimbookstore.apiserver.storage.service;

import com.simsimbookstore.apiserver.common.config.LocalStorageConfig;
import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileStorageService {

    // 허용된 이미지 파일 확장자 목록
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

    private final LocalStorageConfig localStorageConfig;
    private final HttpClient httpClient;



    /*
        여러 개의 MultipartFile 객체를 로컬 디스크에 저장
        - 이미지 파일 확장자인지 확인
        - 각 파일에 대해 고유한 이름 생성 후 저장
        - 저장된 파일의 경로 목록 반환
     */
    @Transactional
    public List<String> saveFiles(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new ObjectStorageException("No files uploaded.");
        }

        List<String> savedFilePaths = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            validateImageFile(multipartFile);
            String fileName = createUniqueFileName(multipartFile.getOriginalFilename());
            String storageLocation = localStorageConfig.getLocation();
            File destinationDir = new File(storageLocation);
            File destinationFile = new File(storageLocation, fileName);
            if (!destinationDir.exists() && !destinationDir.mkdirs()) {
                throw new ObjectStorageException("Could not create storage directory: " + storageLocation);
            }

            try (InputStream inputStream = multipartFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                savedFilePaths.add(destinationFile.getAbsolutePath());
            } catch (IOException e) {
                throw new ObjectStorageException("Failed to save file: " + multipartFile.getOriginalFilename());
            }
            log.info(destinationFile.getAbsolutePath());
        }
        return savedFilePaths;
    }

    @Transactional
    public String saveFileFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new ObjectStorageException("No URL provided for upload.");
        }

        String storageLocation = localStorageConfig.getLocation();
        File destinationDir = new File(storageLocation);
        if (!destinationDir.exists() && !destinationDir.mkdirs()) {
            throw new ObjectStorageException("Could not create storage directory: " + storageLocation);
        }

        try {
            InputStream inputStream = createInputStreamFromUrl(fileUrl);
            String originalFileName = getFileNameFromUrl(fileUrl);
            String fileName = createUniqueFileName(originalFileName);
            File destinationFile = new File(destinationDir, fileName);

            Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info(destinationFile.getAbsolutePath());
            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            throw new ObjectStorageException("Failed to save file from URL: " + fileUrl);
        }
    }

    private InputStream createInputStreamFromUrl(String fileUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new IOException("Failed to fetch resource: HTTP " + response.statusCode());
            }

            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ObjectStorageException("Thread was interrupted while creating InputStream from URL: " + fileUrl);
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to create InputStream from URL: " + fileUrl);
        }
    }

    private String getFileNameFromUrl(String urlString) {
        try {
            URI uri = URI.create(urlString);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (Exception e) {
            throw new ObjectStorageException("Invalid URL: " + urlString);
        }
    }

    private String createUniqueFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFileName);
        return uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1) : "";
    }

    private void validateImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            throw new ObjectStorageException("File name is invalid");
        }

        String extension = getFileExtension(fileName);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ObjectStorageException("Unsupported file type: " + extension);
        }
    }

}