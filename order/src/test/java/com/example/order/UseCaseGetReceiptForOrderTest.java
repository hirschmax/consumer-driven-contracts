package com.example.order;

import com.example.order.model.Receipt;
import com.example.order.service.MockProductService;
import com.example.order.service.MockReceiptService;
import com.example.order.service.ProductService;
import com.example.order.service.ReceiptService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import com.example.order.model.OrderRequest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class UseCaseGetReceiptForOrderTest {

    @BeforeEach
    void setUp() {
        QuarkusMock.installMockForType(new MockProductService(), ProductService.class, RestClient.LITERAL);
        QuarkusMock.installMockForType(new MockReceiptService(), ReceiptService.class, RestClient.LITERAL);
    }

    @Test
    void placeOrder() {
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
}

