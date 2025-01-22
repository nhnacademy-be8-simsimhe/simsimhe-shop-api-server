package com.simsimbookstore.apiserver.common;

import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import com.simsimbookstore.apiserver.storage.service.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final LocalFileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("file") List<MultipartFile> files) {
        List<String> filePaths = fileStorageService.saveFiles(files);
        return ResponseEntity.ok(filePaths);
    }

    @PostMapping("/upload-from-url")
    public ResponseEntity<String> uploadFileFromUrl(@RequestParam("url") String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("No URL provided for upload.");
        }

        try {
            String uploadedFileName = fileStorageService.saveFileFromUrl(imageUrl);
            return ResponseEntity.ok("File uploaded successfully: " + uploadedFileName);
        } catch (ObjectStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }
}
