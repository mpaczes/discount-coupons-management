package pl.paczesny.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@MapperScan("pl.paczesny.mappers")
public class MyBatisConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
