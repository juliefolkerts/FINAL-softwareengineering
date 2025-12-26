package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.orderitem.OrderItemRequest;
import finalproject.com.example.demo.dto.orderitem.OrderItemResponse;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    List<OrderItemResponse> findAll();

    Optional<OrderItemResponse> findById(Long id);

    OrderItemResponse create(OrderItemRequest request);

    Optional<OrderItemResponse> update(Long id, OrderItemRequest request);

    void deleteById(Long id);
}