package com.example.myframework.domain;

import com.example.myframework.annotation.MyComponent;
import com.example.myframework.annotation.MyPostConstruct;
import com.example.myframework.annotation.MyPreDestroy;

@MyComponent
public class Lion implements Animal {

    private final String name = "레오";

    @MyPostConstruct
    public void born() {
        System.out.println("[Lion] " + name + " 태어났다! (PostConstruct)");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String makeSound() {
        return "어흥!";
    }

    @MyPreDestroy
    public void die() {
        System.out.println("[Lion] " + name + " 잠들었다... (PreDestroy)");
    }
}
