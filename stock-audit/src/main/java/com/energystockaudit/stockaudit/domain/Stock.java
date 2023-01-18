package com.energystockaudit.stockaudit.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "${com.energystockaudit.elasticsearch.index}")
public class Stock implements Serializable {
  @Field(type = FieldType.Keyword)
  private String symbol;
  @Field(type = FieldType.Text)
  private String company;
  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date timestamp;
  @Field(type = FieldType.Double)
  private double openPrice;
  @Field(type = FieldType.Double)
  private double highPrice;
  @Field(type = FieldType.Double)
  private double lowPrice;
  @Field(type = FieldType.Double)
  private double closePrice;
  @Field(type = FieldType.Integer)
  private int volume;
  @Field(type = FieldType.Integer)
  private int numberOfTrades;
  @Field(type = FieldType.Double)
  private double volumeWeightedAveragePrice;

  @Override
  public String toString() {
    return "Stock{" +
        "symbol='" + symbol + '\'' +
        ", company='" + company + '\'' +
        ", timestamp=" + timestamp +
        ", openPrice=" + openPrice +
        ", highPrice=" + highPrice +
        ", lowPrice=" + lowPrice +
        ", closePrice=" + closePrice +
        ", volume=" + volume +
        ", numberOfTrades=" + numberOfTrades +
        ", volumeWeightedAveragePrice=" + volumeWeightedAveragePrice +
        '}';
  }
}
