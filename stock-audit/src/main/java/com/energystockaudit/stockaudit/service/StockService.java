package com.energystockaudit.stockaudit.service;

import com.energystockaudit.stockaudit.connector.ElasticsearchConnectorImpl;
import com.energystockaudit.stockaudit.domain.FilterParameters;
import com.energystockaudit.stockaudit.domain.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.ws.rs.DefaultValue;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final ElasticsearchConnectorImpl elasticsearchConnector;

  public List<Stock> search(FilterParameters filterParameters) {
    if (null != filterParameters.getSymbol() && checkOtherSearchFieldsAreNull(filterParameters)) {
      log.info("Search redis cache for symbol: {}", filterParameters.getSymbol());
      checkOtherSearchFieldsAreNull(filterParameters);
      return redisTemplate.opsForList()
          .range(filterParameters.getSymbol(), filterParameters.getFrom(), filterParameters.getSize())
          .stream().map(i -> (Stock) i)
          .collect(Collectors.toList());
    }

    log.info("Search elasticsearch for data");
    return elasticsearchConnector.search(filterParameters);
  }

  public Map<String, Long> aggregate(FilterParameters filterParameters) {
    return elasticsearchConnector.aggregate(filterParameters);
  }

  private boolean checkOtherSearchFieldsAreNull(FilterParameters filterParameters) {
    var s = Arrays.stream(filterParameters.getClass().getDeclaredFields())
        .peek(i -> i.setAccessible(true))
        .filter(i -> checkFieldNotNull(i, filterParameters) &&
            i.getDeclaredAnnotation(NonNull.class) == null &&
            i.getDeclaredAnnotation(DefaultValue.class) == null &&
            checkFieldHasDefaultValue(i, filterParameters));
    return s.count() < 2;
  }

  private boolean checkFieldHasDefaultValue(Field f, FilterParameters filterParameters) {
    try {
      if (f.get(filterParameters) instanceof Integer) {
        return (int) f.get(filterParameters) != 0;
      }

      if (f.get(filterParameters) instanceof Double) {
        return (double) f.get(filterParameters) != 0.0;
      }

      return true;

    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  private boolean checkFieldNotNull(Field f, FilterParameters filterParameters) {
    try {
        return f.get(filterParameters) != null;
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
    }
  }
}
