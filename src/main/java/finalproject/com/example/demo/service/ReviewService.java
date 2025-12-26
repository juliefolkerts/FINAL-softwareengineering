package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.review.ReviewRequest;
import finalproject.com.example.demo.dto.review.ReviewResponse;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    List<ReviewResponse> findAll();

    Optional<ReviewResponse> findById(Long id);

    ReviewResponse create(ReviewRequest request);

    Optional<ReviewResponse> update(Long id, ReviewRequest request);

    void deleteById(Long id);
}