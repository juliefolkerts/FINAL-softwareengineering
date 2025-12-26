package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.order.OrderRequest;
import finalproject.com.example.demo.dto.order.OrderResponse;
import finalproject.com.example.demo.entity.Order;
import finalproject.com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "userId", source = "user.id")
    OrderResponse toResponse(Order entity);

    List<OrderResponse> toResponse(List<Order> entities);

    @Mapping(target = "user", expression = "java(toUser(request.getUserId()))")
    Order toEntity(OrderRequest request);

    List<Order> toEntity(List<OrderRequest> requests);

    default User toUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}