package com.java.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.app.dto.ColorSwatchDto;
import com.java.app.dto.ProductDto;
import com.java.app.entity.ColorSwatch;
import com.java.app.entity.Price;
import com.java.app.entity.Product;
import com.java.app.repository.DiscountPriceRepository;
import com.java.app.utility.ColorUtil;
import com.java.app.utility.CurrencyUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DiscountPriceServiceImpl implements DiscountPriceService {

	@Autowired
	DiscountPriceRepository repo;

	@Autowired
	ColorUtil colorUtil;
	@Autowired
	CurrencyUtil currencyUtil;

	@Override
	public List<ProductDto> fetchAllProducts(String priceLabel) {

		log.info("Calling fetchAllProducts("+priceLabel+") method....");

		List<Product> fetchedProducts = repo.fetchAllProducts();
		log.info("Fetched products from Repository....");
		
		List<ProductDto> productDtoList = processFetchedProducts(priceLabel, fetchedProducts);
		log.info("Processing Done");
		return productDtoList;
	}
	
	@Override
	public List<ProductDto> processFetchedProducts(String priceLabel, List<Product> fetchedProducts) {
		
		List<ProductDto> productDtoList = fetchedProducts.stream()
				.filter(pdt->!(pdt==null))
				.map(pdt->{
					return processProductEntity(pdt, priceLabel);
				}).collect(Collectors.toList());
		
		log.info("Sorting the fetched Products based on reduced price....");
		productDtoList.sort((ProductDto p1, ProductDto p2)->p2.getPriceReduced().compareTo(p1.getPriceReduced()));
		log.info("Sorting Done....");
		
		return productDtoList;
	}

	private ProductDto processProductEntity(Product pdt, String priceLabel) {
		ProductDto productDto = new ProductDto();
		if(pdt!=null) {

			List<ColorSwatchDto> colorSwatchList = mapColorSwatchDtoList(pdt.getColorSwatches());

			Price price = Optional.ofNullable(pdt.getPrice()).orElse(new Price());
			String currencyCode = extractCurrencyCode(price);
			String nowPriceStr = extractFormattedPriceValue(price, "now");

			Double reducedPrice = extractReducedPrice(price);

			String proccessedPriceLabel = extractPriceLabel(priceLabel, price);
			
			productDto.setProductId(pdt.getProductId());
			productDto.setTitle(pdt.getTitle());
			productDto.setColorSwatches(colorSwatchList);
			productDto.setNowPrice(currencyCode + nowPriceStr);
			productDto.setPriceLabel(proccessedPriceLabel);
			productDto.setPriceReduced(reducedPrice);
		}
		return productDto;
	}
	
	@Override
	public Double extractReducedPrice(Price price) {

		String nowPriceStr = extractFormattedPriceValue(price, "now");
		String wasPriceStr=extractFormattedPriceValue(price, "was");

		return Double.parseDouble(wasPriceStr)-Double.parseDouble(nowPriceStr);
	}
	
	@Override
	public String extractFormattedPriceValue(Price price, String priceType) {
		String priceStr="";
		String fetchedPriceStr = "";
		switch(priceType.trim().toLowerCase()) {
		case "was":
			fetchedPriceStr = (String) Optional.ofNullable(price.getWas()).orElse("");
			priceStr=currencyUtil.extractPriceFormat(fetchedPriceStr);
			break;
		case "then1":
			fetchedPriceStr = (String) Optional.ofNullable(price.getThen1()).orElse("");
			priceStr=currencyUtil.extractPriceFormat(fetchedPriceStr);
			break;
		case "then2":
			fetchedPriceStr = (String) Optional.ofNullable(price.getThen2()).orElse("");
			priceStr=currencyUtil.extractPriceFormat(fetchedPriceStr);
			break;
		default:
			fetchedPriceStr = (String) Optional.ofNullable(price.getNow()).orElse("");
			priceStr=currencyUtil.extractPriceFormat(fetchedPriceStr);
			break;
		}
		return priceStr;
	}

	public String extractCurrencyCode(Price price) {
		String currency = Optional.ofNullable(price.getCurrency()).orElse("");
		String currencyCode = currencyUtil.isoCodeToSymbol(currency);
		return currencyCode;
	}

	@Override
	public String extractPriceLabel(String priceLabel, Price price) {

		String currencyCode = extractCurrencyCode(price);
		String nowPriceStr = extractFormattedPriceValue(price, "now");
		String wasPriceStr=extractFormattedPriceValue(price, "was");
		Double reducedPrice = extractReducedPrice(price);

		String proccessedPriceLabel = "Was " + currencyCode + wasPriceStr 
				+ ", now " + currencyCode + nowPriceStr;
		switch(Optional.ofNullable(priceLabel).orElse("").toLowerCase()) {
		case "showwasthennow":
			String thenPrice = "";
			thenPrice=extractFormattedPriceValue(price, "then2");
			if(thenPrice.equals("0.00")) {
				thenPrice=extractFormattedPriceValue(price, "then1");
			}
			proccessedPriceLabel = "Was " + currencyCode + wasPriceStr
					+ ", then " + currencyCode + thenPrice
					+ ", now " + currencyCode + nowPriceStr;
			break;
		case "showpercdscount":
			String discountStr = Math.round(100*reducedPrice/Double.parseDouble(wasPriceStr)) + "%";
			proccessedPriceLabel = discountStr + " off - " + "now " + currencyCode + nowPriceStr;
			break;
		}
		return proccessedPriceLabel;
	}
	
	private List<ColorSwatchDto> mapColorSwatchDtoList(List<ColorSwatch> colorSwatchList) {
		List<ColorSwatchDto> colorSwatchDtoList = new ArrayList<>();
		if(colorSwatchList.size()>0) {
			colorSwatchDtoList = colorSwatchList.stream()
					.map(eachColorSwatch->{
						return extractColorSwatch(eachColorSwatch);
					}).collect(Collectors.toList());
		}
		return colorSwatchDtoList;
	}

	@Override
	public ColorSwatchDto extractColorSwatch(ColorSwatch eachColorSwatch) {
		return new ColorSwatchDto(
				eachColorSwatch.getBasicColor(), 
				colorUtil.basicToRgb(eachColorSwatch.getBasicColor()), 
				eachColorSwatch.getSkuId());
	}

}
