package finalproject.com.example.demo.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Long sellerId;
    private String status;
}