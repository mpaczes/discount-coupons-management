package pl.paczesny.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import pl.paczesny.dtos.CouponCreateRequest;
import pl.paczesny.dtos.CouponUsage;
import pl.paczesny.dtos.CouponUseRequest;
import pl.paczesny.model.Coupon;
import pl.paczesny.services.CouponService;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Kupony", description = "Zarządzanie i realizacja kuponów rabatowych")
public class CouponController {
	
    private final CouponService couponService;
  
    @Operation(summary = "Tworzenie kuponu", description = "Dodaje nowy kupon do systemu.")
    @PostMapping
    public ResponseEntity<Void> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Realizacja kuponu", description = "Sprawdza dostępność i realizuje kupon dla danego użytkownika.")
	@PostMapping("/use")
    public ResponseEntity<String> useCoupon(@Valid @RequestBody CouponUseRequest request, HttpServletRequest httpRequest) {  
        String userIp = getClientIp(httpRequest);
        couponService.useCoupon(request.getCode(), request.getUserId(), userIp);
        return ResponseEntity.ok("Kupon został pomyślnie wykorzystany.");
    }
    
    @Operation(summary = "Wyszukiwanie wszystkich kuponów", description = "Wyszukuje wszystkie kupony w systemie.")
    @GetMapping
    public ResponseEntity<List<Coupon>> findCoupons() {
    	return ResponseEntity.ok(couponService.findCoupons());
    }
    
    @Operation(summary = "Wyszukiwanie użycia kuponu", description = "Wyszukuje wszystkie użycia kuponu w systemie.")
    @GetMapping("/use/{couponCode}")
    public ResponseEntity<List<CouponUsage>> findCouponUsage(@PathVariable String couponCode) {
    	return ResponseEntity.ok(couponService.findCouponUsage(couponCode));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
