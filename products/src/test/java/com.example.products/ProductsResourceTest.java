package com.example.products;

import com.example.products.model.ProductRequest;
import com.example.products.repository.MockProductRepository;
import com.example.products.repository.ProductRepository;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import com.example.products.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ProductsResourceTest {

    @BeforeEach
    void setUp() {
        MockProductRepository repository = new MockProductRepository();
        repository.setProductRepository(Map.of(
                "M1", new Product("M1", "Pizza", 9.99),
                "M2", new Product("M2", "Burger", 10.49),
                "M3", new Product("M3", "Kebab", 7.99)
        ));
        QuarkusMock.installMockForType(repository, ProductRepository.class);
    }

    @Test
    void shouldReturnProducts() {
        List<String> items = List.of("M1", "M2");
        Product[] response = given()
                .body(new ProductRequest(items))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/api/products")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .body()
                .as(Product[].class);

        assertThat(response).isNotNull();
        assertThat(Stream.of(response).map(Product::id)).contains("M1", "M2");
        assertThat(Stream.of(response).map(Product::name)).contains("Pizza", "Burger");
    }

}