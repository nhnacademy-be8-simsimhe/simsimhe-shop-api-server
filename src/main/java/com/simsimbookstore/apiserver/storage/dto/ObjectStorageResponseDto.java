package com.simsimbookstore.apiserver.storage.dto;

import lombok.Data;

@Data
public class ObjectStorageResponseDto {
    private String tokenId;
    private String storageUrl;
}