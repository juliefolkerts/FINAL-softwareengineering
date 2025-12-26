package finalproject.com.example.demo.repository;

import finalproject.com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}