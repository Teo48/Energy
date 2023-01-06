package com.energystockaudit.stockaudit.utils;

import com.energystockaudit.stockaudit.domain.Stock;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockDeserializer extends StdDeserializer<Stock> {
  public StockDeserializer() {
    this(null);
  }

  public StockDeserializer(Class<?> valueClass) {
    super(valueClass);
  }

  @Override
  public Stock deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    Date timestamp;

    try {
       timestamp = format.parse(node.get("t").asText());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    double openPrice = node.get("o").asDouble();
    double highPrice = node.get("h").asDouble();
    double lowPrice = node.get("l").asDouble();
    double closePrice = node.get("c").asDouble();
    int volume = node.get("v").asInt();
    int numberOfTrades = node.get("n").asInt();
    double volumeWeightedAveragePrice = node.get("vw").asDouble();

    return new Stock(null, null, timestamp, openPrice, highPrice, lowPrice, closePrice, volume, numberOfTrades, volumeWeightedAveragePrice);
  }
}
