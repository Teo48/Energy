package com.energystockaudit.stockaudit.exceptions;

public class ElasticsearchConnectorException extends RuntimeException {
  public ElasticsearchConnectorException(Throwable e) {
    super(e);
  }
}
