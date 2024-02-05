package com.example.receipts;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.receipts.model.ReceiptRequest;
import com.example.receipts.model.ReceiptResponse;
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

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArray;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "products", port = "8094")
@QuarkusTest
class ReceiptResponseResourceContractTest {

    @Pact(consumer = "receipts")
    public V4Pact pactToGetProductForOneProductId(PactDslWithProvider builder) {
        var headers = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        var productsRequest = newJsonBody(body -> body
                .array("ids", array -> array
                        .stringValue("M1")
                )
        ).build();
        var productsResponse = newJsonArray(array -> array
                        .object(o -> o
                                .stringValue("id", "M1")
                                .stringType("name")
                                .numberType("price")
                        )
        ).build();

        return builder.uponReceiving("post request").path("/api/products").headers(headers).method(HttpMethod.POST).body(productsRequest).willRespondWith().status(Response.Status.OK.getStatusCode()).headers(headers).body(productsResponse).toPact(V4Pact.class);
    }


    @Test
    @PactTestFor(pactMethod = "pactToGetProductForOneProductId")
    void shouldGetOneProductForOneProductId() {
        ReceiptRequest request = new ReceiptRequest(List.of("M1"), "");
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
    }



    //@Test
    void shouldCalculateReceiptWithTenPercentDiscount() {
        ReceiptRequest request = new ReceiptRequest(List.of("M2", "M3"), "TEN");
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