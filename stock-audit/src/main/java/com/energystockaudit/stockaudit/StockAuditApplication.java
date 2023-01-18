package com.energystockaudit.stockaudit;

import com.energystockaudit.stockaudit.service.StockIngestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class StockAuditApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(StockAuditApplication.class, args);
//		var stockIngestService = context.getBean(StockIngestService.class);
//		stockIngestService.ingestStockQuotations();
	}
}
