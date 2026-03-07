package pl.paczesny.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

	/**
	 * Wyjątek, który zostanie zgłoszony, gdy walidacja argumentu oznaczonego adnotacją @Valid zakończy się niepowodzeniem.
	 * @param ex wyjątek typu MethodArgumentNotValidException
	 * @return mapa gdzie kluczem jest nazwa obiektu walidowanego, a wartością wiadomość walidacji
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
            
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }
	
	/**
	 * Walidacja konkretnych pól obiektu.
	 * @param ex wyjątek typu ConstraintViolationException
	 * @return mapa gdzie kluczem jest ścieżka do pola w obiekcie walidowanym, a wartością wiadomość walidacji
	 */
	@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(
            ConstraintViolationException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> 
            errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
            
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }
	
}
