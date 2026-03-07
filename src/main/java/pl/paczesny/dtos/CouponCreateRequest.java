package pl.paczesny.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CouponCreateRequest {

	@NotBlank(message = "Kod kuponu nie może być pusty")
	@Size(min = 3, max = 20, message = "Kod kuponu musi mieć od 3 do 20 znaków")
	@Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Kod kuponu może zawierać litery, cyfry i podkreślenia")
	private final String code;
 
    @NotNull(message = "Maksymalna liczba użycień nie może być pusta")
    @Min(value = 1, message = "Maksymalna liczba użyć musi być większa od 0")
    @Max(value = 100, message = "Maksymalna liczba użyć nie może przekraczać 100")
	private final Integer maxUsages;
 
    @NotBlank(message = "Kod kraju nie może być pusty")
    @Size(min = 2, max = 3, message = "Kod kraju musi mieć 2-3 znaki")
    @Pattern(regexp = "^[A-Za-z]{2,3}$", message = "Kod kraju może zawierać małe i wielkie litery")
	private final String countryCode;
	
}
