package com.example.study.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Configuration 클래스는 "Bean 정의서" 역할을 한다.
 *
 * 학습 포인트:
 * 1. @Service, @Repository처럼 클래스에 직접 붙여서 Bean 등록하는 방식도 있지만,
 * 2. 이렇게 @Bean 메서드로 "반환값 객체"를 Bean으로 등록할 수도 있다.
 *
 * 이번 프로젝트에서는 Clock 객체를 Bean으로 등록한다.
 * 그러면 Service가 "시간이 필요할 때" 직접 new로 만들지 않고,
 * IoC 컨테이너가 관리하는 Clock을 주입받을 수 있다.
 *
 * 왜 좋은가?
 * - 시간 의존성을 외부화할 수 있다.
 * - 테스트할 때 고정 시간 Clock으로 바꾸기 쉽다.
 * - "객체 생성의 제어권"을 애플리케이션 코드가 아니라 컨테이너가 가진다.
 */
@Configuration
public class AppBeanConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
