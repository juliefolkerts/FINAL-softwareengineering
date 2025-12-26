package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.order.OrderRequest;
import finalproject.com.example.demo.dto.order.OrderResponse;
import finalproject.com.example.demo.entity.Order;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.mapper.OrderMapper;
import finalproject.com.example.demo.repository.OrderRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;
    private OrderResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("buyer@example.com");

        order = new Order();
        order.setId(2L);
        order.setUser(user);
        order.setTotalPrice(BigDecimal.TEN);
        order.setStatus("NEW");
        order.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

        response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
    }

    @Test
    void findAllReturnsMappedOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponse(List.of(order))).thenReturn(List.of(response));

        List<OrderResponse> results = orderService.findAll();

        assertThat(results).containsExactly(response);
        verify(orderRepository).findAll();
        verify(orderMapper).toResponse(List.of(order));
    }

    @Test
    void findByIdReturnsMappedOrderWhenPresent() {
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(response);

        Optional<OrderResponse> result = orderService.findById(2L);

        assertTrue(result.isPresent());
        assertThat(result.get().getStatus()).isEqualTo("NEW");
        verify(orderRepository).findById(2L);
        verify(orderMapper).toResponse(order);
    }

    @Test
    void createSetsDefaultsAndPersistsOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());
        request.setStatus("NEW");
        request.setTotalPrice(BigDecimal.valueOf(50));

        Order mapped = new Order();
        mapped.setStatus(request.getStatus());
        mapped.setTotalPrice(request.getTotalPrice());

        Order saved = new Order();
        saved.setId(5L);
        saved.setUser(user);
        saved.setStatus(request.getStatus());
        saved.setTotalPrice(request.getTotalPrice());
        saved.setCreatedAt(LocalDateTime.of(2024, 2, 2, 10, 0));

        OrderResponse savedResponse = new OrderResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setStatus(saved.getStatus());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(orderMapper.toEntity(request)).thenReturn(mapped);
        when(orderRepository.save(mapped)).thenReturn(saved);
        when(orderMapper.toResponse(saved)).thenReturn(savedResponse);

        OrderResponse result = orderService.create(request);

        assertThat(result.getId()).isEqualTo(5L);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order persisted = captor.getValue();
        assertThat(persisted.getUser()).isEqualTo(user);
        assertThat(persisted.getCreatedAt()).isNotNull();
        verify(orderMapper).toEntity(request);
        verify(orderMapper).toResponse(saved);
    }

    @Test
    void updateReturnsEmptyWhenOrderMissing() {
        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());

        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThat(orderService.update(100L, request)).isEmpty();
    }

    @Test
    void updateAppliesChangesAndPersists() {
        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());
        request.setStatus("PAID");
        request.setTotalPrice(BigDecimal.valueOf(75));
        request.setCreatedAt(LocalDateTime.of(2024, 3, 3, 12, 0));

        Order updated = new Order();
        updated.setId(order.getId());
        updated.setUser(user);
        updated.setStatus(request.getStatus());
        updated.setTotalPrice(request.getTotalPrice());
        updated.setCreatedAt(request.getCreatedAt());

        OrderResponse updatedResponse = new OrderResponse();
        updatedResponse.setId(updated.getId());
        updatedResponse.setStatus(updated.getStatus());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(orderRepository.save(order)).thenReturn(updated);
        when(orderMapper.toResponse(updated)).thenReturn(updatedResponse);

        Optional<OrderResponse> result = orderService.update(order.getId(), request);

        assertTrue(result.isPresent());
        assertThat(order.getStatus()).isEqualTo("PAID");
        assertThat(order.getCreatedAt()).isEqualTo(request.getCreatedAt());
        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(order);
        verify(orderMapper).toResponse(updated);
    }

    @Test
    void deleteDelegatesToRepository() {
        orderService.deleteById(9L);

        verify(orderRepository).deleteById(9L);
    }
}