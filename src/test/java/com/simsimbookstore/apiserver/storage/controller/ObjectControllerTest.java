package com.simsimbookstore.apiserver.storage.controller;


import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;

import com.simsimbookstore.apiserver.storage.service.ObjectServiceImpl;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.anyList;

import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)  // Mockito 확장 사용

class ObjectControllerStandaloneTest {


    private MockMvc mockMvc;


    @Mock

    private ObjectServiceImpl objectService;  // @Mock 사용


    ObjectController objectController;


    @BeforeEach
    void setUp() {

        // Mockito 확장을 통해 Mock 초기화 완료

        objectController = new ObjectController(objectService);  // 컨트롤러에 의존성 주입

        mockMvc = MockMvcBuilders.standaloneSetup(objectController).build();  // standaloneSetup으로 MockMvc 설정

    }


    @Test

    @DisplayName("파일 업로드 성공 테스트")
    void uploadFilesSuccess() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile(

                "file", "test1.png", MediaType.IMAGE_PNG_VALUE,

                "dummy content 1".getBytes());

        MockMultipartFile file2 = new MockMultipartFile(

                "file", "test2.jpg", MediaType.IMAGE_JPEG_VALUE,

                "dummy content 2".getBytes());


        when(objectService.uploadObjects(anyList()))

                .thenReturn(List.of("path/to/test1.png", "path/to/test2.jpg"));


        mockMvc.perform(multipart("/api/shop/objects/upload-file")

                        .file(file1)

                        .file(file2)

                        .contentType(MediaType.MULTIPART_FORM_DATA)

                        .accept(MediaType.APPLICATION_JSON))  // Accept 헤더 추가

                .andExpect(status().isOk())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0]").value("path/to/test1.png"))

                .andExpect(jsonPath("$[1]").value("path/to/test2.jpg"));

    }


    @Test

    @DisplayName("파일 업로드 실패 시 오류 메시지 반환 테스트")
    void uploadFilesFailure() throws Exception {

        mockMvc.perform(multipart("/api/shop/objects/upload-file")

                        .contentType(MediaType.MULTIPART_FORM_DATA)

                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());


    }


    @Test

    @DisplayName("URL을 통한 파일 업로드 성공 테스트")
    void uploadFileByUrlSuccess() throws Exception {

        String testUrl = "https://example.com/test.png";

        String serviceResponse = "File uploaded successfully: path/to/test.png";


        when(objectService.uploadObjects(anyString())).thenReturn(serviceResponse);


        mockMvc.perform(post("/api/shop/objects/upload-url")

                        .param("url", testUrl)

                        .accept(MediaType.APPLICATION_JSON))  // Accept 헤더 추가

                .andExpect(status().isOk())

                .andExpect(content().string("File uploaded successfully: " + serviceResponse));

    }


    @Test

    @DisplayName("빈 URL 제공 시 BadRequest 반환 테스트")
    void uploadFileByUrlEmpty() throws Exception {

        mockMvc.perform(post("/api/shop/objects/upload-url")

                        .param("url", "")

                        .accept(MediaType.APPLICATION_JSON))  // Accept 헤더 추가

                .andExpect(status().isBadRequest())

                .andExpect(content().string("No URL provided for upload."));

    }


    @Test

    @DisplayName("URL 업로드 실패 시 오류 메시지 반환 테스트")
    void uploadFileByUrlFailure() throws Exception {

        String testUrl = "https://example.com/test.png";

        when(objectService.uploadObjects(anyString()))

                .thenThrow(new ObjectStorageException("Upload failed."));


        mockMvc.perform(post("/api/shop/objects/upload-url")

                        .param("url", testUrl)

                        .accept(MediaType.APPLICATION_JSON))  // Accept 헤더 추가

                .andExpect(status().isInternalServerError())

                .andExpect(content().string("File upload failed: Upload failed."));

    }


    @Test

    @DisplayName("서비스 예외 발생 시 500 오류 및 메시지 반환 테스트")
    void uploadFilesServiceException() throws Exception {

        // 가짜 파일 생성

        MockMultipartFile file = new MockMultipartFile(

                "file", "test.png", MediaType.IMAGE_PNG_VALUE,

                "dummy content".getBytes());


        // 서비스에서 예외 발생하도록 설정

        when(objectService.uploadObjects(anyList()))

                .thenThrow(new ObjectStorageException("Upload failed."));


        mockMvc.perform(multipart("/api/shop/objects/upload-file")

                        .file(file)

                        .contentType(MediaType.MULTIPART_FORM_DATA)

                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError())

                // 반환된 메시지 형식에 맞춰 검증

                .andExpect(jsonPath("$[0]").value("File upload failed: Upload failed."));

    }


}