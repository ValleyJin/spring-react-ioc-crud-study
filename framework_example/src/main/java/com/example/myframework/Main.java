package com.example.myframework;

import com.example.myframework.container.MyContainer;
import com.example.myframework.domain.Zoo;

/**
 * 미니 IoC 프레임워크 실행 진입점.
 *
 * 흐름:
 *   1) 컨테이너 생성
 *   2) 패키지 스캔 + Bean 생성 + 주입 + PostConstruct
 *   3) Zoo Bean 을 꺼내서 사용
 *   4) shutdown -> PreDestroy
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println(" Mini IoC Framework - 동물원 예제");
        System.out.println("==================================================");

        MyContainer container = new MyContainer();
        container.scan("com.example.myframework");

        System.out.println();
        Zoo zoo = container.getBean(Zoo.class);
        zoo.showAll();

        System.out.println();
        container.shutdown();
    }
}
