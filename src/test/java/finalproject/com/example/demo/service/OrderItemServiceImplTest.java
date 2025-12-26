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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private Order order;
    private Product product;
    private OrderItem orderItem;
    private OrderItemResponse response;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);

        product = new Product();
        product.setId(2L);
        product.setName("Phone");

        orderItem = new OrderItem();
        orderItem.setId(3L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(100));

        response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setQuantity(orderItem.getQuantity());
    }

    @Test
    void findAllReturnsMappedOrderItems() {
        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
        when(orderItemMapper.toResponse(List.of(orderItem))).thenReturn(List.of(response));

        List<OrderItemResponse> results = orderItemService.findAll();

        assertThat(results).containsExactly(response);
        verify(orderItemRepository).findAll();
        verify(orderItemMapper).toResponse(List.of(orderItem));
    }

    @Test
    void findByIdReturnsMappedOrderItemWhenPresent() {
        when(orderItemRepository.findById(3L)).thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toResponse(orderItem)).thenReturn(response);

        Optional<OrderItemResponse> result = orderItemService.findById(3L);

        assertTrue(result.isPresent());
        assertThat(result.get().getQuantity()).isEqualTo(2);
        verify(orderItemRepository).findById(3L);
        verify(orderItemMapper).toResponse(orderItem);
    }

    @Test
    void createResolvesRelationsAndPersistsOrderItem() {
        OrderItemRequest request = new OrderItemRequest();
        request.setOrderId(order.getId());
        request.setProductId(product.getId());
        request.setQuantity(1);
        request.setPriceAtPurchase(BigDecimal.TEN);

        OrderItem mapped = new OrderItem();
        mapped.setId(10L);
        mapped.setQuantity(request.getQuantity());
        mapped.setPriceAtPurchase(request.getPriceAtPurchase());

        OrderItem saved = new OrderItem();
        saved.setId(7L);
        saved.setOrder(order);
        saved.setProduct(product);
        saved.setQuantity(request.getQuantity());
        saved.setPriceAtPurchase(request.getPriceAtPurchase());

        OrderItemResponse savedResponse = new OrderItemResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setQuantity(saved.getQuantity());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderItemMapper.toEntity(request)).thenReturn(mapped);
        when(orderItemRepository.save(mapped)).thenReturn(saved);
        when(orderItemMapper.toResponse(saved)).thenReturn(savedResponse);

        OrderItemResponse result = orderItemService.create(request);

        assertThat(result.getId()).isEqualTo(7L);
        ArgumentCaptor<OrderItem> captor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(captor.capture());
        OrderItem persisted = captor.getValue();
        assertThat(persisted.getOrder()).isEqualTo(order);
        assertThat(persisted.getProduct()).isEqualTo(product);
        verify(orderRepository).findById(order.getId());
        verify(productRepository).findById(product.getId());
        verify(orderItemMapper).toEntity(request);
        verify(orderItemMapper).toResponse(saved);
    }

    @Test
    void updateReturnsEmptyWhenOrderItemMissing() {
        OrderItemRequest request = new OrderItemRequest();
        request.setOrderId(order.getId());
        request.setProductId(product.getId());

        when(orderItemRepository.findById(50L)).thenReturn(Optional.empty());

        assertThat(orderItemService.update(50L, request)).isEmpty();
    }

    @Test
    void updateModifiesExistingOrderItem() {
        OrderItemRequest request = new OrderItemRequest();
        request.setOrderId(order.getId());
        request.setProductId(product.getId());
        request.setQuantity(5);
        request.setPriceAtPurchase(BigDecimal.valueOf(200));

        OrderItem updated = new OrderItem();
        updated.setId(orderItem.getId());
        updated.setOrder(order);
        updated.setProduct(product);
        updated.setQuantity(request.getQuantity());
        updated.setPriceAtPurchase(request.getPriceAtPurchase());

        OrderItemResponse updatedResponse = new OrderItemResponse();
        updatedResponse.setId(updated.getId());
        updatedResponse.setQuantity(updated.getQuantity());

        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderItemRepository.save(orderItem)).thenReturn(updated);
        when(orderItemMapper.toResponse(updated)).thenReturn(updatedResponse);

        Optional<OrderItemResponse> result = orderItemService.update(orderItem.getId(), request);

        assertTrue(result.isPresent());
        assertThat(orderItem.getQuantity()).isEqualTo(5);
        assertThat(orderItem.getPriceAtPurchase()).isEqualTo(BigDecimal.valueOf(200));
        verify(orderItemRepository).save(orderItem);
        verify(orderItemMapper).toResponse(updated);
    }

    @Test
    void deleteDelegatesToRepository() {
        orderItemService.deleteById(6L);

        verify(orderItemRepository).deleteById(6L);
    }
}