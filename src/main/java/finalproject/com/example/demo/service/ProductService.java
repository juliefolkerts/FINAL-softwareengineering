package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.product.ProductRequest;
import finalproject.com.example.demo.dto.product.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductResponse> findAll();



    Optional<ProductResponse> findById(Long id);

    ProductResponse create(ProductRequest request);

    Optional<ProductResponse> update(Long id, ProductRequest request);

    void deleteById(Long id);
}