package com.example.study.bootstrap;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.study.task.domain.Task;
import com.example.study.task.domain.TaskStatus;
import com.example.study.task.repository.TaskRepository;

/**
 * 애플리케이션 시작 시 샘플 데이터를 넣는 초기화용 Bean.
 *
 * CommandLineRunner도 Bean이다.
 * Spring Boot가 모든 Bean 초기화 후 run(...)을 자동 호출한다.
 *
 * 여기에서도 IoC가 보인다:
 * - DataInitializer가 TaskRepository, Clock을 직접 만들지 않는다.
 * - 컨테이너가 넣어준다.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public DataInitializer(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now(clock);

        taskRepository.save(new Task(
                "Spring IoC 개념 정리",
                "IoC, DI, Bean lifecycle을 문장으로 정리하기",
                TaskStatus.TODO,
                5,
                now.minusDays(2),
                now.minusDays(2)
        ));

        taskRepository.save(new Task(
                "React Query로 목록 조회",
                "useQuery의 queryKey와 cache 구조 이해하기",
                TaskStatus.DOING,
                4,
                now.minusDays(1),
                now.minusHours(12)
        ));

        taskRepository.save(new Task(
                "React 최적화 체크",
                "useMemo / useCallback / React.memo 동작 확인",
                TaskStatus.DONE,
                3,
                now.minusHours(10),
                now.minusHours(2)
        ));
    }
}
