package pl.paczesny.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.paczesny.exceptions.*;
import pl.paczesny.mappers.CouponMapper;
import pl.paczesny.model.Coupon;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponMapper couponMapper;

    @Mock
    private GeoLocationService geoService;

    @InjectMocks
    private CouponService couponService;

    private Coupon validCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = Coupon.builder()
                .id(1L)
                .code("PROMO2024")
                .countryCode("PL")
                .maxUsages(10)
                .currentUsages(0)
                .build();
    }

    @Test
    @DisplayName("Powinien rzucić CouponNotFoundException, gdy kod nie istnieje")
    void shouldThrowNotFoundWhenCouponDoesNotExist() {
        when(couponMapper.findByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> 
            couponService.useCoupon("NIEISTNIEJE", "user1", "127.0.0.1"));
    }

    @Test
    @DisplayName("Powinien rzucić InvalidCountryException, gdy kraj się nie zgadza")
    void shouldThrowInvalidCountryWhenIpFromOtherCountry() {
        when(couponMapper.findByCode("PROMO2024")).thenReturn(Optional.of(validCoupon));
        when(geoService.getCountryCode(anyString())).thenReturn("DE");

        assertThrows(InvalidCountryException.class, () -> 
            couponService.useCoupon("PROMO2024", "user1", "1.2.3.4"));
    }

    @Test
    @DisplayName("Powinien rzucić AlreadyUsedException, gdy użytkownik już raz go użył")
    void shouldThrowAlreadyUsedWhenUserRedeemedBefore() {
        when(couponMapper.findByCode("PROMO2024")).thenReturn(Optional.of(validCoupon));
        when(geoService.getCountryCode(anyString())).thenReturn("PL");
        when(couponMapper.hasUserUsedCoupon(anyLong(), anyString())).thenReturn(true);

        assertThrows(AlreadyUsedException.class, () -> 
            couponService.useCoupon("PROMO2024", "user1", "127.0.0.1"));
    }

    @Test
    @DisplayName("Powinien rzucić CouponLimitExceededException, gdy brak wolnych użyć")
    void shouldThrowLimitExceededWhenNoMoreUsages() {
        when(couponMapper.findByCode("PROMO2024")).thenReturn(Optional.of(validCoupon));
        when(geoService.getCountryCode(anyString())).thenReturn("PL");
        when(couponMapper.hasUserUsedCoupon(anyLong(), anyString())).thenReturn(false);

        when(couponMapper.incrementUsages(anyLong())).thenReturn(0);

        assertThrows(CouponLimitExceededException.class, () -> 
            couponService.useCoupon("PROMO2024", "user1", "127.0.0.1"));
    }

    @Test
    @DisplayName("Powinien pomyślnie przejść cały proces, gdy dane są poprawne")
    void shouldRedeemSuccessfully() {
        when(couponMapper.findByCode("PROMO2024")).thenReturn(Optional.of(validCoupon));
        when(geoService.getCountryCode(anyString())).thenReturn("PL");
        when(couponMapper.hasUserUsedCoupon(anyLong(), anyString())).thenReturn(false);
        when(couponMapper.incrementUsages(anyLong())).thenReturn(1);

        couponService.useCoupon("PROMO2024", "user1", "127.0.0.1");

        verify(couponMapper, times(1)).saveUsage(anyLong(), eq("user1"));
    }
}
