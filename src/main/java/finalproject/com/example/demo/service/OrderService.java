package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.order.OrderRequest;
import finalproject.com.example.demo.dto.order.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderResponse> findAll();

    Optional<OrderResponse> findById(Long id);

    OrderResponse create(OrderRequest request);

    Optional<OrderResponse> update(Long id, OrderRequest request);

    void deleteById(Long id);
}