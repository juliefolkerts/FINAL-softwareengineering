package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.category.CategoryRequest;
import finalproject.com.example.demo.dto.category.CategoryResponse;
import finalproject.com.example.demo.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryResponse toResponse(Category entity);
    List<CategoryResponse> toResponse(List<Category> entities);

    Category toEntity(CategoryRequest request);
    List<Category> toEntity(List<CategoryRequest> requests);
}
