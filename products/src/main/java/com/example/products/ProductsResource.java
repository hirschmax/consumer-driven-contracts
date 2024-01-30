package com.example.products;

import com.example.products.model.ProductRequest;
import com.example.products.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class ProductsResource {

    @Inject
    ProductService productService;

    @POST
    @Path("/products/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts(ProductRequest productRequest) {
        return Response.ok(productService.getProducts(productRequest)).build();
    }
}
