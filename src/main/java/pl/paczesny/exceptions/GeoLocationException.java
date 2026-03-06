package pl.paczesny.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GeoLocationException extends RuntimeException {
	
	public GeoLocationException(String message) {
		super(message);
	}

}
