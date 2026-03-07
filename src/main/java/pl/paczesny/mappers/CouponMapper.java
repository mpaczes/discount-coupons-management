package pl.paczesny.mappers;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pl.paczesny.dtos.CouponUsage;
import pl.paczesny.model.Coupon;

@Mapper
public interface CouponMapper {
	
    Optional<Coupon> findByCode(@Param("code") String code);
    List<Coupon> findCoupons();
    void insertCoupon(Coupon coupon);
    boolean hasUserUsedCoupon(@Param("couponId") Long couponId, @Param("userId") String userId);
    
    void saveUsage(@Param("couponId") Long couponId, @Param("userId") String userId);
    int incrementUsages(@Param("id") Long id);
    List<CouponUsage> findCouponUsage(@Param("couponCode") String couponCode);
    
}
