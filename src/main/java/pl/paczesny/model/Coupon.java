package pl.paczesny.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    
    private Long id;
    private String code;
    private LocalDateTime createdAt;
    private Integer maxUsages;
    private Integer currentUsages;
    private String countryCode;

    public boolean isExpired() {
        return currentUsages >= maxUsages;
    }

}
