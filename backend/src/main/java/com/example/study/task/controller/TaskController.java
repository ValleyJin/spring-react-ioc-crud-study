package com.example.study.task.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.study.task.dto.TaskCreateRequest;
import com.example.study.task.dto.TaskResponse;
import com.example.study.task.dto.TaskUpdateRequest;
import com.example.study.task.service.TaskService;

import jakarta.validation.Valid;

/**
 * Controller는 HTTP 요청의 진입점이다.
 *
 * 비유:
 * - Controller = 문지기 / 입구
 * - Service = 업무 책임자
 * - Repository = DB 담당자
 *
 * Controller가 모든 업무를 처리하면 안 된다.
 * 검증 진입, 파라미터 수신, Service 호출, 응답 반환에 집중해야 한다.
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    /**
     * 생성자 주입.
     * Controller 역시 직접 new TaskServiceImpl(...) 하지 않는다.
     * 어떤 구현체가 들어올지는 컨테이너가 결정한다.
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 예:
     * GET /api/tasks?search=react&status=TODO
     */
    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        return taskService.getTasks(search, status);
    }

    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        return taskService.updateTask(taskId, request);
    }

    @PatchMapping("/{taskId}/toggle")
    public TaskResponse toggleTask(@PathVariable Long taskId) {
        return taskService.toggleTask(taskId);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }
}
