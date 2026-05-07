package com.example.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 애플리케이션의 진입점(entry point)이다.
 *
 * 메모리/CS 관점 설명:
 * 1. main 메서드는 JVM 프로세스가 시작될 때 호출된다.
 * 2. SpringApplication.run(...)이 실행되면 Spring이 내부적으로 ApplicationContext
 *    (즉, IoC 컨테이너)를 만들고, 필요한 Bean 객체들을 heap 메모리에 생성한다.
 * 3. 각 Bean 간의 참조(의존성)를 연결한 뒤, 내장 Tomcat 서버를 띄운다.
 *
 * @SpringBootApplication은 사실 3개의 핵심 애노테이션을 묶은 축약형이다.
 * - @Configuration
 * - @EnableAutoConfiguration
 * - @ComponentScan
 *
 * 즉, "이 클래스부터 시작해서 프로젝트를 스캔하고, Spring Boot의 자동설정을 켜라"
 * 라는 선언이다.
 */
@SpringBootApplication
public class StudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class, args);
    }
}
