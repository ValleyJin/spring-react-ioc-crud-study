package com.example.myframework.domain;

/**
 * 모든 동물의 공통 행동을 정의하는 인터페이스.
 * Zoo 는 구체 클래스가 아니라 이 인터페이스에 의존한다 (DIP).
 */
public interface Animal {
    String getName();

    String makeSound();
}
