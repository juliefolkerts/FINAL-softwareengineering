package finalproject.com.example.demo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderRequest {

    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}