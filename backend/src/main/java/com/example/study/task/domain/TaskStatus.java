package com.example.study.task.domain;

/**
 * enum은 "정해진 값 집합"을 표현할 때 사용한다.
 *
 * 문자열을 아무렇게나 쓰면 오타("DONEE", "donE")가 런타임 버그로 이어지기 쉽다.
 * enum은 컴파일 시점에 가능한 값을 제한해준다.
 */
public enum TaskStatus {
    TODO,
    DOING,
    DONE
}
