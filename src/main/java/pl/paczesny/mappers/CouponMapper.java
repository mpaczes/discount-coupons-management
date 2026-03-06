package pl.paczesny.mappers;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pl.paczesny.model.Coupon;

@Mapper
public interface CouponMapper {
	
    Optional<Coupon> findByCode(@Param("code") String code);

    int incrementUsages(@Param("id") Long id);

    boolean hasUserUsedCoupon(@Param("couponId") Long couponId, @Param("userId") String userId);

    void saveUsage(@Param("couponId") Long couponId, @Param("userId") String userId);

    void insertCoupon(Coupon coupon);

}
