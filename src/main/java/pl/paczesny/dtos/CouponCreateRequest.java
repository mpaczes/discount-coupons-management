package pl.paczesny.dtos;

import lombok.Data;

@Data
public class CouponCreateRequest {
 private final String code;
 private final Integer maxUsages;
 private final String countryCode;
}
