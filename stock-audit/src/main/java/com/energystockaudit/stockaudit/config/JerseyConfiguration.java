package com.energystockaudit.stockaudit.config;

import com.energystockaudit.stockaudit.resource.StockResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class JerseyConfiguration extends ResourceConfig {
  @PostConstruct
  public void init() {
    register(StockResource.class);
  }
}
