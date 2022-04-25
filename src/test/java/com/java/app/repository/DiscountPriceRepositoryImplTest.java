package com.java.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.java.app.entity.Product;
import com.java.app.entity.ProductResponse;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
		  locations = "classpath:api-url.properties")
public class DiscountPriceRepositoryImplTest {
	
	@Autowired
	DiscountPriceRepository repo;
	
	@Value("${product-list-url}")
	private String productUrl;
	@Value("${product-list-url-key}")
	private String productUrlKey;
	
	@Test
	public void whenHittingValidAPI_thenResponseShouldBeOK() {
		
		ResponseEntity<ProductResponse> resp = repo.hitApiUrl(productUrl + productUrlKey, ProductResponse.class);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		
	}
	
	@Test
	public void whenHittingValidAPI_thenProcessedResponseShouldBeInstancesOfProductClass() {
		
		Product product = repo.fetchAllProducts().get(0);
		assertThat(product).isInstanceOf(Product.class);
		
	}
	
	@Test
	public void whenHittingInvalidAPI_thenResponseShouldBeNotOK() {
		
		ResponseEntity<ProductResponse> resp = repo.hitApiUrl(productUrl, ProductResponse.class);
		assertThat(resp.getStatusCode()).isNotEqualTo(HttpStatus.OK);
		
	}
	
}
