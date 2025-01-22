package com.simsimbookstore.apiserver.common.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogAndCrashAppenderTest {

    private LogAndCrashAppender appender;

    @Mock
    private RestTemplate mockRestTemplate;

    @BeforeEach
    void setUp() {

        appender = new LogAndCrashAppender();
        appender.setAppKey("testAppKey");
        appender.setPlatform("testPlatform");
        appender.setRestTemplate(mockRestTemplate); // Inject mock RestTemplate
    }

    @Test
    void testStartWithoutAppKeyOrPlatform() {
        appender.setAppKey(null);
        appender.setPlatform(null);

        appender.start();

        assertFalse(appender.isStarted());
    }
}