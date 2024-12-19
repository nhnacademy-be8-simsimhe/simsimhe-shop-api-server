package com.simsimbookstore.apiserver.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectService {
    String upload(MultipartFile file);
}
