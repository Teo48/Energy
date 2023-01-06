package com.energystockaudit.stockaudit.service;

import com.energystockaudit.stockaudit.connector.ElasticsearchConnectorImpl;
import com.energystockaudit.stockaudit.domain.FilterParameters;
import com.energystockaudit.stockaudit.domain.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch._types.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
  private final ElasticsearchConnectorImpl elasticsearchConnector;

  public Result saveMappedEvent(Stock stock) {
    return elasticsearchConnector.saveStockQuotation(stock);
  }

  public List<Stock> search(FilterParameters filterParameters) {
    return elasticsearchConnector.search(filterParameters);
  }

  public Map<String, Long> aggregate(FilterParameters filterParameters) {
    return elasticsearchConnector.aggregate(filterParameters);
  }
}
