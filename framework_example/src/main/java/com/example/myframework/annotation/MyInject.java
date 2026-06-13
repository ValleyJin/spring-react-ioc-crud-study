package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 의존성 주입 대상 필드임을 표시한다.
 * Spring의 {@code @Autowired} 와 같은 역할.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MyInject {
}
