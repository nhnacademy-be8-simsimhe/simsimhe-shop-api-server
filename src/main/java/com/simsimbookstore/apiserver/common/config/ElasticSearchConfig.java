package com.simsimbookstore.apiserver.common.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.simsimbookstore.apiserver.elastic.dto.ElasticProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticSearchConfig {

    private final ElasticProperty elasticProperty;
    private final KeyConfig keyConfig;

    @Bean
    public ElasticsearchTransport restClientTransport(){
        String[] data = keyConfig.keyStore(elasticProperty.getData()).split("\n");

        String url = data[0];
        String username = data[1];
        String password = data[2];

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        RestClientBuilder builder = RestClient.builder(new HttpHost(url, 9200))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                });

        ElasticsearchTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper());
        return transport;
    }


    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport restClientTransport) {
        return new ElasticsearchClient(restClientTransport);
    }

}