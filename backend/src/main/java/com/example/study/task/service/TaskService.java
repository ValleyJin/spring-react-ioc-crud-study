package com.example.study.task.service;

import java.util.List;

import com.example.study.task.dto.TaskCreateRequest;
import com.example.study.task.dto.TaskResponse;
import com.example.study.task.dto.TaskUpdateRequest;

/**
 * Service 인터페이스.
 *
 * 학습 목적상 인터페이스를 두는 이유:
 * 1. Controller는 구현체가 아니라 "역할"에 의존하게 한다.
 * 2. 나중에 다른 구현체로 교체하기 쉽다.
 * 3. 테스트 시 mock/stub 대체가 쉬워진다.
 *
 * 이것은 DIP(Dependency Inversion Principle)와도 연결된다.
 */
public interface TaskService {

    List<TaskResponse> getTasks(String search, String status);

    TaskResponse createTask(TaskCreateRequest request);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);

    TaskResponse toggleTask(Long taskId);

    void deleteTask(Long taskId);
}
