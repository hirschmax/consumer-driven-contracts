package com.example.order;

import au.com.dius.pact.consumer.dsl.*;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.order.model.OrderRequest;
import com.example.order.model.Receipt;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "receipts", port = "8095")
@QuarkusTest
public class UseCaseGetReceiptForOrderContractTest {

    @Pact(consumer = "order")
    public V4Pact pactToGetReceiptForOneProduct(PactDslWithProvider builder) {
        var headers = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        var receiptRequest = newJsonBody(body -> body
                .array("productIds", array -> array
                        .stringValue("M1")
                )
                .stringType("discountCode")
        ).build();
        var responseBody = newJsonBody(body -> body
                .array("products", array -> array
                    .object(o -> o
                        .stringType("id")
                        .stringType("name")
                        .numberType("price")
                    )
                )
                .object("total", o -> o
                        .numberType("value")
                )
        ).build();

        return builder.uponReceiving("post request").path("/api/receipts").headers(headers).method(HttpMethod.POST).body(receiptRequest).willRespondWith().status(Response.Status.OK.getStatusCode()).headers(headers).body(responseBody).toPact(V4Pact.class);
    }

    @Pact(consumer = "order")
    public V4Pact pactToGetReceiptForTwoProducts(PactDslWithProvider builder) {
        var headers = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        var receiptRequest = newJsonBody(body -> body
                .array("productIds", array -> array
                        .stringValue("M1")
                        .stringValue("M2")
                )
                .stringType("discountCode")
        ).build();
        var responseBody = newJsonBody(body -> body
                .array("products", array -> array
                        .object(o -> o
                                .stringValue("id", "M1")
                                .stringType("name")
                                .numberType("price")
                        )
                        .object(o -> o
                                .stringValue("id", "M2")
                                .stringType("name")
                                .numberType("price")
                        )
                )
                .object("total", o -> o
                        .numberType("value")
                )
        ).build();
        return builder.uponReceiving("post request").path("/api/receipts").headers(headers).method(HttpMethod.POST).body(receiptRequest).willRespondWith().status(Response.Status.OK.getStatusCode()).headers(headers).body(responseBody).toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "pactToGetReceiptForOneProduct")
    public void placeOrderWithOneItem() {
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
    }

    @Test
    @PactTestFor(pactMethod = "pactToGetReceiptForTwoProducts")
    public void placeOrderWithTwoItems() {
        OrderRequest orderRequest = new OrderRequest(List.of("M1", "M2"), "");
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
        assertThat(response.products().get(0).id()).isEqualTo("M1");
        assertThat(response.products().get(1).id()).isEqualTo("M2");
    }
}

