package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.order.OrderRequest;
import finalproject.com.example.demo.dto.order.OrderResponse;
import finalproject.com.example.demo.entity.Order;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.mapper.OrderMapper;
import finalproject.com.example.demo.repository.OrderRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderResponse> findAll() {
        return orderMapper.toResponse(orderRepository.findAll());
    }

    @Override
    public Optional<OrderResponse> findById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse create(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = orderMapper.toEntity(request);
        order.setId(null);
        order.setUser(user);
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    public Optional<OrderResponse> update(Long id, OrderRequest request) {
        Optional<Order> existingOpt = orderRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order existing = existingOpt.get();
        existing.setUser(user);
        existing.setStatus(request.getStatus());
        existing.setTotalPrice(request.getTotalPrice());
        existing.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : existing.getCreatedAt());

        Order updated = orderRepository.save(existing);
        return Optional.of(orderMapper.toResponse(updated));
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}