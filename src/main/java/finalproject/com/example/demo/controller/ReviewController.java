package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.review.ReviewRequest;
import finalproject.com.example.demo.dto.review.ReviewResponse;
import finalproject.com.example.demo.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Public
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    // Public
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin or User
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<ReviewResponse> create(@RequestBody ReviewRequest request) {
        ReviewResponse created = reviewService.create(request);
        return ResponseEntity.created(URI.create("/reviews/" + created.getId())).body(created);
    }

    // Admin or User
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody ReviewRequest request
    ) {
        return reviewService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin or User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}












//package finalproject.com.example.demo.controller;
//
//import finalproject.com.example.demo.dto.review.ReviewRequest;
//import finalproject.com.example.demo.dto.review.ReviewResponse;
//import finalproject.com.example.demo.service.ReviewService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequestMapping("/reviews")
//public class ReviewController {
//
//    private final ReviewService reviewService;
//
//    public ReviewController(ReviewService reviewService) {
//        this.reviewService = reviewService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<ReviewResponse>> getAll() {
//        return ResponseEntity.ok(reviewService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
//        return reviewService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<?> create(@RequestBody ReviewRequest request) {
//        try {
//            ReviewResponse created = reviewService.create(request);
//            return ResponseEntity.created(URI.create("/reviews/" + created.getId())).body(created);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReviewRequest request) {
//        try {
//            return reviewService.update(id, request)
//                    .map(ResponseEntity::ok)
//                    .orElseGet(() -> ResponseEntity.notFound().build());
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        reviewService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}