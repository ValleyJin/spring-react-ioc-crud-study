package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean 이 생성되고 의존성이 모두 주입된 직후 호출될 메서드를 표시한다.
 * Spring/JSR-250 의 {@code @PostConstruct} 와 같은 역할.
 *
 * "태어날 때" 단계에 해당한다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyPostConstruct {
}
