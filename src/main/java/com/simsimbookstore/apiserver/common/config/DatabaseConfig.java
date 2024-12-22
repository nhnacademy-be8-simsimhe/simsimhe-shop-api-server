package com.simsimbookstore.apiserver.common.config;

import com.simsimbookstore.apiserver.common.dto.DatabaseProperty;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    private final DatabaseProperty databaseProperty;
    private final KeyConfig keyConfig;

    @Bean
    public String getDecryptedUrl() {
        return keyConfig.keyStore(databaseProperty.getUrl());
    }

    @Bean
    public String getDecryptedUsername() {
        return keyConfig.keyStore(databaseProperty.getUsername());
    }

    @Bean
    public String getDecryptedPassword() {
        return keyConfig.keyStore(databaseProperty.getPassword());
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(getDecryptedUrl());
        dataSource.setUsername(getDecryptedUsername());
        dataSource.setPassword(getDecryptedPassword());

        return dataSource;
    }
}

