package com.example.order.service;

import com.example.order.model.ProductsRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import com.example.order.model.Product;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Path("/products")
@RegisterRestClient(configKey = "products-api")
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductService {

    @POST
    @Path("/list")
    List<Product> getProducts(ProductsRequest productRequest);


}
