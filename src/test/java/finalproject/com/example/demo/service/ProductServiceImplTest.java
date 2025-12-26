package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.product.ProductRequest;
import finalproject.com.example.demo.dto.product.ProductResponse;
import finalproject.com.example.demo.entity.Category;
import finalproject.com.example.demo.entity.Product;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.mapper.ProductMapper;
import finalproject.com.example.demo.repository.CategoryRepository;
import finalproject.com.example.demo.repository.ProductRepository;
import finalproject.com.example.demo.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductResponse response;
    private Category category;
    private User seller;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(2L);
        category.setName("Electronics");

        seller = new User();
        seller.setId(3L);
        seller.setEmail("seller@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setDescription("Smartphone");
        product.setPrice(BigDecimal.TEN);
        product.setCategory(category);
        product.setSeller(seller);

        response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
    }

    @Test
    void findAllReturnsMappedProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(List.of(product))).thenReturn(List.of(response));

        List<ProductResponse> results = productService.findAll();

        assertThat(results).containsExactly(response);
        verify(productRepository).findAll();
        verify(productMapper).toResponse(List.of(product));
    }

    @Test
    void findByIdReturnsMappedProductWhenPresent() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        Optional<ProductResponse> result = productService.findById(1L);

        assertTrue(result.isPresent());
        assertThat(result.get().getName()).isEqualTo("Phone");
        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void createResolvesRelationsAndPersistsProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setPrice(BigDecimal.ONE);
        request.setCategoryId(category.getId());
        request.setSellerId(seller.getId());
        request.setStatus("AVAILABLE");

        Product mapped = new Product();
        mapped.setId(9L);
        mapped.setName(request.getName());
        mapped.setDescription(request.getDescription());
        mapped.setPrice(request.getPrice());

        Product saved = new Product();
        saved.setId(5L);
        saved.setName(mapped.getName());
        saved.setDescription(mapped.getDescription());
        saved.setPrice(mapped.getPrice());
        saved.setCategory(category);
        saved.setSeller(seller);

        ProductResponse savedResponse = new ProductResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setName(saved.getName());

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(productMapper.toEntity(request)).thenReturn(mapped);
        when(productRepository.save(mapped)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(savedResponse);

        ProductResponse result = productService.create(request);

        assertThat(result.getId()).isEqualTo(5L);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product persisted = captor.getValue();
        assertThat(persisted.getId()).isNull();
        assertThat(persisted.getCategory()).isEqualTo(category);
        assertThat(persisted.getSeller()).isEqualTo(seller);
        verify(categoryRepository).findById(category.getId());
        verify(userRepository).findById(seller.getId());
        verify(productMapper).toEntity(request);
        verify(productMapper).toResponse(saved);
    }

    @Test
    void createThrowsWhenCategoryMissing() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(99L);
        request.setSellerId(seller.getId());

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @Test
    void updateReturnsEmptyWhenProductMissing() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(category.getId());
        request.setSellerId(seller.getId());

        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        assertThat(productService.update(42L, request)).isEmpty();
    }

    @Test
    void updateChangesProductFields() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated");
        request.setDescription("Updated description");
        request.setPrice(BigDecimal.valueOf(20));
        request.setCategoryId(category.getId());
        request.setSellerId(seller.getId());
        request.setStatus("SOLD");

        Product updated = new Product();
        updated.setId(product.getId());
        updated.setName(request.getName());
        updated.setDescription(request.getDescription());
        updated.setPrice(request.getPrice());
        updated.setCategory(category);
        updated.setSeller(seller);
        updated.setStatus(request.getStatus());

        ProductResponse updatedResponse = new ProductResponse();
        updatedResponse.setId(updated.getId());
        updatedResponse.setName(updated.getName());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(productRepository.save(product)).thenReturn(updated);
        when(productMapper.toResponse(updated)).thenReturn(updatedResponse);

        Optional<ProductResponse> result = productService.update(product.getId(), request);

        assertTrue(result.isPresent());
        assertThat(product.getStatus()).isEqualTo("SOLD");
        assertThat(product.getName()).isEqualTo("Updated");
        verify(productRepository).findById(product.getId());
        verify(productRepository).save(product);
        verify(productMapper).toResponse(updated);
    }

    @Test
    void deleteDelegatesToRepository() {
        productService.deleteById(4L);

        verify(productRepository).deleteById(4L);
    }
}