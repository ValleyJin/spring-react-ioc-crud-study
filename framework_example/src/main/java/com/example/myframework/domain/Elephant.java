package com.example.myframework.domain;

import com.example.myframework.annotation.MyComponent;
import com.example.myframework.annotation.MyPostConstruct;
import com.example.myframework.annotation.MyPreDestroy;

@MyComponent
public class Elephant implements Animal {

    private final String name = "엘리";

    @MyPostConstruct
    public void born() {
        System.out.println("[Elephant] " + name + " 태어났다! (PostConstruct)");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String makeSound() {
        return "푸우우~";
    }

    @MyPreDestroy
    public void die() {
        System.out.println("[Elephant] " + name + " 잠들었다... (PreDestroy)");
    }
}
