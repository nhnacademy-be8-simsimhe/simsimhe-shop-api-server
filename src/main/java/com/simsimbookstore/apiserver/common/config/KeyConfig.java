package com.simsimbookstore.apiserver.common.config;

import com.simsimbookstore.apiserver.common.dto.KeyResponseDto;
import com.simsimbookstore.apiserver.common.exception.KeyMangerException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;

import java.util.List;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "secure-key-manager")
public class KeyConfig {

    private final String password;
    private final String url;
    private final String path;
    private final String appKey;


    public String keyStore(String keyId) {
        try {
            // 1. 키스토어 초기화
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            try (InputStream result = new ClassPathResource("simsim.p12").getInputStream()) {
                clientStore.load(result, password.toCharArray());
            }

            SSLContext sslContext = SSLContextBuilder.create()
                    .setProtocol("TLS") // TLS 프로토콜 설정
                    .loadKeyMaterial(clientStore, password.toCharArray()) // 클라이언트 인증서 로드
                    .loadTrustMaterial(new TrustSelfSignedStrategy())    // 신뢰 인증서 설정
                    .build();

            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(new org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory(sslContext))
                    .build();

            // 4. HttpClient 생성
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            // 5. RestTemplate 초기화
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            RestTemplate restTemplate = new RestTemplate(requestFactory);

            // 6. HTTP 요청 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .path(path)
                    .encode()
                    .build()
                    .expand(appKey, keyId)
                    .toUri();

            // 7. HTTP 요청 전송 및 응답 처리
            return Objects.requireNonNull(restTemplate.exchange(uri,
                                    HttpMethod.GET,
                                    new HttpEntity<>(headers),
                                    KeyResponseDto.class)
                            .getBody())
                    .getBody()
                    .getSecret();
        } catch (Exception e) {
            throw new KeyMangerException("Error while accessing keystore: " + e.getMessage(), e);
        }
    }
}
