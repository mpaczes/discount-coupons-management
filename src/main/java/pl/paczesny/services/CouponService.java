package pl.paczesny.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paczesny.dtos.CouponCreateRequest;
import pl.paczesny.dtos.CouponUsage;
import pl.paczesny.exceptions.*;
import pl.paczesny.mappers.CouponMapper;
import pl.paczesny.model.Coupon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponMapper couponMapper;
    private final GeoLocationService geoService;

    @Transactional
    public void useCoupon(String code, String userId, String userIp) {
    	log.info("Próba użycia kuponu: {} przez użytkownika: {} (IP: {})", code, userId, userIp);
        
    	String normalizedCode = code.trim().toUpperCase();
        Coupon coupon = couponMapper.findByCode(normalizedCode)
        		.orElseThrow(() -> {
                    log.warn("Nieudana próba: Kupon {} nie istnieje", normalizedCode);
                    return new CouponNotFoundException("Kupon nie istnieje: " + normalizedCode);
                });
        
        String userCountry = geoService.getCountryCode(userIp);
        if (!coupon.getCountryCode().equalsIgnoreCase(userCountry)) {
        	log.warn("Błąd kraju: Kupon {} przypisany do {}, użytkownik z {}", normalizedCode, coupon.getCountryCode(), userCountry);
            throw new InvalidCountryException("Kupon nie jest dostępny w Twoim kraju (" + userCountry + ")");
        }
        
        if (couponMapper.hasUserUsedCoupon(coupon.getId(), userId)) {
        	log.warn("Kupon wykorzystany: Użytkownik {} wykorzystał kupon {}", userId, coupon.getCode());
            throw new AlreadyUsedException("Użytkownik " + userId + " już wykorzystał ten kupon");
        }

        int updatedRows = couponMapper.incrementUsages(coupon.getId());
        if (updatedRows == 0) {
        	log.error("Limit wykorzystania kuponu {} został osiągnięty", normalizedCode);
            throw new CouponLimitExceededException("Kupon osiągnął maksymalną liczbę użyć");
        }

        log.debug("Zapisywanie historii użycia kuponu {} w bazie danych: {}", coupon.getCode());
        couponMapper.saveUsage(coupon.getId(), userId);
    }

    @Transactional
    public void createCoupon(CouponCreateRequest request) {
    	log.info("Tworzenie nowego kuponu: {}", request.getCode());
    	
        String normalizedCode = request.getCode().trim().toUpperCase();
        
        if (couponMapper.findByCode(normalizedCode).isPresent()) {
        	log.warn("Taki kupon {} istieje w bazie danych", normalizedCode);
            throw new CouponAlreadyExistsException("Kod kuponu " + normalizedCode + " jest już zajęty");
        }

        Coupon newCoupon = Coupon.builder()
                .code(normalizedCode)
                .maxUsages(request.getMaxUsages())
                .currentUsages(0)
                .countryCode(request.getCountryCode().toUpperCase())
                .createdAt(LocalDateTime.now())
                .build();

        log.debug("Zapisywanie kuponu w bazie danych: {}", request);
        couponMapper.insertCoupon(newCoupon);
    }
    
    public List<Coupon> findCoupons() {
    	log.info("Wyszukiwanie wszystkich kuponów");
    	try {
    		return couponMapper.findCoupons();
    	} catch (Exception ex) {
    		log.warn("Wyszukiwanie kuponów nie powiodło się");
    		return new ArrayList<>();
    	}
    }
    
    public List<CouponUsage> findCouponUsage(String couponCode) {
    	log.info("Wyszukiwanie użycia kuponu {}", couponCode);
    	try {
    		return couponMapper.findCouponUsage(couponCode);
    	} catch (Exception ex) {
    		log.warn("Wyszukiwanie użycia kuponu {} nie powiodło się", couponCode);
    		return new ArrayList<>();
    	}
    }
    
}
