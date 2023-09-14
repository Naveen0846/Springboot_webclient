package com.microservice.order.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.order.model.OrderDetails;

import reactor.core.publisher.Mono;

@Service
public class EmailWebClient {

	private static final Logger LOGGER = LogManager.getLogger(EmailWebClient.class);
	WebClient emailClient = WebClient.create("http://localhost:9091/");

	public String sendOrderedEmail(OrderDetails orderDetails) {
		LOGGER.info("Start of sendOrderedEmail API");
		String emailresponse = emailClient.post().uri("/email")
				.body(Mono.just(orderDetails), OrderDetails.class)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		LOGGER.info("end of sendOrderedEmail API");
		return emailresponse;
	}

}
