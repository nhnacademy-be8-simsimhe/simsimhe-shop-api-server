package com.simsimbookstore.apiserver.storage.controller;

import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import com.simsimbookstore.apiserver.storage.service.ObjectServiceImpl;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/objects")
public class ObjectController {

    private final ObjectServiceImpl objectService;

    public ObjectController(ObjectServiceImpl objectService) {
        this.objectService = objectService;
    }


    /*
        여러 개의 이미지를 업로드
        - @RequestParam("file")로 여러 파일을 받음
        - 업로드 성공 시 업로드된 파일 이름 리스트 반환
     */
    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadObjects(@RequestParam("file") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files provided for upload.");
        }

        try {
            List<String> uploadedFileNames = objectService.uploadObjects(files);
            return ResponseEntity.ok("Files uploaded successfully: " + String.join(", ", uploadedFileNames));
        } catch (ObjectStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    /*
        - 이미지 링크로 도서의 상세 이미지 업로드
        - @RequestParam("url")로 이미지링크를 받음
        - 업로드 성공 시 업로드된 파일 이름 반환
     */

    @PostMapping("/upload-url")
    public ResponseEntity<String> uploadObjectByUrl(@RequestParam("url") String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("No URL provided for upload.");
        }

        try {
            String uploadedFileName = objectService.uploadObjects(imageUrl);
            return ResponseEntity.ok("File uploaded successfully: " + uploadedFileName);
        } catch (ObjectStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

}
