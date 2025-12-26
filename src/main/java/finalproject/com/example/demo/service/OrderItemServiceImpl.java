package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.orderitem.OrderItemRequest;
import finalproject.com.example.demo.dto.orderitem.OrderItemResponse;
import finalproject.com.example.demo.entity.Order;
import finalproject.com.example.demo.entity.OrderItem;
import finalproject.com.example.demo.entity.Product;
import finalproject.com.example.demo.mapper.OrderItemMapper;
import finalproject.com.example.demo.repository.OrderItemRepository;
import finalproject.com.example.demo.repository.OrderRepository;
import finalproject.com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<OrderItemResponse> findAll() {
        return orderItemMapper.toResponse(orderItemRepository.findAll());
    }

    @Override
    public Optional<OrderItemResponse> findById(Long id) {
        return orderItemRepository.findById(id).map(orderItemMapper::toResponse);
    }

    @Override
    public OrderItemResponse create(OrderItemRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        OrderItem orderItem = orderItemMapper.toEntity(request);
        orderItem.setId(null);
        orderItem.setOrder(order);
        orderItem.setProduct(product);

        OrderItem saved = orderItemRepository.save(orderItem);
        return orderItemMapper.toResponse(saved);
    }

    @Override
    public Optional<OrderItemResponse> update(Long id, OrderItemRequest request) {
        Optional<OrderItem> existingOpt = orderItemRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        OrderItem existing = existingOpt.get();
        existing.setOrder(order);
        existing.setProduct(product);
        existing.setQuantity(request.getQuantity());
        existing.setPriceAtPurchase(request.getPriceAtPurchase());

        OrderItem updated = orderItemRepository.save(existing);
        return Optional.of(orderItemMapper.toResponse(updated));
    }

    @Override
    public void deleteById(Long id) {
        orderItemRepository.deleteById(id);
    }
}