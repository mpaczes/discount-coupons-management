package pl.paczesny.services;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.paczesny.exceptions.GeoLocationException;

@Slf4j
@Service
public class GeoLocationService {
    
    private final RestTemplate restTemplate;
    private static final String API_URL = "http://ip-api.com/json/{ip}?fields=status,countryCode";
    private static final String UNKNOWN = "UNKNOWN";

    public GeoLocationService(RestTemplate restTemplate) {
    	this.restTemplate = restTemplate;
    }
    
    public String getCountryCode(String ip) {
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            return "PL";
        }

        try {
            IpApiResponse response = restTemplate.getForObject(API_URL, IpApiResponse.class, ip);
            
            if (response != null && "success".equals(response.getStatus())) {
                return response.getCountryCode();
            }
        } catch (Exception e) {
        	log.warn("Próba pobrania danych geolokalizacyjnych: Nie udało się zweryfikować geolokalizacji dla adresu IP {}", ip);
        	throw new GeoLocationException("Nie udało się zweryfikować geolokalizacji dla adresu IP " + ip);
        }
        
        return UNKNOWN;
    }

    @Data
    private static class IpApiResponse {
        private String status;
        private String countryCode;
    }
	
}
