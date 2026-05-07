package com.example.study.task.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.study.common.exception.TaskNotFoundException;
import com.example.study.task.domain.Task;
import com.example.study.task.domain.TaskStatus;
import com.example.study.task.dto.TaskCreateRequest;
import com.example.study.task.dto.TaskResponse;
import com.example.study.task.dto.TaskUpdateRequest;
import com.example.study.task.repository.TaskRepository;

/**
 * Service 구현체.
 *
 * 이 클래스가 가장 중요한 학습 지점 중 하나다.
 *
 * 1. @Service
 *    - Spring이 이 클래스를 Bean 후보로 스캔한다.
 * 2. 생성자 주입(constructor injection)
 *    - TaskRepository와 Clock을 외부에서 넣어준다.
 *    - 클래스 내부에서 new 하지 않는다.
 *    - 즉, 객체 생성과 연결의 제어권이 IoC 컨테이너에 있다.
 *
 * CS/메모리 관점:
 * - ApplicationContext가 시작될 때 Service 객체가 heap에 생성된다.
 * - 그 안의 필드 taskRepository, clock은 각각 다른 Bean 객체를 가리키는 참조(reference)다.
 * - 이후 Controller가 Service를 호출하면, 같은 singleton Bean 인스턴스를 계속 사용한다.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public TaskServiceImpl(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(String search, String status) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        String normalizedStatus = status == null ? "ALL" : status.trim().toUpperCase(Locale.ROOT);

        return taskRepository.findAllByOrderByIdDesc()
                .stream()
                .filter(task -> matchesSearch(task, normalizedSearch))
                .filter(task -> matchesStatus(task, normalizedStatus))
                .map(TaskResponse::from)
                .toList();
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);

        Task task = new Task(
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getStatus(),
                request.getPriority(),
                now,
                now
        );

        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = getTaskOrThrow(taskId);

        task.update(
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getStatus(),
                request.getPriority(),
                LocalDateTime.now(clock)
        );

        // JPA dirty checking으로 인해 명시적 save 없이도 update 가능하지만,
        // 초보자에게 "변경 의도"를 더 명확히 드러내기 위해 save를 호출한다.
        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Override
    public TaskResponse toggleTask(Long taskId) {
        Task task = getTaskOrThrow(taskId);
        task.toggleDone(LocalDateTime.now(clock));

        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = getTaskOrThrow(taskId);
        taskRepository.delete(task);
    }

    private Task getTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private boolean matchesSearch(Task task, String normalizedSearch) {
        if (normalizedSearch.isBlank()) {
            return true;
        }

        String title = task.getTitle().toLowerCase(Locale.ROOT);
        String description = task.getDescription().toLowerCase(Locale.ROOT);

        return title.contains(normalizedSearch) || description.contains(normalizedSearch);
    }

    private boolean matchesStatus(Task task, String normalizedStatus) {
        if (normalizedStatus.equals("ALL") || normalizedStatus.isBlank()) {
            return true;
        }

        return task.getStatus().name().equals(normalizedStatus);
    }
}
