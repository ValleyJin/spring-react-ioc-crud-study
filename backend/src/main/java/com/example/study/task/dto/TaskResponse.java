package com.example.study.task.dto;

import java.time.LocalDateTime;

import com.example.study.task.domain.Task;
import com.example.study.task.domain.TaskStatus;

/**
 * API 응답 DTO.
 *
 * 프론트엔드에는 Entity 자체가 아니라
 * "외부 공개용으로 정제된 데이터"를 내려주는 편이 좋다.
 */
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponse(Long id,
                        String title,
                        String description,
                        TaskStatus status,
                        Integer priority,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 정적 팩토리 메서드.
     * Task -> TaskResponse 변환 책임을 한 군데에 모은다.
     */
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Integer getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
