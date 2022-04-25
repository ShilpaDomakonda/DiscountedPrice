package com.java.app.repository;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.java.app.entity.Product;

public interface DiscountPriceRepository {
	public List<Product> fetchAllProducts();

	<T> ResponseEntity<T> hitApiUrl(String resourceEndpoint, Class<T> resourceClass);
}
