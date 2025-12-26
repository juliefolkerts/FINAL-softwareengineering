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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponse> findAll() {
        return productMapper.toResponse(productRepository.findAll());
    }

    @Override
    public Optional<ProductResponse> findById(Long id) {
        return productRepository.findById(id).map(productMapper::toResponse);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        Product product = productMapper.toEntity(request);
        product.setId(null);
        product.setCategory(category);
        product.setSeller(seller);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public Optional<ProductResponse> update(Long id, ProductRequest request) {
        Optional<Product> existingOpt = productRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        Product existing = existingOpt.get();
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setCategory(category);
        existing.setSeller(seller);
        existing.setStatus(request.getStatus());

        Product updated = productRepository.save(existing);
        return Optional.of(productMapper.toResponse(updated));
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}