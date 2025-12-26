package finalproject.com.example.demo.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponse {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer rating;
    private String comment;
}