package com.energystockaudit.stockaudit.resource;

import com.energystockaudit.stockaudit.domain.FilterParameters;
import com.energystockaudit.stockaudit.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
@Component
@Produces(MediaType.APPLICATION_JSON)
public class StockResource {
  @Autowired
  private StockService stockService;

  @GET
  @Path("/stocks/select")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStocksElasticsearch(@BeanParam FilterParameters filterParameters) {
    return Response.ok(stockService.search(filterParameters)).build();
  }

  @GET
  @Path("/stocks/aggregate")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAggregateElasticsearch(@BeanParam FilterParameters filterParameters) {
    return Response.ok(stockService.aggregate(filterParameters)).build();
  }
}