package com.energystockaudit.stockaudit.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("com.energystockaudit.java.api.client")
@Data
@NoArgsConstructor
public class JavaAPIClientProperties {
  private String hostname;
  private String scheme;
  private int port;
}
