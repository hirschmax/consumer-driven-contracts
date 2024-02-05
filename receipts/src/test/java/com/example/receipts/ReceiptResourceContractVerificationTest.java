package com.example.receipts;


import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.receipts.model.Product;
import com.example.receipts.service.MockProductService;
import com.example.receipts.service.ProductService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@Provider("receipts")
@PactFolder("pacts")
@QuarkusTest
class ReceiptResourceContractVerificationTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    int quarkusPort;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", quarkusPort));
        MockProductService productServiceMock = new MockProductService();
        productServiceMock.setProductRepository(Map.of(
                "M1", new Product("M1", "Fries", 5.99),
                "M2", new Product("M2", "Soup", 8.49),
                "M3", new Product("M3", "Salad", 9.99)
        ));
        QuarkusMock.installMockForType(productServiceMock, ProductService.class, RestClient.LITERAL);
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}