package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.category.CategoryRequest;
import finalproject.com.example.demo.dto.category.CategoryResponse;
import finalproject.com.example.demo.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        return ResponseEntity.created(URI.create("/categories/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}





//
//
//
//
// package finalproject.com.example.demo.controller;
//
//import finalproject.com.example.demo.dto.category.CategoryRequest;
//import finalproject.com.example.demo.dto.category.CategoryResponse;
//import finalproject.com.example.demo.service.CategoryService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequestMapping("/categories")
//public class CategoryController {
//
//    private final CategoryService categoryService;
//
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<CategoryResponse>> getAll() {
//        return ResponseEntity.ok(categoryService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
//        return categoryService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
//        CategoryResponse created = categoryService.create(request);
//        return ResponseEntity.created(URI.create("/categories/" + created.getId())).body(created);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
//        return categoryService.update(id, request)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        categoryService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}