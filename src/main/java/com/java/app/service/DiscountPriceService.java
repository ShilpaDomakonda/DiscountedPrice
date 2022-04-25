package com.java.app.service;

import java.util.List;

import com.java.app.dto.ColorSwatchDto;
import com.java.app.dto.ProductDto;
import com.java.app.entity.ColorSwatch;
import com.java.app.entity.Price;
import com.java.app.entity.Product;

public interface DiscountPriceService {
	public List<ProductDto> fetchAllProducts(String priceLabel);

	ColorSwatchDto extractColorSwatch(ColorSwatch eachColorSwatch);

	String extractPriceLabel(String priceLabel, Price price);

	String extractFormattedPriceValue(Price price, String priceType);

	Double extractReducedPrice(Price price);

	List<ProductDto> processFetchedProducts(String priceLabel, List<Product> fetchedProducts);
}
