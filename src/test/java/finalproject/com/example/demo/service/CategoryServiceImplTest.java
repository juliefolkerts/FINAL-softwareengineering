package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.category.CategoryRequest;
import finalproject.com.example.demo.dto.category.CategoryResponse;
import finalproject.com.example.demo.entity.Category;
import finalproject.com.example.demo.mapper.CategoryMapper;
import finalproject.com.example.demo.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Devices and gadgets");

        response = new CategoryResponse();
        response.setId(1L);
        response.setName(category.getName());
        response.setDescription(category.getDescription());
    }

    @Test
    void findAllReturnsMappedCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponse(List.of(category))).thenReturn(List.of(response));

        List<CategoryResponse> results = categoryService.findAll();

        assertThat(results).hasSize(1).containsExactly(response);
        verify(categoryRepository).findAll();
        verify(categoryMapper).toResponse(List.of(category));
    }

    @Test
    void findByIdReturnsMappedCategoryWhenPresent() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        Optional<CategoryResponse> result = categoryService.findById(1L);

        assertTrue(result.isPresent());
        assertThat(result.get().getName()).isEqualTo("Electronics");
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CategoryResponse> result = categoryService.findById(99L);

        assertFalse(result.isPresent());
        verify(categoryRepository).findById(99L);
    }

    @Test
    void createPersistsCategoryAndReturnsResponse() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Home");
        request.setDescription("Home goods");

        Category mapped = new Category();
        mapped.setId(5L);
        mapped.setName("Home");
        mapped.setDescription("Home goods");

        Category saved = new Category();
        saved.setId(10L);
        saved.setName(mapped.getName());
        saved.setDescription(mapped.getDescription());

        CategoryResponse savedResponse = new CategoryResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setName(saved.getName());
        savedResponse.setDescription(saved.getDescription());

        when(categoryMapper.toEntity(request)).thenReturn(mapped);
        when(categoryRepository.save(mapped)).thenReturn(saved);
        when(categoryMapper.toResponse(saved)).thenReturn(savedResponse);

        CategoryResponse result = categoryService.create(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Home");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getName()).isEqualTo("Home");
        verify(categoryMapper).toEntity(request);
        verify(categoryMapper).toResponse(saved);
    }

    @Test
    void updateReturnsEmptyWhenCategoryMissing() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Not found");
        request.setDescription("Missing");

        when(categoryRepository.findById(77L)).thenReturn(Optional.empty());

        Optional<CategoryResponse> result = categoryService.update(77L, request);

        assertThat(result).isEmpty();
        verify(categoryRepository).findById(77L);
    }

    @Test
    void updateChangesPersistedCategory() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated");
        request.setDescription("Updated description");

        Category updated = new Category();
        updated.setId(1L);
        updated.setName(request.getName());
        updated.setDescription(request.getDescription());

        CategoryResponse updatedResponse = new CategoryResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName(request.getName());
        updatedResponse.setDescription(request.getDescription());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(updated);
        when(categoryMapper.toResponse(updated)).thenReturn(updatedResponse);

        Optional<CategoryResponse> result = categoryService.update(1L, request);

        assertTrue(result.isPresent());
        assertThat(result.get().getName()).isEqualTo("Updated");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(updated);
    }

    @Test
    void deleteDelegatesToRepository() {
        categoryService.deleteById(3L);

        verify(categoryRepository).deleteById(3L);
    }
}