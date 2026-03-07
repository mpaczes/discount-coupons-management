package pl.paczesny.dtos;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CouponUsage {

	 private final String code;
	 private final LocalDateTime createdAt;
	 private final String countryCode;
	 private final Integer maxUsages;
	 private final Integer currentUsages;
	 private final String userId;
	 private final LocalDateTime usedAt;

}
