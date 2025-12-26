package finalproject.com.example.demo.dto.orderitem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {

    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}