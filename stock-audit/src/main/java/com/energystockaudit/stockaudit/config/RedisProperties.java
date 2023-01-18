package com.energystockaudit.stockaudit.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("com.energystockaudit.jedis")
@Data
@NoArgsConstructor
public class RedisProperties {
  private String hostname;
  private String scheme;
  private int port;
}
