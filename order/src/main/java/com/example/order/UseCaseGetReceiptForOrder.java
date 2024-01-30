package com.example.order;

import com.example.order.model.*;
import com.example.order.service.ProductService;
import com.example.order.service.ReceiptService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/order")
public class UseCaseGetReceiptForOrder {

    private final ProductService productService;
    private final ReceiptService receiptService;

    public UseCaseGetReceiptForOrder(@RestClient ProductService productService, @RestClient ReceiptService receiptService) {
        this.productService = productService;
        this.receiptService = receiptService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Receipt> calculateReceiptForOrder(OrderRequest orderRequest) {
        ProductsRequest productRequest = new ProductsRequest(orderRequest.productIds());
        List<Product> productsList = productService.getProducts(productRequest);

        ReceiptRequest receiptRequest = new ReceiptRequest(productsList, orderRequest.discountCode());
        Receipt receipt = receiptService.calculateReceipt(receiptRequest);

        return RestResponse.ResponseBuilder.ok(receipt).build();
    }
}
