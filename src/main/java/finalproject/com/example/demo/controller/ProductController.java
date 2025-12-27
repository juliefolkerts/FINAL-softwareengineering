package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.product.ProductRequest;
import finalproject.com.example.demo.dto.product.ProductResponse;
import finalproject.com.example.demo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Public
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    // Public
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin or Seller
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/products/" + created.getId())).body(created);
    }

    // Admin or Seller
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @RequestBody ProductRequest request
    ) {
        return productService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin or Seller
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}











// package finalproject.com.example.demo.controller;
//
//import finalproject.com.example.demo.dto.product.ProductRequest;
//import finalproject.com.example.demo.dto.product.ProductResponse;
//import finalproject.com.example.demo.service.ProductService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequestMapping("/products")
//public class ProductController {
//
//    private final ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @GetMapping
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<List<ProductResponse>> getAll() {
//        return ResponseEntity.ok(productService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
//        return productService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
//    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
//        try {
//            ProductResponse created = productService.create(request);
//            return ResponseEntity.created(URI.create("/products/" + created.getId())).body(created);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
//    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductRequest request) {
//        try {
//            return productService.update(id, request)
//                    .map(ResponseEntity::ok)
//                    .orElseGet(() -> ResponseEntity.notFound().build());
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        productService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
//
//
////package finalproject.com.example.demo.controller;
////
////import finalproject.com.example.demo.dto.product.ProductRequest;
////import finalproject.com.example.demo.dto.product.ProductResponse;
////import finalproject.com.example.demo.service.ProductService;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.net.URI;
////import java.util.List;
////
////@RestController
////@RequestMapping("/products")
////public class ProductController {
////
////    private final ProductService productService;
////
////    public ProductController(ProductService productService) {
////        this.productService = productService;
////    }
////
////    @GetMapping
////    public ResponseEntity<List<ProductResponse>> getAll() {
////        return ResponseEntity.ok(productService.findAll());
////    }
////
////    @GetMapping("/{id}")
////    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
////        return productService.findById(id)
////                .map(ResponseEntity::ok)
////                .orElseGet(() -> ResponseEntity.notFound().build());
////    }
////
////    @PostMapping
////    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
////        try {
////            ProductResponse created = productService.create(request);
////            return ResponseEntity.created(URI.create("/products/" + created.getId())).body(created);
////        } catch (IllegalArgumentException ex) {
////            return ResponseEntity.badRequest().body(ex.getMessage());
////        }
////    }
////
////    @PutMapping("/{id}")
////    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductRequest request) {
////        try {
////            return productService.update(id, request)
////                    .map(ResponseEntity::ok)
////                    .orElseGet(() -> ResponseEntity.notFound().build());
////        } catch (IllegalArgumentException ex) {
////            return ResponseEntity.badRequest().body(ex.getMessage());
////        }
////    }
////
////    @DeleteMapping("/{id}")
////    public ResponseEntity<Void> delete(@PathVariable Long id) {
////        productService.deleteById(id);
////        return ResponseEntity.noContent().build();
////    }
////}