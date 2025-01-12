package com.simsimbookstore.apiserver.storage.controller;

import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import com.simsimbookstore.apiserver.storage.service.ObjectServiceImpl;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/shop/objects")
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
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadObjects(@RequestPart("file") List<MultipartFile> files) {
        log.info("uploadObjects in");
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files provided for upload.");
        }

        try {
            List<String> uploadedFileNames = objectService.uploadObjects(files);
            return ResponseEntity.ok(uploadedFileNames); // string -> list<string>
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
