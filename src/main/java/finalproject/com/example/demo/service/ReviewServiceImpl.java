package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.review.ReviewRequest;
import finalproject.com.example.demo.dto.review.ReviewResponse;
import finalproject.com.example.demo.entity.Product;
import finalproject.com.example.demo.entity.Review;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.mapper.ReviewMapper;
import finalproject.com.example.demo.repository.ProductRepository;
import finalproject.com.example.demo.repository.ReviewRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewResponse> findAll() {
        return reviewMapper.toResponse(reviewRepository.findAll());
    }

    @Override
    public Optional<ReviewResponse> findById(Long id) {
        return reviewRepository.findById(id).map(reviewMapper::toResponse);
    }

    @Override
    public ReviewResponse create(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Review review = reviewMapper.toEntity(request);
        review.setId(null);
        review.setUser(user);
        review.setProduct(product);

        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Override
    public Optional<ReviewResponse> update(Long id, ReviewRequest request) {
        Optional<Review> existingOpt = reviewRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Review existing = existingOpt.get();
        existing.setUser(user);
        existing.setProduct(product);
        existing.setRating(request.getRating());
        existing.setComment(request.getComment());

        Review updated = reviewRepository.save(existing);
        return Optional.of(reviewMapper.toResponse(updated));
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}