package pl.paczesny.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CouponUseRequest {
	
    @NotBlank(message = "Kod kuponu nie może być pusty")
    @Size(min = 3, max = 20, message = "Kod kuponu musi mieć od 3 do 20 znaków")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Kod kuponu może zawierać litery, cyfry i podkreślenia")
	private final String code;
 
    @NotBlank(message = "ID użytkownika nie może być puste")
    @Size(min = 1, max = 50, message = "ID użytkownika musi mieć od 1 do 50 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "ID użytkownika może zawierać tylko litery, cyfry, myślniki i podkreślenia")
	private final String userId;
 
}
