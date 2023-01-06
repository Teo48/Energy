package com.energystockaudit.stockaudit.connector;

import com.energystockaudit.stockaudit.domain.FilterParameters;
import com.energystockaudit.stockaudit.domain.Stock;
import org.opensearch.client.opensearch._types.Result;

import java.util.List;
import java.util.Map;

public interface ElasticsearchConnector {
  Result saveStockQuotation(Stock stock);

  List<Stock> search(FilterParameters filterParameters);

  Map<String, Long> aggregate(FilterParameters filterParameters);
}
