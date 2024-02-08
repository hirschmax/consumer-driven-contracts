package com.example.receipts;

import com.example.receipts.model.Product;
import com.example.receipts.model.ReceiptResponse;
import com.example.receipts.model.ReceiptRequest;
import com.example.receipts.service.DiscountService;
import com.example.receipts.service.MockProductService;
import com.example.receipts.service.ProductService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ReceiptResponseResourceTest {

    @Inject
    DiscountService discountService;

    @BeforeEach
    void setUp() {
        MockProductService productServiceMock = new MockProductService();
        productServiceMock.setProductRepository(Map.of(
                "M1", new Product("M1", "Fries", 5.99),
                "M2", new Product("M2", "Soup", 8.49),
                "M3", new Product("M3", "Salad", 9.99)
        ));
        QuarkusMock.installMockForType(productServiceMock, ProductService.class, RestClient.LITERAL);
    }

    @Test
    void shouldCalculateReceiptWithoutDiscount() {
        ReceiptRequest request = new ReceiptRequest(List.of("M1", "M2"), "SOME_INVALID_CODE");
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
        assertThat(response.total().value()).isEqualTo(14.48);
    }

    @Test
    void shouldCalculateReceiptWithTenPercentDiscount() {
        discountService.addDiscountCode("TEST2024", 10);
        ReceiptRequest request = new ReceiptRequest(List.of("M2", "M3"), "TEST2024");
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
        assertThat(response.total().value()).isEqualTo(16.63);
    }
}