package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨테이너가 종료되기 직전에 호출될 메서드를 표시한다.
 * Spring/JSR-250 의 {@code @PreDestroy} 와 같은 역할.
 *
 * "죽을 때" 단계에 해당한다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyPreDestroy {
}
