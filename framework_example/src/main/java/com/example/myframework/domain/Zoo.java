package com.example.myframework.domain;

import com.example.myframework.annotation.MyComponent;
import com.example.myframework.annotation.MyInject;
import com.example.myframework.annotation.MyPostConstruct;
import com.example.myframework.annotation.MyPreDestroy;

import java.util.List;

@MyComponent
public class Zoo {

    /**
     * 컨테이너가 Animal 타입 Bean 들을 모아서 이 List 에 주입한다.
     * Spring 으로 치면:
     *   {@code @Autowired private List<Animal> animals;}
     */
    @MyInject
    private List<Animal> animals;

    @MyPostConstruct
    public void open() {
        System.out.println("[Zoo] 개장! 동물 " + animals.size() + " 마리가 입주했습니다.");
    }

    public void showAll() {
        System.out.println("[Zoo] === 동물원 둘러보기 ===");
        for (Animal a : animals) {
            System.out.println("  - " + a.getName() + " : " + a.makeSound());
        }
    }

    @MyPreDestroy
    public void close() {
        System.out.println("[Zoo] 폐장합니다.");
    }
}
