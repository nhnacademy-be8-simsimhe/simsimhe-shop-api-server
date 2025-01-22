package com.simsimbookstore.apiserver.common.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {
        "file.storage.location=/test/storage"
})
class LocalStorageConfigTest {

    @Autowired
    private LocalStorageConfig localStorageConfig;

    @Test
    void testLocalStorageConfigBinding() {
        // Given the property file configuration, verify the location field
        assertNotNull(localStorageConfig);
        assertEquals("/test/storage", localStorageConfig.getLocation());
    }
}