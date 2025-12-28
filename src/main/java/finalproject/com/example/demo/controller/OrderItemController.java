package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.orderitem.OrderItemRequest;
import finalproject.com.example.demo.dto.orderitem.OrderItemResponse;
import finalproject.com.example.demo.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAll() {
        return ResponseEntity.ok(orderItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable Long id) {
        return orderItemService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@RequestBody OrderItemRequest request) {
        System.out.println(">>> OrderItemController.create() CALLED");
        OrderItemResponse created = orderItemService.create(request);
        return ResponseEntity
                .created(URI.create("/order-items/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(
            @PathVariable Long id,
            @RequestBody OrderItemRequest request
    ) {
        return orderItemService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}






//package finalproject.com.example.demo.controller;
//
//import finalproject.com.example.demo.dto.orderitem.OrderItemRequest;
//import finalproject.com.example.demo.dto.orderitem.OrderItemResponse;
//import finalproject.com.example.demo.service.OrderItemService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequestMapping("/order-items")
//public class OrderItemController {
//
//    private final OrderItemService orderItemService;
//
//    public OrderItemController(OrderItemService orderItemService) {
//        this.orderItemService = orderItemService;
//    }
//
//    // ADMIN only
//    @GetMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<List<OrderItemResponse>> getAll() {
//        return ResponseEntity.ok(orderItemService.findAll());
//    }
//
//    // ADMIN only
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<OrderItemResponse> getById(@PathVariable Long id) {
//        return orderItemService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // ADMIN only
////    @PostMapping
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    public ResponseEntity<OrderItemResponse> create(@RequestBody OrderItemRequest request) {
////        OrderItemResponse created = orderItemService.create(request);
////        return ResponseEntity
////                .created(URI.create("/order-items/" + created.getId()))
////                .body(created);
////    }
//    @PostMapping
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<OrderItemResponse> create(@RequestBody OrderItemRequest request) {
//        OrderItemResponse created = orderItemService.create(request);
//        return ResponseEntity
//                .created(URI.create("/order-items/" + created.getId()))
//                .body(created);
//    }
//
//    // ADMIN only
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<OrderItemResponse> update(
//            @PathVariable Long id,
//            @RequestBody OrderItemRequest request
//    ) {
//        return orderItemService.update(id, request)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // ADMIN only
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        orderItemService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
//
//
//




//package finalproject.com.example.demo.controller;
//
//import finalproject.com.example.demo.dto.orderitem.OrderItemRequest;
//import finalproject.com.example.demo.dto.orderitem.OrderItemResponse;
//import finalproject.com.example.demo.service.OrderItemService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequestMapping("/order-items")
//public class OrderItemController {
//
//    private final OrderItemService orderItemService;
//
//    public OrderItemController(OrderItemService orderItemService) {
//        this.orderItemService = orderItemService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<OrderItemResponse>> getAll() {
//        return ResponseEntity.ok(orderItemService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<OrderItemResponse> getById(@PathVariable Long id) {
//        return orderItemService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<?> create(@RequestBody OrderItemRequest request) {
//        try {
//            OrderItemResponse created = orderItemService.create(request);
//            return ResponseEntity.created(URI.create("/order-items/" + created.getId())).body(created);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody OrderItemRequest request) {
//        try {
//            return orderItemService.update(id, request)
//                    .map(ResponseEntity::ok)
//                    .orElseGet(() -> ResponseEntity.notFound().build());
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        orderItemService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}