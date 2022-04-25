# Products Restful Webservice

This is a Java / Maven / Spring Boot (version 2.5.0) application that creates a restful webservice to request dresses that have a price reduction from the below API,
[https://api.johnlewis.com/search/api/rest/v2/catalog/products/search/keyword?q=dresses](https://api.johnlewis.com/search/api/rest/v2/catalog/products/search/keyword?q=dresses&key=AIzaSyDD_6O5gUgC4tRW5f9kxC0_76XRC8W7_mI)

## About the Service

The array of products processed from the [provided api url](https://api.johnlewis.com/search/api/rest/v2/catalog/products/search/keyword?q=dresses&key=AIzaSyDD_6O5gUgC4tRW5f9kxC0_76XRC8W7_mI) should only contain products with a price reduction and should be sorted to show the highest price reduction first. 
Price reduction is calculated using price.was - price.now.

**NOTE:**
_If there is invalid data in the [used](https://api.johnlewis.com/search/api/rest/v2/catalog/products/search/keyword?q=dresses&key=AIzaSyDD_6O5gUgC4tRW5f9kxC0_76XRC8W7_mI) api response then assuming a value of zero rather than throwing exceptions or creating complex error logging._

Each products in the returned array should contain:

`productId <String>`
`title <String>`
`An array of colorSwatches`. Each element should contain:
-  `color<String>`
- `rgbColor<String>` which is an RGB representation of the basicColor in a six digit
hexadecimal format, e.g. “F0A1C2”.
- `skuid<String>`
- `nowPrice<String>` which is the price.now represented as a string, including the currency, e.g.
“£1.75”. For values that are integer, if they are less £10 return a decimal price, otherwise show an
integer price, e.g. “£2.00” or “£10”.

`priceLabel<String>`. An optional query parm called labelType can be set to any of:
1. **ShowWasNow** - in which case return a string saying “Was £x.xx, now £y.yyy”.
2. **ShowWasThenNow** - in which case return a string saying “Was £x.xx, then £y.yy, now
£z.zzz”. If the original price.then2 is not empty use that for the “then” price otherwise use
the then1 price. If the then1 price is also empty then don’t show the “then” price.
3. **ShowPercDscount** - in which case return “x% off - now £y.yy”.
If the query parm is not set default to use ShowWasNow format.
In all cases use the price formatting as described for nowPrice

### Retrieve list of products

```
GET http://localhost:1221/price-reduction/fetchProducts?labelType=ShowWasNow

http-status: 200
connection: keep-alive 
content-type: application/json 
date: Fri,11 Jun 2021 07:53:46 GMT 
keep-alive: timeout=60 
transfer-encoding: chunked 

Response Body: 
[
  {
    "productId": "4873363",
    "title": "Ghost Astrid Floral Dress, Navy Clusters",
    "colorSwatches": [
      {
        "color": "Blue",
        "rgbColor": "0000FF",
        "skuId": "238346446"
      }
    ],
    "nowPrice": "£74",
    "priceLabel": "Was £149, now £74"
  },
  {
    "productId": "4823284",
    "title": "Hobbs Astraea Floral Dress, Navy/Ivory",
    "colorSwatches": [
      {
        "color": "Blue",
        "rgbColor": "0000FF",
        "skuId": "238448191"
      }
    ],
    "nowPrice": "£99",
    "priceLabel": "Was £169, now £99"
  }
]
```

## How to Run 

This application is packaged as a jar which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the ```java -jar``` command.

* Clone this repository 
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:
```
        java -jar target/price-reduction-0.0.1-SNAPSHOT.war
or
        mvn spring-boot:run
```

Once the application runs you should see something like this

```
2021-06-11 13:18:04.658  INFO 7108 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 1221 (http) with context path '/price-reduction'
2021-06-11 13:18:04.667  INFO 7108 --- [  restartedMain] com.java.app.DiscountPriceApplication   : Started DiscountPriceApplication in 2.266 seconds (JVM running for 3.312)
```


## Flow of the CodeBase

1. c.j.a.c.DiscountPriceController         : Request Recielved from client: http://localhost:1221/price-reduction/fetchProducts
2. c.j.a.c.DiscountPriceController         : Inside DiscountPriceController Class....
3. c.j.a.c.DiscountPriceController         : Calling getProducts() method....
4. c.j.a.c.DiscountPriceController         : Fetching Products from Service....
5. c.j.a.service.DiscountPriceServiceImpl  : Inside DiscountPriceServiceImpl Class....
6. c.j.a.service.DiscountPriceServiceImpl  : Calling fetchAllProducts(ShowWasNow) method....
7. c.j.a.service.DiscountPriceServiceImpl  : Fetching Products from Repository....
8. c.j.a.r.DiscountPriceRepositoryImpl     : Inside DiscountPriceRepositoryImpl Class....
9. c.j.a.r.DiscountPriceRepositoryImpl     : Calling fetchAllProducts() method....
10. c.j.a.r.DiscountPriceRepositoryImpl     : Fetching productUrl and productUrlKey from 11. api-url.properties file....
12. c.j.a.r.DiscountPriceRepositoryImpl     : Hitting the Product API url: https://api.johnlewis.com/search/api/rest/v2/catalog/products/search/keyword?q=dresses&key=AIzaSyDD_6O5gUgC4tRW5f9kxC0_76XRC8W7_mI
13. c.j.a.r.DiscountPriceRepositoryImpl     : Got the response code: 200 OK
14. c.j.a.r.DiscountPriceRepositoryImpl     : Returning the Product response to the Service....
15. c.j.a.service.DiscountPriceServiceImpl  : Fetched Done from Repository....
16. c.j.a.service.DiscountPriceServiceImpl  : Processing the fetched Products....
17. c.j.a.service.DiscountPriceServiceImpl  : Processing Done....
18. c.j.a.service.DiscountPriceServiceImpl  : Sorting the fetched Products based on reduced price....
19. c.j.a.service.DiscountPriceServiceImpl  : Sorting Done....
20. c.j.a.service.DiscountPriceServiceImpl  : Sending Processed Products (DTO) to the controller....
21. c.j.a.c.DiscountPriceController         : Fetched Done from Service....
22. c.j.a.c.DiscountPriceController         : Returing the response to the client....

