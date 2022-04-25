package com.java.app.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.java.app.dto.ProductDto;
import com.java.app.entity.ColorSwatch;
import com.java.app.entity.Price;
import com.java.app.entity.Product;
import com.java.app.repository.DiscountPriceRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiscountPriceServiceImplTest {

	@Autowired
    DiscountPriceService service;

	@MockBean
	private DiscountPriceRepository repo;

	@Before
	public void setUp() {

		List<Product> products = new ArrayList<>();
		ColorSwatch cs = null;
		Price price = null;
		Product product = null;
		List<ColorSwatch> colorSwatchs = null;

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatch("Black", "1234");
		colorSwatchs.add(cs);
		price = new Price("169.00", "0.00", "0.00", "110", "GBP");
		product = new Product("4981457", "Whistles Elephant Print", price, colorSwatchs);
		products.add(product);

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatch("Black", "1234");
		colorSwatchs.add(cs);
		cs = new ColorSwatch("White", "1235");
		colorSwatchs.add(cs);
		price = new Price("149.00", "0.00", "0.00", "74", "GBP");
		product = new Product("4873363", "Ghost Astrid Floral Dress", price, colorSwatchs);
		products.add(product);

		colorSwatchs = new ArrayList<>();
		cs = new ColorSwatch("White", "1235");
		colorSwatchs.add(cs);
		price = new Price("169.00", "0.00", "0.00", "99", "GBP");
		product = new Product("4823284", "Hobbs Astraea Floral Dress", price, colorSwatchs);
		products.add(product);

		Mockito.when(repo.fetchAllProducts())
		.thenReturn(products);
	}

	@Test
	public void whenCallingService_thenProductsShouldBeReturnedFromRepo() {

		assertThat(
				repo.fetchAllProducts().size()
				).isEqualTo(3);

	}

	@Test
	public void whenCallingService_thenExpectListOfProductDto() {

		assertThat(
				service.processFetchedProducts("ShowWasNow", repo.fetchAllProducts()).get(0)
				).isInstanceOf(ProductDto.class);

	}

	@Test
	public void whenCallingService_thenExpectListOfProductDtoSortedByReducedPriceDesc() {

		List<ProductDto> productDtos = service.processFetchedProducts("ShowWasNow", repo.fetchAllProducts());

		assertThat(
				productDtos.get(0).getProductId()
				).isEqualTo("4873363");
		assertThat(
				productDtos.get(1).getProductId()
				).isEqualTo("4823284");
		assertThat(
				productDtos.get(2).getProductId()
				).isEqualTo("4981457");

	}

	@Test
	public void whenGivingBasicColor_thenGetRGBCode() {

		ColorSwatch cs = new ColorSwatch("Black", "1234");
		assertThat(
				service.extractColorSwatch(cs).getRgbColor()
				).isEqualTo("000000");

		cs = new ColorSwatch("White", "1235");
		assertThat(
				service.extractColorSwatch(cs).getRgbColor()
				).isEqualTo("FFFFFF");

		cs = new ColorSwatch("Red", "1236");
		assertThat(
				service.extractColorSwatch(cs).getRgbColor()
				).isEqualTo("FF0000");

	}

	@Test
	public void whenPriceLessThan10_thenPriceWillBeDecimalPrice() {

		Price price = new Price();
		price.setNow("9.99");

		assertThat(
				service.extractFormattedPriceValue(price, "Now")
				).isEqualTo("9.99");

	}

	@Test
	public void whenPriceGreterOrEqualThan10_thenPriceWillBeIntegerPrice() {

		Price price = new Price();
		price.setNow("11.99");

		assertThat(
				service.extractFormattedPriceValue(price, "Now")
				).isEqualTo("11");

	}

	@Test
	public void whenPriceNotAvailableOrCorrupt_thenPriceWillZero() {

		Price price = new Price();

		price.setNow("");
		assertThat(
				service.extractFormattedPriceValue(price, "Now")
				).isEqualTo("0.00");

		price.setNow(null);
		assertThat(
				service.extractFormattedPriceValue(price, "Now")
				).isEqualTo("0.00");

	}

	@Test
	public void testReducedPrice() {

		Price price = new Price("20.00", "0.00", "0.00", "15.00", "GBP");

		assertThat(
				service.extractReducedPrice(price)
				).isEqualTo(5.00);

	}

	@Test
	public void whenPriceLabelIsNotPresent_thenExpectWasNowInPriceLabe() {

		Price price = new Price("12.45", "0.00", "0.00", "9.56", "GBP");

		assertThat(
				service.extractPriceLabel("", price)
				).isEqualTo("Was £12, now £9.56");
		assertThat(
				service.extractPriceLabel(null, price)
				).isEqualTo("Was £12, now £9.56");

	}

	@Test
	public void whenPriceLabelIsShowWasNow_thenExpectWasNowInPriceLabel() {

		Price price = new Price("12.45", "0.00", "0.00", "9.56", "GBP");

		assertThat(
				service.extractPriceLabel("ShowWasNow", price)
				).isEqualTo("Was £12, now £9.56");

	}

	@Test
	public void whenPriceLabelIsShowWasThenNow_thenExpectWasThenNowInPriceLabel_v1() {
		/*
		 * then1 and then2 both present
		 */
		Price price = new Price("12.45", "5.00", "8.19", "9.56", "GBP");

		assertThat(
				service.extractPriceLabel("ShowWasThenNow", price)
				).isEqualTo("Was £12, then £8.19, now £9.56");

	}

	@Test
	public void whenPriceLabelIsShowWasThenNow_thenExpectWasThenNowInPriceLabel_v2() {
		/*
		 * then1 is present and then2 is not present
		 */
		Price price = new Price("12.45", "5.00", "", "9.56", "GBP");

		assertThat(
				service.extractPriceLabel("ShowWasThenNow", price)
				).isEqualTo("Was £12, then £5.0, now £9.56");

	}

	@Test
	public void whenPriceLabelIsShowPercDscount_thenExpectNowPercDscountInPriceLabel() {

		Price price = new Price("20.00", "0.00", "0.00", "15.00", "GBP");

		assertThat(
				service.extractPriceLabel("ShowPercDscount", price)
				).isEqualTo("25% off - now £15");

	}

}
