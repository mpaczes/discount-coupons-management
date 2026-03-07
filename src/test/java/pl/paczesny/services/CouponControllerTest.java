package pl.paczesny.services;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.paczesny.controllers.CouponController;
import pl.paczesny.dtos.CouponCreateRequest;
import pl.paczesny.dtos.CouponUseRequest;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @MockBean
    private CouponService couponService;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Powinien zwrócić 400 gdy kod kuponu jest za krótki")
    void shouldReturnBadRequestWhenCodeTooShort() throws Exception {
        CouponCreateRequest request = new CouponCreateRequest("AB", 10, "PL");
        
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Kod kuponu musi mieć od 3 do 20 znaków"));
    }
    
    @Test
    @DisplayName("Powinien zwrócić 400 gdy maxUsages jest ujemne")
    void shouldReturnBadRequestWhenMaxUsagesNegative() throws Exception {
        CouponCreateRequest request = new CouponCreateRequest("PROMO2024", -1, "PL");
        
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maxUsages").value("Maksymalna liczba użyć musi być większa od 0"));
    }
    
    @Test
    @DisplayName("Powinien zwrócić 400 gdy userId jest pusty")
    void shouldReturnBadRequestWhenUserIdEmpty() throws Exception {
        CouponUseRequest request = new CouponUseRequest("PROMO2024", "");
        
        mockMvc.perform(post("/api/coupons/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("ID użytkownika może zawierać tylko litery, cyfry, myślniki i podkreślenia"));
    }

    private String objectToJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

}