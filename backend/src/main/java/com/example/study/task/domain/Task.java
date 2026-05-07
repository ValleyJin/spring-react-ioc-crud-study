package com.example.study.task.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * JPA Entity.
 *
 * 이 클래스의 인스턴스는 단순한 Java 객체(POJO)이지만,
 * @Entity가 붙으면 JPA가 이 객체를 DB 테이블의 한 행(row)과 매핑한다.
 *
 * 메모리 관점:
 * - Java heap 안에는 Task 객체가 존재한다.
 * - JPA의 영속성 컨텍스트는 특정 Task 객체를 추적한다.
 * - 필드 변경이 일어나면 flush 시점에 SQL UPDATE로 반영될 수 있다.
 *
 * 중요한 점:
 * - Entity는 "DB와 가까운 모델"
 * - API 응답/요청은 DTO로 분리하는 것이 실무적으로 안전하다.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * nullable = false:
     * DB 레벨에서도 null을 막는다.
     */
    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Task() {
        // JPA는 리플렉션으로 객체를 생성할 수 있어야 하므로 기본 생성자가 필요하다.
    }

    public Task(String title,
                String description,
                TaskStatus status,
                Integer priority,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Entity 내부에 "자기 상태를 바꾸는 메서드"를 두면
     * 비즈니스 규칙을 하나의 장소에 묶기 쉬워진다.
     */
    public void update(String title, String description, TaskStatus status, Integer priority, LocalDateTime updatedAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public void toggleDone(LocalDateTime updatedAt) {
        if (this.status == TaskStatus.DONE) {
            this.status = TaskStatus.TODO;
        } else {
            this.status = TaskStatus.DONE;
        }
        this.updatedAt = updatedAt;
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
