package com.energystockaudit.stockaudit.domain;

import com.energystockaudit.stockaudit.annotations.MatchQueryMethod;
import com.energystockaudit.stockaudit.annotations.RangeQueryMethod;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterParameters {
  @QueryParam("symbol")
  @MatchQueryMethod("getMatchQuery")
  private String symbol;

  @QueryParam("company")
  @MatchQueryMethod("getMatchQuery")
  private String company;

  @QueryParam("timestamp")
  private Date timestamp;

  @QueryParam("openPrice")
  private double openPrice;

  @QueryParam("highPrice")
  private double highPrice;

  @QueryParam("lowPrice")
  private double lowPrice;

  @QueryParam("closePrice")
  private double closePrice;

  @QueryParam("volume")
  private int volume;

  @QueryParam("numberOfTrades")
  private int numberOfTrades;

  @QueryParam("volumeWeightedAveragePrice")
  private double volumeWeightedAveragePrice;

  @DefaultValue("0")
  @QueryParam("from")
  private Integer from;

  @DefaultValue("100")
  @QueryParam("size")
  private Integer size;

  @QueryParam("word")
  private String word;

  @QueryParam("field")
  private String field;

  @QueryParam("wildcard")
  private String wildcard;

  @QueryParam("startDate")
  private String startDate;

  @QueryParam("endDate")
  private String endDate;

  @QueryParam("startOpenPrice")
  @RangeQueryMethod("getGteRangeQuery")
  @DefaultValue("0.0")
  private double startOpenPrice;

  @QueryParam("endOpenPrice")
  @RangeQueryMethod("getLteRangeQuery")
  @DefaultValue("100000000.0")
  private double endOpenPrice;

  @QueryParam("startHighPrice")
  @RangeQueryMethod("getGteRangeQuery")
  @DefaultValue("0.0")
  private double startHighPrice;

  @QueryParam("endHighPrice")
  @RangeQueryMethod("getLteRangeQuery")
  @DefaultValue("100000000.0")
  private double endHighPrice;

  @QueryParam("startLowPrice")
  @RangeQueryMethod("getGteRangeQuery")
  @DefaultValue("0.0")
  private double startLowPrice;

  @QueryParam("endLowPrice")
  @RangeQueryMethod("getLteRangeQuery")
  @DefaultValue("100000000.0")
  private double endLowPrice;

  @QueryParam("startClosePrice")
  @RangeQueryMethod("getGteRangeQuery")
  @DefaultValue("0.0")
  private double startClosePrice;

  @QueryParam("endClosePrice")
  @RangeQueryMethod("getLteRangeQuery")
  @DefaultValue("100000000.0")
  private double endClosePrice;

  @QueryParam("sortingCriteria")
  @NonNull
  private List<String> sortingCriteria;
}
