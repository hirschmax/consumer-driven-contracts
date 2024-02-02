package com.example.order;

import au.com.dius.pact.consumer.dsl.*;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.order.model.OrderRequest;
import com.example.order.model.Receipt;
import com.example.order.service.MockReceiptService;
import com.example.order.service.ReceiptService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpHeaders;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArray;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "products", port = "8094")
@QuarkusTest
public class UseCaseGetReceiptForOrderContractTest {

    @BeforeEach
    void setUp() {
        QuarkusMock.installMockForType(new MockReceiptService(), ReceiptService.class, RestClient.LITERAL);
    }

    @Pact(consumer = "order")
    public V4Pact getProductListForM1(PactDslWithProvider builder) {
        var headers = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        var orderBody = newJsonBody(body -> body.array("ids", array -> array.stringValue("M1"))).build();
        var responseBody = newJsonArray(array -> array.object(body -> body.stringValue("id","M1").stringType("name").numberType("price"))).build();

        return builder.uponReceiving("post request").path("/products/list").headers(headers).method(HttpMethod.POST).body(orderBody).willRespondWith().status(Response.Status.OK.getStatusCode()).headers(headers).body(responseBody).toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getProductListForM1")
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

}

