package com.simsimbookstore.apiserver.storage.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class TokenRequestDtoTest {

    @Test
    void testTokenRequestDtoBuilderAndGetters() {
        // given
        String tenantId = "tenant123";
        String username = "user@example.com";
        String password = "securePassword";

        // when
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .tenantId(tenantId)
                .username(username)
                .password(password)
                .build();

        // then
        assertNotNull(tokenRequestDto);
        assertNotNull(tokenRequestDto.getAuth(), "Auth 객체는 null이 아니어야 합니다.");
        assertEquals(tenantId, tokenRequestDto.getAuth().getTenantId(), "tenantId가 일치해야 합니다.");

        TokenRequestDto.Auth auth = tokenRequestDto.getAuth();
        assertNotNull(auth.getPasswordCredentials(), "PasswordCredentials 객체는 null이 아니어야 합니다.");

        TokenRequestDto.PasswordCredentials credentials = auth.getPasswordCredentials();
        assertEquals(username, credentials.getUsername(), "username이 일치해야 합니다.");
        assertEquals(password, credentials.getPassword(), "password가 일치해야 합니다.");
    }

    @Test
    void testAuthAndPasswordCredentialsBuildersAndGetters() {
        // given
        String tenantId = "tenantXYZ";
        String username = "john.doe@example.com";
        String password = "johnsPassword";

        // when
        TokenRequestDto.PasswordCredentials passwordCredentials =
                TokenRequestDto.PasswordCredentials.builder()
                        .username(username)
                        .password(password)
                        .build();

        TokenRequestDto.Auth auth = TokenRequestDto.Auth.builder()
                .tenantId(tenantId)
                .passwordCredentials(passwordCredentials)
                .build();

        // then
        assertNotNull(auth);
        assertEquals(tenantId, auth.getTenantId(), "Auth의 tenantId가 일치해야 합니다.");
        assertNotNull(auth.getPasswordCredentials(), "Auth의 PasswordCredentials가 null이 아니어야 합니다.");
        assertEquals(username, auth.getPasswordCredentials().getUsername(), "Username이 일치해야 합니다.");
        assertEquals(password, auth.getPasswordCredentials().getPassword(), "Password가 일치해야 합니다.");
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Jackson을 활용한 JSON 역직렬화 테스트
        String json = "{"
                + "\"tenantId\": \"tenantABC\","
                + "\"username\": \"alice@example.com\","
                + "\"password\": \"alicePassword\""
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        TokenRequestDto tokenRequestDto = mapper.readValue(json, TokenRequestDto.class);

        assertNotNull(tokenRequestDto);
        assertNotNull(tokenRequestDto.getAuth());
        assertEquals("tenantABC", tokenRequestDto.getAuth().getTenantId());

        TokenRequestDto.PasswordCredentials credentials = tokenRequestDto.getAuth().getPasswordCredentials();
        assertNotNull(credentials);
        assertEquals("alice@example.com", credentials.getUsername());
        assertEquals("alicePassword", credentials.getPassword());
    }

    @Test
    void testNoArgsConstructors() {
        // TokenRequestDto의 no-args 생성자 테스트
        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        assertNotNull(tokenRequestDto, "TokenRequestDto 인스턴스는 null이 아니어야 합니다.");
        // 기본적으로 필드들은 null이어야 함
        assertNull(tokenRequestDto.getAuth(), "Auth 필드는 초기 상태에서 null이어야 합니다.");

        // Auth의 no-args 생성자 테스트
        TokenRequestDto.Auth auth = new TokenRequestDto.Auth();
        assertNotNull(auth, "Auth 인스턴스는 null이 아니어야 합니다.");
        assertNull(auth.getTenantId(), "tenantId 필드는 초기 상태에서 null이어야 합니다.");
        assertNull(auth.getPasswordCredentials(), "passwordCredentials 필드는 초기 상태에서 null이어야 합니다.");

        // PasswordCredentials의 no-args 생성자 테스트
        TokenRequestDto.PasswordCredentials credentials = new TokenRequestDto.PasswordCredentials();
        assertNotNull(credentials, "PasswordCredentials 인스턴스는 null이 아니어야 합니다.");
        assertNull(credentials.getUsername(), "username 필드는 초기 상태에서 null이어야 합니다.");
        assertNull(credentials.getPassword(), "password 필드는 초기 상태에서 null이어야 합니다.");
    }
}