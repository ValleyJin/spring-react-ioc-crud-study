package com.example.study.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.study.task.domain.Task;

/**
 * Spring Data JPA Repository.
 *
 * 이 인터페이스는 구현체가 없다.
 * 그런데도 동작한다.
 *
 * 왜?
 * - Spring Data JPA가 런타임에 프록시(proxy) 구현체를 생성해서 Bean으로 등록하기 때문이다.
 * - 즉, IoC 컨테이너가 "실제 구현 객체"를 만들어 의존성 주입에 사용한다.
 *
 * 여기서 초보자가 중요한 감각:
 * "내가 new TaskRepositoryImpl()을 하지 않았는데도 동작한다"
 * -> 이것이 바로 IoC / 프레임워크 주도 구조의 전형적인 모습이다.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByIdDesc();
}
