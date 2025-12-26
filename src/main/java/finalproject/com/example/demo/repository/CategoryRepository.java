package finalproject.com.example.demo.repository;

import finalproject.com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}