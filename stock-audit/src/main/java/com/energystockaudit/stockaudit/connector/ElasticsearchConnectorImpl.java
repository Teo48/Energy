package com.energystockaudit.stockaudit.connector;

import com.energystockaudit.stockaudit.annotations.MatchQueryMethod;
import com.energystockaudit.stockaudit.annotations.RangeQueryMethod;
import com.energystockaudit.stockaudit.domain.FilterParameters;
import com.energystockaudit.stockaudit.domain.Stock;
import com.energystockaudit.stockaudit.exceptions.ElasticsearchConnectorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.*;
import org.opensearch.client.opensearch._types.aggregations.*;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class ElasticsearchConnectorImpl implements ElasticsearchConnector {
  @Autowired
  private OpenSearchClient elasticsearchClient;

  @Value("${com.energystockaudit.elasticsearch.index}")
  private String energyStockIndex;
  private IndexResponse indexResponse;
  private SearchResponse<Stock> response;

  @Override
  public Result saveStockQuotation(Stock stock) {
    log.info("[{}] - Saving stock details for symbol {}.", stock.getTimestamp(), stock.getSymbol());

    try {
      indexResponse = elasticsearchClient.index(doc -> doc
          .index(energyStockIndex)
          .document(stock)
      );
    } catch (IOException | OpenSearchException e) {
      log.error("Could not connect to database. ", e);
      throw new ElasticsearchConnectorException(e);
    }

    log.info("[{}] - Saved stock details for symbol {}.", stock.getTimestamp(), stock.getSymbol());
    return indexResponse.result();
  }

  public List<Stock> runSearch(SearchRequest query)  {
    executeSearch(query);

    List<Stock> eventList = getEventListFromResponse(response);
    log.info("Extracted stocks from index={} eventnumber={}", energyStockIndex, eventList.size());

    return eventList;
  }

  private Map<String, Long> runSearchAggregate(String aggregationField, SearchRequest query)  {
    executeSearch(query);

    Map<String, Long> aggregatedEvents = getAggregationsFromResponse(aggregationField, response);
    log.info("Extracted aggregation results from index={} eventnumber={}", energyStockIndex, aggregatedEvents.size());

    return aggregatedEvents;
  }

  private void executeSearch(SearchRequest query) {
    try {
      response = elasticsearchClient.search(query, Stock.class);
    } catch (IOException | OpenSearchException e) {
      log.error("Could not connect to database.", e);
      throw new ElasticsearchConnectorException(e);
    }
  }

  private List<Stock> getEventListFromResponse(SearchResponse<Stock> response) {
    return response.hits().hits().stream()
        .map(Hit::source)
        .collect(Collectors.toList());
  }

  private SearchRequest.Builder createRequestQuery(Integer from, Integer size, List<String> sortingCriteria) {
    log.info("Creating request query");
    return new SearchRequest.Builder().index(energyStockIndex).from(from).size(size).sort(createSortingCriteria(sortingCriteria));
  }

  private List<SortOptions> createSortingCriteria(List<String> sortingCriteria) {
    Pattern regex = Pattern.compile("([^,]*),?(asc|desc)?", Pattern.CASE_INSENSITIVE);
    List<SortOptions> sortOrders = new ArrayList<>();

    for (var criteria : sortingCriteria) {
      Matcher m = regex.matcher(criteria);
      if (m.find()) {
        SortOrder direction = m.groupCount() > 1 && "desc".equals(m.group(2)) ? SortOrder.Desc : SortOrder.Asc;
        SortOptions sortOptions = SortOptions.of(sOrder -> sOrder
            .field(fd -> fd.field(m.group(1)).order(direction)));
        sortOrders.add(sortOptions);
      }
    }

    return sortOrders;
  }

  @Override
  public List<Stock> search(FilterParameters filterParameters) {
    BoolQuery.Builder queryBuilder = new BoolQuery.Builder();
    List<QueryVariant> criteria;

    try {
      criteria = getFilterCriteria(filterParameters)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
             NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    List<Query> queryCriteria = criteria.stream().map(QueryVariant::_toQuery).collect(Collectors.toList());
    queryBuilder.must(queryCriteria);

    var listOfEvents = runSearch(createRequestQuery(filterParameters.getFrom(), filterParameters.getSize(), filterParameters.getSortingCriteria())
        .query(queryBuilder.build()._toQuery())
        .build());

    log.info("list of stocks={}", listOfEvents);
    return listOfEvents;
  }

  @Override
  public Map<String, Long> aggregate(FilterParameters filterParameters) {
    BoolQuery.Builder queryBuilder = new BoolQuery.Builder();
    String aggregationField = filterParameters.getField();
    List<QueryVariant> filters;

    try {
      filters = getFilterCriteria(filterParameters)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
             NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    Map<String, Aggregation> aggregations = Stream.of(getAggregation(aggregationField, getFieldKeyword(aggregationField),
            (Object x) -> TermsAggregation.of(t -> t.field((String) x))))
        .filter(Objects::nonNull).map(AggregationVariant::_toAggregation)
        .collect(Collectors.toMap((f) -> aggregationField, Function.identity()));

    List<Query> queryFilters = filters.stream().map(QueryVariant::_toQuery).collect(Collectors.toList());
    queryBuilder.filter(queryFilters);

    return runSearchAggregate(aggregationField, createRequestQuery(filterParameters.getFrom(),
        filterParameters.getSize(), filterParameters.getSortingCriteria())
        .aggregations(aggregations)
        .query(queryBuilder.build()._toQuery())
        .build());
  }

  private QueryVariant getQueryVariant(String field, Object fieldValue, BiFunction<String, Object, QueryVariant> fn) {
    if (null == field || null == fieldValue) {
      return null;
    }

    return fn.apply(field, fieldValue);
  }

  private AggregationVariant getAggregation(String aggregationName, String field, Function<Object, AggregationVariant> fn) {
    if (null == aggregationName || null == field) {
      return null;
    }

    return fn.apply(field);
  }

  private Map<String, Long> getAggregationsFromResponse(String aggregationField, SearchResponse<Stock> response) {
    if (null == aggregationField || null == response.aggregations().get(aggregationField)) {
      return Collections.emptyMap();
    }

    return response.aggregations()
        .get(aggregationField)
        .sterms()
        .buckets()
        .array()
        .stream()
        .collect(Collectors.toMap(StringTermsBucket::key, MultiBucketBase::docCount));
  }

  private RangeQuery getRangeQuery(String field, String fieldValueStart, String fieldValueEnd) {
    if (null == field || null == fieldValueStart && null == fieldValueEnd) {
      return null;
    }

    return RangeQuery.of(m -> m.field(field)
        .gte(getJsonDataFromDate(fieldValueStart))
        .lte(getJsonDataFromDate(fieldValueEnd)));
  }

  private BiFunction<String, Object, QueryVariant> getLteRangeQuery() {
    return (field, value) -> RangeQuery.of(rq -> rq.field(field)
        .lte(JsonData.of(value)));
  }

  private BiFunction<String, Object, QueryVariant> getGteRangeQuery() {
    return (field, value) -> RangeQuery.of(rq -> rq.field(field)
        .gte(JsonData.of(value)));
  }

  private JsonData getJsonDataFromDate(String date) {
    if (null == date) {
      return null;
    }

    return JsonData.of(Date.from(Instant.parse(date)).getTime());
  }

  private Stream<QueryVariant> getFilterCriteria(FilterParameters filterParameters) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    Map<Field, Method> queryMethodsFields = getFieldMethodMapping(FilterParameters.class, MatchQueryMethod.class);
    Map<Field, Method> rangeQueryMethodsFields = getFieldMethodMapping(FilterParameters.class, RangeQueryMethod.class);

    Stream<QueryVariant> rangeQueryStream = createQueryVariantStreamFromFieldMethodMapping(rangeQueryMethodsFields, filterParameters);
    Stream<QueryVariant> matchQueryStream = createQueryVariantStreamFromFieldMethodMapping(queryMethodsFields, filterParameters);

    Stream<QueryVariant> otherQueryStream = Stream.of(
        getQueryVariant(filterParameters.getField(), filterParameters.getWildcard(),
            (x, y) -> WildcardQuery.of(wq -> wq.field(filterParameters.getField()).wildcard((String) y))),
        getQueryVariant(filterParameters.getField(), filterParameters.getWord(),
            (x, y) -> MatchQuery.of(m -> m.field(x).query(FieldValue.of((String) y)))),
        getRangeQuery("timestamp", filterParameters.getStartDate(), filterParameters.getEndDate()));

    return Stream.concat(Stream.concat(matchQueryStream, otherQueryStream), rangeQueryStream);
  }

  private BiFunction<String, Object, QueryVariant> getMatchQuery() {
    return (field, value) -> MatchQuery.of(m -> m.field(field).query(FieldValue.of((String) value)));
  }

  private String getFieldKeyword(String field) {
    if (null == field) {
      return null;
    }

    switch (field.toLowerCase()) {
      case "symbol" : return "symbol.keyword";
      case "company" : return "company.keyword";
      case "startopenprice" :
      case "endopenprice" : return "openPrice";
      case "startcloseprice" :
      case "endcloseprice" : return "closePrice";
      case "starthighprice" :
      case "endhighprice" : return "highPrice";
      case "startlowprice" :
      case "endlowprice" : return "lowPrice";
      default: break;
    }

    return null;
  }

  private Stream<QueryVariant> createQueryVariantStreamFromFieldMethodMapping(Map<Field, Method> fieldMethodMap, FilterParameters filterParameters) {
    return fieldMethodMap
        .entrySet()
        .stream()
        .map(entry -> getQueryVariantFromFieldMethodMapping(entry.getKey(), entry.getValue(), filterParameters));
  }

  private QueryVariant getQueryVariantFromFieldMethodMapping(Field field, Method method, FilterParameters filterParameters) {
    QueryVariant queryVariant;

    try {
      queryVariant = getQueryVariant(getFieldKeyword(field.getName()), field.get(filterParameters), (BiFunction) method.invoke(this));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    return queryVariant;
  }

  private Map<Field, Method> getFieldMethodMapping(Class<?> cls, Class<? extends Annotation> annotation) {
    return FieldUtils.getFieldsListWithAnnotation(cls, annotation)
        .stream()
        .peek(i -> i.setAccessible(true))
        .collect(Collectors.toMap(Function.identity(), field -> getMethodFromField(field, annotation)));
  }

  private Method getMethodFromField(Field field, Class<? extends Annotation> annotation) {
    Method method = null;

    try {
      Class<?> c = Class.forName(this.getClass().getCanonicalName());

      if (annotation.getTypeName().equals(MatchQueryMethod.class.getCanonicalName())) {
        method = c.getDeclaredMethod(((MatchQueryMethod) field.getAnnotation(annotation)).value());
      }

      if (annotation.getTypeName().equals(RangeQueryMethod.class.getCanonicalName())) {
        method = c.getDeclaredMethod(((RangeQueryMethod) field.getAnnotation(annotation)).value());
      }

    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }

    return method;
  }
}
