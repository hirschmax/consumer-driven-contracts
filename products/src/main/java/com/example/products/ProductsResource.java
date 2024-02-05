package com.example.products;

import com.example.products.model.Product;
import com.example.products.model.ProductRequest;
import com.example.products.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api")
public class ProductsResource {

    @Inject
    ProductService productService;

    @POST
    @Path("/products")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<Product>> getProducts(ProductRequest productRequest) {
        return RestResponse.ResponseBuilder.ok(productService.getProducts(productRequest)).build();
    }
}
