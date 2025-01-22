package com.simsimbookstore.apiserver.common;

import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import com.simsimbookstore.apiserver.storage.service.LocalFileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FileController.class)
@Import(FileControllerTest.TestConfig.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalFileStorageService fileStorageService; // TestConfig에서 주입받은 모킹 객체

    @TestConfiguration
    static class TestConfig {

        @Bean
        public LocalFileStorageService fileStorageService() {
            return Mockito.mock(LocalFileStorageService.class);
        }
    }

    @Test
    @DisplayName("파일 업로드 성공 테스트")
    void testUploadFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test file content".getBytes()
        );

        List<String> mockFilePaths = List.of("/mock/path/test.jpg");
        when(fileStorageService.saveFiles(anyList())).thenReturn(mockFilePaths);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("/mock/path/test.jpg"));

        verify(fileStorageService, times(1)).saveFiles(anyList());
    }

    @Test
    @DisplayName("파일 업로드 실패 시 내부 서버 오류 테스트")
    void testUploadFileFromUrl_InternalServerError() throws Exception {
        when(fileStorageService.saveFileFromUrl(anyString()))
                .thenThrow(new ObjectStorageException("Failed to save file"));

        mockMvc.perform(post("/api/files/upload-from-url")
                        .param("url", "https://example.com/test.jpg")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("File upload failed: Failed to save file"));

        verify(fileStorageService, times(1)).saveFileFromUrl(anyString());
    }
}
