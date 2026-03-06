-- Tabela główna kuponów
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    max_usages INT NOT NULL,
    current_usages INT NOT NULL DEFAULT 0,
    country_code VARCHAR(3) NOT NULL
);

-- Unikalny indeks dla kodu
CREATE UNIQUE INDEX idx_coupon_code ON coupons(code);

-- Tabela historii użyć kuponów (dla wymogu: 1 użytkownik = 1 użycie)
CREATE TABLE coupon_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id BIGINT NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    used_at TIMESTAMP NOT NULL,
    
    -- Klucz unikalny na parze kupon-użytkownik (blokada ponownego użycia na poziomie bazy)
    CONSTRAINT uq_coupon_user UNIQUE (coupon_id, user_id),
    
    -- Powiązanie z tabelą kuponów
    CONSTRAINT fk_coupon_usage FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);
