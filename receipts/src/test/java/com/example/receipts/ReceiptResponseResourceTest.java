package com.example.receipts;

import com.example.receipts.model.Product;
import com.example.receipts.model.ReceiptResponse;
import com.example.receipts.model.ReceiptRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ReceiptResponseResourceTest {

    @Test
    void shouldCalculateReceiptWithoutDiscount() {
        ReceiptRequest request = new ReceiptRequest(List.of(new Product("M1", "", 10d), new Product("M2", "", 5.49)), "");
        ReceiptResponse response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/api/receipts")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .body()
                .as(ReceiptResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.total().value()).isEqualTo(15.49);
    }

    @Test
    void shouldCalculateReceiptWithTenPercentDiscount() {
        ReceiptRequest request = new ReceiptRequest(List.of(new Product("M1", "", 10d), new Product("M2", "", 5.49)), "TEN");
        ReceiptResponse response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/api/receipts")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .body()
                .as(ReceiptResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.total().value()).isEqualTo(13.94);
    }
}