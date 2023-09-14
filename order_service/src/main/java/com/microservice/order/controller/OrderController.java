package com.microservice.order.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.client.EmailWebClient;
import com.microservice.order.client.ProductWebClient;
import com.microservice.order.model.OrderDetails;
import com.microservice.order.request.OrderVo;
import com.microservice.order.response.ResponseBuilder;
import com.microservice.order.response.ResponseMessage;
import com.microservice.order.response.ResponseVO;
import com.microservice.order.service.OrderService;

@RestController
@RequestMapping(value = "/orders")
public class OrderController extends ResponseBuilder {

	private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductWebClient productWebClient;

	@Autowired
	private EmailWebClient client;

	@PostMapping
	public ResponseEntity<ResponseVO> placeOrder(@RequestBody OrderVo orderVo) {
		LOGGER.info("Start of placeOrder API");
		LOGGER.info("Map Vo TO Entity");
		OrderDetails orderDetails = orderService.mapVoToOrder(orderVo);
		LOGGER.info("Check Product To Place order");
		ResponseVO productResponse = productWebClient.checkproductExists(orderDetails);
		if (productResponse.getStatusCode().contains("404")) {
			LOGGER.info("product details fetching FAILED");
			return buildErrorResponse(ResponseMessage.ORDER_DETAILS_FETCHED_FAILED);
		}
		if (!orderService.placeOrder(orderVo)) {
			LOGGER.info("placeOrder details FAILED");
			return buildErrorResponse(ResponseMessage.ORDER_PLACED_FAILED);
		}
		LOGGER.info("sending Email for order details ");
		String response = client.sendOrderedEmail(orderDetails);
		if (response.equalsIgnoreCase("FAILED")) {
			LOGGER.info("sending Email for order details FAILED");
			return buildErrorResponse(ResponseMessage.ORDER_EMAIL_SEND_FAILED);
		}
		LOGGER.info("end of placeOrder API");
		return buildSuccessResponse(response, ResponseMessage.ORDER_PLACED_SUCCESS);
	}

	@GetMapping
	public ResponseEntity<ResponseVO> getOrders() {
		LOGGER.info("Start of getOrders API ");
		List<OrderDetails> allClients = orderService.fetchOrders();
		if (allClients.size() == 0) {
			LOGGER.info("getOrders API failed");
			return buildErrorResponse(ResponseMessage.ORDER_DETAILS_AVAILABLE);
		}
		LOGGER.info("end of getOrders API ");
		return buildSuccessResponse(allClients, ResponseMessage.ORDER_DETAILS_FETCHED_FAILED);
	}

	@GetMapping(value = "/orId")
	public ResponseEntity<ResponseVO> getOrderDetail(@RequestParam(value = "orId") Integer orId) {
		LOGGER.info("Start of getOrderDetail API ");
		OrderDetails findOrderById = orderService.findOrderById(orId);
		if (findOrderById != null) {
			LOGGER.info("Fetching getOrderDetail failed ");
			return buildErrorResponse(ResponseMessage.ORDER_DETAILS_FETCHED_FAILED);
		}
		LOGGER.info("end of getOrderDetail API ");
		return buildSuccessResponse(findOrderById, ResponseMessage.ORDER_DETAILS_AVAILABLE);
	}

}
