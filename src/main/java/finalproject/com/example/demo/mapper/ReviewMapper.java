package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.review.ReviewRequest;
import finalproject.com.example.demo.dto.review.ReviewResponse;
import finalproject.com.example.demo.entity.Product;
import finalproject.com.example.demo.entity.Review;
import finalproject.com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    ReviewResponse toResponse(Review entity);

    List<ReviewResponse> toResponse(List<Review> entities);

    @Mapping(target = "user", expression = "java(toUser(request.getUserId()))")
    @Mapping(target = "product", expression = "java(toProduct(request.getProductId()))")
    Review toEntity(ReviewRequest request);

    List<Review> toEntity(List<ReviewRequest> requests);

    default User toUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
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