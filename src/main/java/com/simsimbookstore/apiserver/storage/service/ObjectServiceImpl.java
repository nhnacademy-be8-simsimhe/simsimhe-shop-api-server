package com.simsimbookstore.apiserver.storage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.storage.config.ObjectStorageConfig;
import com.simsimbookstore.apiserver.storage.exception.ObjectStorageException;
import com.simsimbookstore.apiserver.storage.exception.ObjectStorageTokenException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/*
    파일을 오브젝트 스토리지에 업로드 하는 서비스를 구현
    ObjectAuthService 로 부터 오브젝트 스토리지의 인증 토큰을 받아 요청에 사용한다.
    RestTemplate를 사용하여 http 요청을 처리
 */


@Service
public class ObjectServiceImpl {

    // 허용된 이미지 파일 확장자 목록
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

    private final ObjectAuthService authService;
    private final String storageUrl;
    private final String containerName;
    private final RestTemplate restTemplate;

    /*
        ObjectStorageConfig : 오브젝트 스토리지에 필요한 정보들의 설정정보
        AuthService : 오브젝트 스토리지 인증 토큰을 제공하는 서비스
        RestTemplate : HTTP 요청을 처리
     */
    public ObjectServiceImpl(ObjectStorageConfig objectStorageConfig, ObjectAuthService authService,
                             RestTemplate restTemplate) {
        this.authService = authService;
        this.storageUrl = normalizeUrl(objectStorageConfig.getStorageUrl());
        this.containerName = objectStorageConfig.getContainerName();
        this.restTemplate = restTemplate;
    }

    /*
        URL이 '/'로 끝나는 경우 제거하여 정규화
     */
    public String normalizeUrl(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /*
        파일 이름을 기준으로 오브젝트 스토리지에서 접근 가능한 URL 생성
     */
    public String getUrl(String fileName) {
        return String.format("%s/%s/%s", storageUrl, containerName, fileName);
    }

    /*
        여러 개의 MultipartFile 객체를 오브젝트 스토리지에 업로드
        - 이미지 파일 확장자인지 확인
        - 각 파일에 대해 고유한 이름 생성 후 업로드
        - 업로드된 파일의 이름 목록 반환
     */
    public List<String> uploadObjects(List<MultipartFile> multipartFiles) {

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new ObjectStorageException("No files upload.");
        }

        String requestToken = authService.requestToken();
        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            try {
                validateImageFile(multipartFile); // 이미지 파일인지 검증
                String fileName = createUniqueFileName(multipartFile.getOriginalFilename());
                String tokenId = extractTokenId(requestToken);
                String url = getUrl(fileName);

                uploadFileToStorage(url, multipartFile.getInputStream(), tokenId); // 파일 업로드
                uploadedFileNames.add(fileName);
            } catch (HttpClientErrorException | IOException e) {
                throw new ObjectStorageException("Failed to upload file: " + multipartFile.getOriginalFilename());
            }
        }
        return uploadedFileNames;
    }

    public String uploadObjects(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new ObjectStorageException("No URLs provided for upload.");
        }

        String requestToken = authService.requestToken();
        List<String> uploadedFileNames = new ArrayList<>();
        String name = "";

        try (InputStream inputStream = createInputStreamFromUrl(fileUrl)) {
            // URL에서 파일 이름 추출 또는 고유 이름 생성
            String fileName = createUniqueFileName(getFileNameFromUrl(fileUrl));
            String tokenId = extractTokenId(requestToken);
            String url = getUrl(fileName);
            name = fileName;
            // 파일 업로드
            uploadFileToStorage(url, inputStream, tokenId);
            uploadedFileNames.add(fileName);
        } catch (IOException e) {
            throw new ObjectStorageException("Failed to upload file from URL: " + fileUrl);
        }

        return name;
    }
    /*
        - 주어진 MultipartFile의 확장자가 허용된 이미지 확장자인지 검증
     */
    private void validateImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName.isEmpty()) {
            throw new ObjectStorageException("File name is invalid");
        }

        String extension = getFileExtension(fileName);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ObjectStorageException("Unsupported file type: " + extension);
        }
    }

    /*
        - 파일 이름에서 확장자를 추출
        - '.' 이후의 문자열을 확장자로 반환
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1) : "";
    }

    /*
        - 파일의 고유한 이름을 생성
        - UUID를 사용
     */
    private String createUniqueFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFileName);

        return uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    /*
        - 인증서비스에서 반환된 JSON에서 토큰 ID 추출
        - "access.token.id" 경로에서 ID를 가져옴
     */
    private String extractTokenId(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            String tokenId = rootNode.path("access").path("token").path("id").asText();
            if (tokenId == null || tokenId.isEmpty()) {
                throw new ObjectStorageTokenException("Token ID is null or empty");
            }

            return tokenId;
        } catch (IOException e) {
            throw new ObjectStorageTokenException("Failed to extract token ID");
        }
    }

    // URL에서 파일 이름 추출
    private String getFileNameFromUrl(String urlString) {
        try {
            URI uri = URI.create(urlString);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (Exception e) {
            throw new ObjectStorageException("Invalid URL: " + urlString);
        }
    }

    // URL로부터 InputStream 생성
    private InputStream createInputStreamFromUrl(String fileUrl) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            // HTTP GET 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .GET()
                    .build();

            // HTTP 응답에서 InputStream 가져오기
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new IOException("Failed to fetch resource: HTTP " + response.statusCode());
            }

            return response.body();
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to create InputStream from URL: " + fileUrl);
        }
    }

    /*
        주어진 URL에 파일을 업로드
        - HTTP PUT 요청 사용
        - 인증 토큰 헤더 추가
     */
    private void uploadFileToStorage(String url, InputStream inputStream, String tokenId) throws IOException {
        RequestCallback requestCallback = request -> {
            request.getHeaders().add("X-Auth-Token", tokenId);
            IOUtils.copy(inputStream, request.getBody());
        };

        restTemplate.execute(url, HttpMethod.PUT, requestCallback, null);
    }
}