package com.example.order.usecase;

import com.example.order.model.Product;
import com.example.order.model.Receipt;
import com.example.order.service.MockReceiptService;
import com.example.order.service.ReceiptService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import com.example.order.model.OrderRequest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class UseCaseGetReceiptForOrderTest {

    @BeforeEach
    void setUp() {
        MockReceiptService receiptServiceMock = new MockReceiptService();
        receiptServiceMock.setProductRepository(Map.of(
                "M1", new Product("M1", "Fries", 5.49),
                "M2", new Product("M2", "Soup", 7.99),
                "M3", new Product("M3", "Salad", 9.99)
        ));
        receiptServiceMock.setDiscountRepository(Map.of("TEN", 10));
        QuarkusMock.installMockForType(receiptServiceMock, ReceiptService.class, RestClient.LITERAL);
    }

    @Test
    void shouldCalculateReceiptForOneItemWithoutDiscount() {
        OrderRequest orderRequest = new OrderRequest(List.of("M1"), "");
        Receipt response = given()
                .body(orderRequest)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/order")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .body()
                .as(Receipt.class);

        assertThat(response.products()).hasSize(1);
        assertThat(response.total().value()).isEqualTo(5.49);
    }

    @Test
    void shouldCalculateReceiptForTwoItemsWithoutDiscount() {
        OrderRequest orderRequest = new OrderRequest(List.of("M2", "M3"), "TEN");
        Receipt response = given()
                .body(orderRequest)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/order")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .body()
                .as(Receipt.class);

        assertThat(response.products()).hasSize(2);
        assertThat(response.total().value()).isEqualTo(16.18);
    }
}

