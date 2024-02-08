package com.example.products.usecase;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.products.model.Product;
import com.example.products.repository.MockProductRepository;
import com.example.products.repository.ProductRepository;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;


@Provider("products")
@PactFolder("pacts")
@QuarkusTest
class UseCaseFindProductsContractVerificationTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    int quarkusPort;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", quarkusPort));
        MockProductRepository repository = new MockProductRepository();
        repository.setProductRepository(Map.of(
                "M1", new Product("M1", "Pizza", 9.99),
                "M2", new Product("M2", "Burger", 10.49),
                "M3", new Product("M3", "Kebab", 7.99)
        ));
        QuarkusMock.installMockForType(repository, ProductRepository.class);
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

}