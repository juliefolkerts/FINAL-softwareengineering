package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.category.CategoryRequest;
import finalproject.com.example.demo.dto.category.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<CategoryResponse> findAll();

    Optional<CategoryResponse> findById(Long id);

    CategoryResponse create(CategoryRequest request);

    Optional<CategoryResponse> update(Long id, CategoryRequest request);

    void deleteById(Long id);
}