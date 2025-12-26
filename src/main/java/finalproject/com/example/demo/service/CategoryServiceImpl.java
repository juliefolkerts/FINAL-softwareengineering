package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.category.CategoryRequest;
import finalproject.com.example.demo.dto.category.CategoryResponse;
import finalproject.com.example.demo.entity.Category;
import finalproject.com.example.demo.mapper.CategoryMapper;
import finalproject.com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryMapper.toResponse(categoryRepository.findAll());
    }

    @Override
    public Optional<CategoryResponse> findById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toResponse);
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category.setId(null);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    public Optional<CategoryResponse> update(Long id, CategoryRequest request) {
        Optional<Category> existingOpt = categoryRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        Category existing = existingOpt.get();
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        Category updated = categoryRepository.save(existing);
        return Optional.of(categoryMapper.toResponse(updated));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}