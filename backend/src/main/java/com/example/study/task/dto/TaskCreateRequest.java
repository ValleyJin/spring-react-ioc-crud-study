package com.example.study.task.dto;

import com.example.study.task.domain.TaskStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 프론트엔드가 POST /api/tasks 로 보낼 요청 body 구조.
 *
 * Entity를 그대로 입력 모델로 쓰지 않는 이유:
 * 1. 외부 입력과 내부 저장 모델의 관심사가 다르다.
 * 2. id, createdAt 같은 필드는 사용자가 보내면 안 된다.
 * 3. 검증 규칙도 입력 DTO에 붙이는 것이 더 자연스럽다.
 */
public class TaskCreateRequest {

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

    public TaskCreateRequest() {
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
