package com.simsimbookstore.apiserver.storage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.simsimbookstore.apiserver.common.config.LocalStorageConfig;
import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LocalFileStorageServiceTest {
    @Mock
    private LocalStorageConfig localStorageConfig;

    @Mock
    private HttpClient httpClient;

    private LocalFileStorageService fileStorageService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // 임시 디렉토리를 로컬 저장 경로로 설정
        lenient().when(localStorageConfig.getLocation()).thenReturn(tempDir.toString());
        fileStorageService = new LocalFileStorageService(localStorageConfig, httpClient);
    }

    @Test
    @DisplayName("여러 파일 저장 성공 테스트")
    void saveFilesSuccess() throws IOException {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.getOriginalFilename()).thenReturn("test1.png");
        when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("content1".getBytes()));

        when(file2.getOriginalFilename()).thenReturn("test2.jpg");
        when(file2.getInputStream()).thenReturn(new ByteArrayInputStream("content2".getBytes()));

        List<String> savedPaths = fileStorageService.saveFiles(List.of(file1, file2));

        // 저장된 파일 경로 검증
        assertEquals(2, savedPaths.size());
        for (String path : savedPaths) {
            assertTrue(Files.exists(Path.of(path)));
        }
    }

    @Test
    @DisplayName("허용되지 않은 파일 확장자 업로드 시 예외 발생")
    void uploadInvalidExtension() {
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getOriginalFilename()).thenReturn("testFile.txt");

        List<MultipartFile> files = List.of(invalidFile);

        assertThrows(ObjectStorageException.class, () -> fileStorageService.saveFiles(files));
    }

    @Test
    @DisplayName("빈 파일 리스트를 업로드 시도 시 예외 발생")
    void uploadEmptyFiles() {
        List<MultipartFile> files = new ArrayList<>();

        ObjectStorageException exception = assertThrows(ObjectStorageException.class,
                () -> fileStorageService.saveFiles(files));

        assertEquals("No files uploaded.", exception.getMessage());
    }


    @Test
    @DisplayName("유효한 URL에서 파일 저장 성공 테스트")
    void saveFileFromUrlSuccess() throws Exception {
        String testUrl = "https://example.com/test.png";
        InputStream mockInputStream = new ByteArrayInputStream("file content".getBytes());

        // HttpResponse 모킹
        HttpResponse<InputStream> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(mockInputStream);

        // HttpClient.send 동작 모킹
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        String savedPath = fileStorageService.saveFileFromUrl(testUrl);

        // 저장된 경로가 실제 파일을 가리키는지 확인
        assertNotNull(savedPath);
        assertTrue(Files.exists(Path.of(savedPath)));
    }

    @Test
    @DisplayName("잘못된 URL로 파일 저장 시 예외 발생 테스트")
    void saveFileFromUrlInvalid() {
        String invalidUrl = "invalid-url";

        ObjectStorageException exception = assertThrows(
                ObjectStorageException.class,
                () -> fileStorageService.saveFileFromUrl(invalidUrl)
        );

        assertTrue(exception.getMessage().contains("Failed to create InputStream from URL"));
    }

}