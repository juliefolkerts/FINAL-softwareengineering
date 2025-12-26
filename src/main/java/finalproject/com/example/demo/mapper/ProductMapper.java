package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.product.ProductRequest;
import finalproject.com.example.demo.dto.product.ProductResponse;
import finalproject.com.example.demo.entity.Category;
import finalproject.com.example.demo.entity.Product;
import finalproject.com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "sellerId", source = "seller.id")
    ProductResponse toResponse(Product entity);

    List<ProductResponse> toResponse(List<Product> entities);

    @Mapping(target = "category", expression = "java(toCategory(request.getCategoryId()))")
    @Mapping(target = "seller", expression = "java(toUser(request.getSellerId()))")
    Product toEntity(ProductRequest request);

    List<Product> toEntity(List<ProductRequest> requests);

    default Category toCategory(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }

    default User toUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}