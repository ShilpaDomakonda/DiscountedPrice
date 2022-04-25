package com.java.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.app.dto.ProductDto;
import com.java.app.service.DiscountPriceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController("/api")
@Slf4j
public class DiscountPriceController {

	@Autowired
	DiscountPriceService service;

	@ApiOperation(value = "Get the array of products, contains only reduced price products and sorted to show highest reduced product first", notes = "Discounted Price Controller")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Server Error")
	})
	@GetMapping(value = {"/fetchProducts"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getProducts(
			@Parameter(
					name="labelType",
					required = false, 
					example = "ShowWasNow",
					description = "Optional query param to change priceLabel",
					schema = @Schema(description = "Optional query param to change priceLabel",type = "string", allowableValues = {"ShowWasNow", "ShowWasThenNow", "ShowPercDscount"})) 
			@RequestParam(name = "labelType", required = false) Optional<String> priceLabelOp,
			HttpServletRequest request){
		log.info("Request received from client: "+request.getRequestURL());

		try {
			log.info("Fetching products from service");
			List<ProductDto> productList = service.fetchAllProducts(priceLabelOp.orElse("ShowWasNow"));
			log.info("Fetched products from service");

			return new ResponseEntity<>(productList, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			Map<String, String> errorMap = new HashMap<>();
			return new ResponseEntity<>(errorMap.put("errorMessage", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

}
