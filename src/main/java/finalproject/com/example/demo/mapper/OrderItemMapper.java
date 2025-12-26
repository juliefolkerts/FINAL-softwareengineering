package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.orderitem.OrderItemRequest;
import finalproject.com.example.demo.dto.orderitem.OrderItemResponse;
import finalproject.com.example.demo.entity.Order;
import finalproject.com.example.demo.entity.OrderItem;
import finalproject.com.example.demo.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productId", source = "product.id")
    OrderItemResponse toResponse(OrderItem entity);

    List<OrderItemResponse> toResponse(List<OrderItem> entities);

    @Mapping(target = "order", expression = "java(toOrder(request.getOrderId()))")
    @Mapping(target = "product", expression = "java(toProduct(request.getProductId()))")
    OrderItem toEntity(OrderItemRequest request);

    List<OrderItem> toEntity(List<OrderItemRequest> requests);

    default Order toOrder(Long id) {
        if (id == null) {
            return null;
        }
        Order order = new Order();
        order.setId(id);
        return order;
    }

    default Product toProduct(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }
}