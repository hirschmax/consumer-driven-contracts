package com.example.products;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import com.example.products.model.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ProductsResourceTest {

    @Test
    void shouldReturnProducts() {
        List<String> items = List.of("M1", "M2");
        Product[] response = given()
                .body(items)
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
    }

}