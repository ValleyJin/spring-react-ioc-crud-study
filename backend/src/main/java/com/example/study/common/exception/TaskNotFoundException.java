package com.example.study.common.exception;

/**
 * 도메인 의미를 가진 예외 클래스를 따로 두면
 * "무슨 에러가 났는지"가 코드 구조에서 명확해진다.
 *
 * IllegalArgumentException 같은 일반 예외를 남발하면,
 * 컨트롤러/핸들러에서 의미 해석이 어려워진다.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long taskId) {
        super("Task not found. id=" + taskId);
    }
}
