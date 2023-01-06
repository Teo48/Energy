package com.energystockaudit.stockaudit.config;

import lombok.AllArgsConstructor;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(JavaAPIClientProperties.class)
public class JavaAPIClientConfig {
  private final JavaAPIClientProperties javaAPIClientProperties;

  @Bean
  public OpenSearchClient openSearchClient() {
    RestClient restClient = RestClient.builder(new HttpHost(javaAPIClientProperties.getHostname(),
            javaAPIClientProperties.getPort(),
            javaAPIClientProperties.getScheme()))
        .build();
    OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

    return new OpenSearchClient(transport);
  }
}
