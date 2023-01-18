package com.energystockaudit.stockaudit.service;

import com.energystockaudit.stockaudit.connector.ElasticsearchConnectorImpl;
import com.energystockaudit.stockaudit.domain.Stock;
import com.energystockaudit.stockaudit.utils.StockDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockIngestService {
  @Autowired
  private ElasticsearchConnectorImpl elasticsearchConnector;
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private final Client client;
  private WebTarget webTarget;
  private Invocation.Builder invocationBuilder;
  @Value("${com.energystockaudit.stock.uri}")
  private String stockURI;
  private static List<String> stockList;
  private static Map<String, String> stockCompanyMap = new HashMap<>();
  private final ObjectMapper objectMapper;
  @Value("${com.energystockaudit.stock.start.timeframe}")
  private int startTimeframe;
  @Value("${com.energystockaudit.alapca.api.key.id}")
  private String apcaApiKey;
  @Value("${com.energystockaudit.alpaca.api.secret.key}")
  private String apcaSecretKey;

  static {
    stockList = List.of("XOM", "CVX", "SHEL", "TTE", "COP", "SLB", "FANG", "PDCE", "OVV", "APA");

    stockCompanyMap.put("XOM", "Exxon Mobil Corporation");
    stockCompanyMap.put("CVX", "Chevron Corporation");
    stockCompanyMap.put("SHEL", "Shell plc");
    stockCompanyMap.put("TTE", "TotalEnergies SE");
    stockCompanyMap.put("COP", "ConocoPhillips");
    stockCompanyMap.put("SLB", "Schlumberger Limited");
    stockCompanyMap.put("FANG", "Diamondback Energy, Inc.");
    stockCompanyMap.put("PDCE", "PDC Energy Inc.");
    stockCompanyMap.put("OVV", "Ovintiv Inc. ");
    stockCompanyMap.put("APA", "APA Corp.");
  }

  public StockIngestService() {
    client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Stock.class, new StockDeserializer());
    objectMapper.registerModule(module);
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void ingestStockQuotations() {
    stockList.forEach(stock -> {
      stockURI = stockURI.replaceAll("symbol", stock);
      callStockApi(stock, null);
    });
  }

  private void callStockApi(String stock, String nextPageToken) {
    webTarget = client.target(stockURI)
        .queryParam("timeframe", "1Day")
        .queryParam("start", ZonedDateTime.now().minusYears(startTimeframe).toInstant().truncatedTo(ChronoUnit.DAYS).toString())
        .queryParam("end", Instant.now().truncatedTo(ChronoUnit.DAYS).toString());

    if (nextPageToken != null) {
      webTarget = webTarget.queryParam("page_token", nextPageToken);
    }

    invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
        .header("Apca-Api-Key-Id", apcaApiKey)
        .header("Apca-Api-Secret-Key", apcaSecretKey);

    var jsonNode = invocationBuilder.get(JsonNode.class);
    try {
      nextPageToken = objectMapper.treeToValue(jsonNode.get("next_page_token"), String.class);
      var x = objectMapper.treeToValue(jsonNode.get("bars"), JsonNode.class);
      List<Stock> stocks = Arrays.stream(objectMapper.treeToValue(x, Stock[].class))
          .peek(i -> i.setSymbol(stock))
          .peek(i -> i.setCompany(stockCompanyMap.get(stock)))
          .collect(Collectors.toList());
      stocks.forEach(i -> elasticsearchConnector.saveStockQuotation(i));
      stocks.forEach(i -> redisTemplate.opsForList().rightPush(i.getSymbol(), i));

      if (nextPageToken != null) {
        callStockApi(stock, nextPageToken);
      }

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
