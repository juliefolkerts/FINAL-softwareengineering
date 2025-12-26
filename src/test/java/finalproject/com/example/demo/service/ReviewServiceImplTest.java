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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private ReviewResponse response;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(2L);

        review = new Review();
        review.setId(3L);
        review.setRating(5);
        review.setComment("Great product");
        review.setUser(user);
        review.setProduct(product);

        response = new ReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
    }

    @Test
    void findAllReturnsMappedReviews() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));
        when(reviewMapper.toResponse(List.of(review))).thenReturn(List.of(response));

        List<ReviewResponse> results = reviewService.findAll();

        assertThat(results).containsExactly(response);
        verify(reviewRepository).findAll();
        verify(reviewMapper).toResponse(List.of(review));
    }

    @Test
    void findByIdReturnsMappedReviewWhenPresent() {
        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        Optional<ReviewResponse> result = reviewService.findById(3L);

        assertTrue(result.isPresent());
        assertThat(result.get().getRating()).isEqualTo(5);
        verify(reviewRepository).findById(3L);
        verify(reviewMapper).toResponse(review);
    }

    @Test
    void createResolvesRelationsAndPersistsReview() {
        ReviewRequest request = new ReviewRequest();
        request.setUserId(user.getId());
        request.setProductId(product.getId());
        request.setRating(4);
        request.setComment("Nice");

        Review mapped = new Review();
        mapped.setId(8L);
        mapped.setRating(request.getRating());
        mapped.setComment(request.getComment());

        Review saved = new Review();
        saved.setId(6L);
        saved.setUser(user);
        saved.setProduct(product);
        saved.setRating(request.getRating());
        saved.setComment(request.getComment());

        ReviewResponse savedResponse = new ReviewResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setRating(saved.getRating());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(reviewMapper.toEntity(request)).thenReturn(mapped);
        when(reviewRepository.save(mapped)).thenReturn(saved);
        when(reviewMapper.toResponse(saved)).thenReturn(savedResponse);

        ReviewResponse result = reviewService.create(request);

        assertThat(result.getId()).isEqualTo(6L);
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        Review persisted = captor.getValue();
        assertThat(persisted.getUser()).isEqualTo(user);
        assertThat(persisted.getProduct()).isEqualTo(product);
        verify(reviewMapper).toEntity(request);
        verify(reviewMapper).toResponse(saved);
    }

    @Test
    void updateReturnsEmptyWhenReviewMissing() {
        ReviewRequest request = new ReviewRequest();
        request.setUserId(user.getId());
        request.setProductId(product.getId());

        when(reviewRepository.findById(12L)).thenReturn(Optional.empty());

        assertThat(reviewService.update(12L, request)).isEmpty();
    }

    @Test
    void updateModifiesExistingReview() {
        ReviewRequest request = new ReviewRequest();
        request.setUserId(user.getId());
        request.setProductId(product.getId());
        request.setRating(3);
        request.setComment("Ok");

        Review updated = new Review();
        updated.setId(review.getId());
        updated.setUser(user);
        updated.setProduct(product);
        updated.setRating(request.getRating());
        updated.setComment(request.getComment());

        ReviewResponse updatedResponse = new ReviewResponse();
        updatedResponse.setId(updated.getId());
        updatedResponse.setRating(updated.getRating());

        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(reviewRepository.save(review)).thenReturn(updated);
        when(reviewMapper.toResponse(updated)).thenReturn(updatedResponse);

        Optional<ReviewResponse> result = reviewService.update(review.getId(), request);

        assertTrue(result.isPresent());
        assertThat(review.getRating()).isEqualTo(3);
        assertThat(review.getComment()).isEqualTo("Ok");
        verify(reviewRepository).save(review);
        verify(reviewMapper).toResponse(updated);
    }

    @Test
    void deleteDelegatesToRepository() {
        reviewService.deleteById(11L);

        verify(reviewRepository).deleteById(11L);
    }
}