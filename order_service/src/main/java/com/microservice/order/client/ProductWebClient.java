package com.microservice.order.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.order.model.OrderDetails;
import com.microservice.order.response.ResponseVO;
import com.microservice.order.utils.RestEndPoints;

@Service
public class ProductWebClient {

	private static final Logger LOGGER = LogManager.getLogger(ProductWebClient.class);

	WebClient productClient = WebClient.create(RestEndPoints.PRODUCT);

	public ResponseVO checkproductExists(OrderDetails orderDetails) {
		LOGGER.info("Start of checkproductExists API");
		ResponseVO producResponse = productClient.post().uri("productExists/{name}", orderDetails.getOrderName())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve()
				.bodyToMono(ResponseVO.class)
				.block();
		LOGGER.info("end of checkproductExists API");
		return producResponse;
	}

}
