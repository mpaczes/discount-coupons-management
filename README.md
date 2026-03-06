# System zarządzania kuponami rabatowymi

REST API do zarządzania i realizacji kuponów rabatowych z ograniczeniami geograficznymi i liczbowymi.

## Technologie

- **Spring Boot 3.2.3** - framework aplikacji
- **Java 21** - wersja językowa
- **MyBatis 3.0.3** - mapowanie obiektowo-relacyjne
- **H2 Database** - baza danych w pamięci (dla celów deweloperskich)
- **Swagger UI** - dokumentacja API
- **Lombok** - generowanie boilerplate code

## Architektura

System zbudowany jest zgodnie z zasadami REST API, wykorzystując następujące warstwy:

- **Controllers** - endpointy HTTP
- **Services** - logika biznesowa
- **Mappers** - mapowanie MyBatis
- **Model** - encje danych
- **Exceptions** - obsługa błędów

## Funkcje

### 1. Tworzenie kuponów
- Unikalny kod kuponu (wielkość liter nie ma znaczenia)
- Maksymalna liczba użyć
- Ograniczenie geograficzne (kod kraju)

### 2. Realizacja kuponów
- Sprawdzanie dostępności kuponu
- Walidacja kraju na podstawie adresu IP użytkownika
- Ograniczenie: 1 użytkownik = 1 użycie kuponu
- Blokada po osiągnięciu limitu użyć

### 3. Walidacja geograficzna
- Automatyczne określanie kraju użytkownika na podstawie IP
- Integracja z zewnętrznym serwisem geolokalizacyjnym (ip-api.com)
- Obsługa błędów geolokalizacji

## Baza danych

### Tabela `coupons`
```sql
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    max_usages INT NOT NULL,
    current_usages INT NOT NULL DEFAULT 0,
    country_code VARCHAR(3) NOT NULL
);
```

### Tabela `coupon_usages`
```sql
CREATE TABLE coupon_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id BIGINT NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    used_at TIMESTAMP NOT NULL,
    
    CONSTRAINT uq_coupon_user UNIQUE (coupon_id, user_id),
    CONSTRAINT fk_coupon_usage FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);
```

## Endpointy API

### POST /api/coupons
Tworzy nowy kupon.

**Przykład żądania:**
```json
{
    "code": "SUMMER2024",
    "maxUsages": 100,
    "countryCode": "POL"
}
```

### POST /api/coupons/use
Realizuje kupon dla danego użytkownika.

**Przykład żądania:**
```json
{
    "code": "SUMMER2024",
    "userId": "user123"
}
```

## Obsługa błędów

System obsługuje następujące przypadki błędów:

- **404 Not Found** - kupon nie istnieje
- **409 Conflict** - kupon został już wykorzystany przez użytkownika
- **403 Unprocessable Entity** - kupon nie jest dostępny w kraju użytkownika
- **400 Conflict** - kupon o podanym kodzie już istnieje

## Wymagania systemowe

- Java 21+
- Maven 3.6+
- Dostęp do internetu (dla serwisu geolokalizacji)

## Uruchomienie

1. **Budowanie projektu:**
   ```
   mvn clean package
   ```

2. **Uruchomienie aplikacji:**
   ```
   java -jar target/coupon-service-0.0.1-SNAPSHOT.jar
   ```

3. **Dostęp do aplikacji:**
   - Aplikacja: http://localhost:8080
   - Konsola H2: http://localhost:8080/h2-console
   - Swagger UI: http://localhost:8080/swagger-ui/index.html

## Konfiguracja

Podstawowa konfiguracja znajduje się w pliku `src/main/resources/application.properties`:

```properties
# Baza danych H2
spring.datasource.url=jdbc:h2:mem:coupondb
spring.h2.console.enabled=true

# MyBatis
mybatis.mapper-locations=classpath:mappers/*.xml
mybatis.configuration.map-underscore-to-camel-case=true

# Swagger
# Dostęp po uruchomieniu: http://localhost:8080/swagger-ui/index.html
```

## Testowanie

Testy jednostkowe znajdują się w katalogu `src/test/java/pl/paczesny/services/`.

Uruchomienie testów:
```
mvn test
```

