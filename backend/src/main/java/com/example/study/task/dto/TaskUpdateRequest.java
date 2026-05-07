package com.example.study.task.dto;

import com.example.study.task.domain.TaskStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * PUT 요청 DTO.
 * 이번 예제에서는 create와 update 구조가 같지만,
 * 실무에서는 update용 DTO가 부분 수정(PATCH) 전용으로 달라지는 경우도 많다.
 */
public class TaskUpdateRequest {

    @NotBlank(message = "title은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "title은 100자를 넘길 수 없습니다.")
    private String title;

    @NotBlank(message = "description은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "description은 1000자를 넘길 수 없습니다.")
    private String description;

    @NotNull(message = "status는 필수입니다.")
    private TaskStatus status;

    @NotNull(message = "priority는 필수입니다.")
    @Min(value = 1, message = "priority는 1 이상이어야 합니다.")
    @Max(value = 5, message = "priority는 5 이하여야 합니다.")
    private Integer priority;

    public TaskUpdateRequest() {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
