package com.java.app.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import com.java.app.dto.ColorSwatchDto;
import com.java.app.dto.ProductDto;
import com.java.app.service.DiscountPriceService;

@RunWith(SpringRunner.class)
@WebMvcTest(DiscountPriceController.class)
public class DiscountPriceControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private DiscountPriceService service;

	@Test
	public void givenProducts_whenGetproducts_thenReturnJsonArray_v1()
			throws Exception {

		List<ProductDto> products = new ArrayList<>();
		ColorSwatchDto cs = null;
		List<ColorSwatchDto> colorSwatchs = null;

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatchDto("Black", "000000", "1234");
		colorSwatchs.add(cs);
		ProductDto product1 = new ProductDto("4957129", "Ghost Astrid Floral Dress", colorSwatchs, "£89", "Was £129, now £89", 129.00-89.00);
		products.add(product1);

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatchDto("Black", "000000", "1234");
		colorSwatchs.add(cs);
		cs = new ColorSwatchDto("White", "FFFFFF", "1235");
		colorSwatchs.add(cs);
		ProductDto product2 = new ProductDto("4873363", "Ghost Astrid Floral Dress", colorSwatchs, "£99", "Was £169, now £99", 149.00-74.00);
		products.add(product2);

		given(service.fetchAllProducts("ShowWasNow")).willReturn(products);

		mvc.perform(get("/fetchProducts")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].productId", is(product1.getProductId())))
		.andExpect(jsonPath("$[0].title", is(product1.getTitle())))
		.andExpect(jsonPath("$[0].nowPrice", is(product1.getNowPrice())))
		.andExpect(jsonPath("$[0].priceLabel", is(product1.getPriceLabel())))
		.andExpect(jsonPath("$[0].colorSwatches[0].color", is(product1.getColorSwatches().get(0).getColor())))
		.andExpect(jsonPath("$[0].colorSwatches[0].rgbColor", is(product1.getColorSwatches().get(0).getRgbColor())))
		.andExpect(jsonPath("$[0].colorSwatches[0].skuId", is(product1.getColorSwatches().get(0).getSkuId())));
		

	}
	
	@Test
	public void givenProducts_whenGetproducts_thenReturnJsonArray_v2()
			throws Exception {

		List<ProductDto> products = new ArrayList<>();
		ColorSwatchDto cs = null;
		List<ColorSwatchDto> colorSwatchs = null;

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatchDto("Black", "000000", "1234");
		colorSwatchs.add(cs);
		ProductDto product1 = new ProductDto("4957129", "Ghost Astrid Floral Dress", colorSwatchs, "£89", "Was £129, then £0.00, now £69", 129.00-89.00);
		products.add(product1);

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatchDto("Black", "000000", "1234");
		colorSwatchs.add(cs);
		cs = new ColorSwatchDto("White", "FFFFFF", "1235");
		colorSwatchs.add(cs);
		ProductDto product2 = new ProductDto("4873363", "Ghost Astrid Floral Dress", colorSwatchs, "£99", "Was £179, then £0.00, now £99", 149.00-74.00);
		products.add(product2);

		given(service.fetchAllProducts("ShowWasThenNow")).willReturn(products);

		mvc.perform(get("/fetchProducts?labelType=ShowWasThenNow")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].productId", is(product1.getProductId())))
		.andExpect(jsonPath("$[0].title", is(product1.getTitle())))
		.andExpect(jsonPath("$[0].nowPrice", is(product1.getNowPrice())))
		.andExpect(jsonPath("$[0].priceLabel", is(product1.getPriceLabel())))
		.andExpect(jsonPath("$[0].colorSwatches[0].color", is(product1.getColorSwatches().get(0).getColor())))
		.andExpect(jsonPath("$[0].colorSwatches[0].rgbColor", is(product1.getColorSwatches().get(0).getRgbColor())))
		.andExpect(jsonPath("$[0].colorSwatches[0].skuId", is(product1.getColorSwatches().get(0).getSkuId())));
		

	}
	
	@Test
	public void givenProducts_whenGetproducts_thenReturnJsonArray_v3()
			throws Exception {

		List<ProductDto> products = new ArrayList<>();
		ColorSwatchDto cs = null;
		List<ColorSwatchDto> colorSwatchs = null;

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatchDto("Black", "000000", "1234");
		colorSwatchs.add(cs);
		ProductDto product1 = new ProductDto("4957129", "Ghost Astrid Floral Dress", colorSwatchs, "£89", "47% off - now £69", 129.00-89.00);
		products.add(product1);

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatchDto("Black", "000000", "1234");
		colorSwatchs.add(cs);
		cs = new ColorSwatchDto("White", "FFFFFF", "1235");
		colorSwatchs.add(cs);
		ProductDto product2 = new ProductDto("4873363", "Ghost Astrid Floral Dress", colorSwatchs, "£99", "45% off - now £99", 149.00-74.00);
		products.add(product2);

		given(service.fetchAllProducts("ShowPercDscount")).willReturn(products);

		mvc.perform(get("/fetchProducts?labelType=ShowPercDscount")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].productId", is(product1.getProductId())))
		.andExpect(jsonPath("$[0].title", is(product1.getTitle())))
		.andExpect(jsonPath("$[0].nowPrice", is(product1.getNowPrice())))
		.andExpect(jsonPath("$[0].priceLabel", is(product1.getPriceLabel())))
		.andExpect(jsonPath("$[0].colorSwatches[0].color", is(product1.getColorSwatches().get(0).getColor())))
		.andExpect(jsonPath("$[0].colorSwatches[0].rgbColor", is(product1.getColorSwatches().get(0).getRgbColor())))
		.andExpect(jsonPath("$[0].colorSwatches[0].skuId", is(product1.getColorSwatches().get(0).getSkuId())));
		

	}
}
