package pl.paczesny.dtos;

import lombok.Data;

@Data
public class CouponUseRequest {
 private final String code;
 private final String userId;
}
