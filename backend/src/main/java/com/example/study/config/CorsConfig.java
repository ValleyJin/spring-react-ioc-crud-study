package com.example.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 프론트엔드(Vite dev server, 보통 5173 포트)와
 * 백엔드(Spring Boot, 8080 포트)는 "서로 다른 origin"이다.
 *
 * 브라우저는 보안상 기본적으로 Cross-Origin 요청을 제한한다.
 * 따라서 서버에서 CORS 정책을 명시적으로 허용해야 한다.
 *
 * 이 또한 WebMvcConfigurer Bean을 IoC 컨테이너에 등록하는 방식으로 구성한다.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // 익명 클래스를 반환하여 WebMvcConfigurer의 메서드를 재정의한다.
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
