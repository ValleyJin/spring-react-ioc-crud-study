package com.example.myframework.container;

import com.example.myframework.annotation.MyComponent;
import com.example.myframework.annotation.MyInject;
import com.example.myframework.annotation.MyPostConstruct;
import com.example.myframework.annotation.MyPreDestroy;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 아주 작은 IoC 컨테이너.
 *
 * 동작 순서:
 *   1) 패키지를 스캔해서 @MyComponent 가 붙은 클래스를 모두 찾는다.
 *   2) 각 클래스의 인스턴스(Bean)를 생성한다.
 *   3) @MyInject 가 붙은 필드에 알맞은 Bean 을 주입한다 (List 도 지원).
 *   4) @MyPostConstruct 메서드를 호출한다 (태어날 때).
 *   5) shutdown() 호출 시 @MyPreDestroy 메서드를 호출한다 (죽을 때).
 *
 * 의도적으로 단순하게 만든 교육용 코드이므로 다음은 지원하지 않는다:
 *   - 생성자 주입 / setter 주입
 *   - 스코프 (prototype 등)
 *   - 순환 참조 감지
 *   - JAR 안에서의 스캔
 */
public class MyContainer {

    /**
     * Bean 저장소.
     * 등록 순서를 보존하기 위해 LinkedHashMap 사용
     * → @MyPreDestroy 를 등록 역순으로 호출하는 데 활용한다.
     */
    private final Map<Class<?>, Object> beans = new LinkedHashMap<>();

    /** 패키지를 스캔하고 컨테이너를 가동한다. */
    public void scan(String basePackage) throws Exception {
        System.out.println("[Container] '" + basePackage + "' 패키지 스캔 시작");

        List<Class<?>> classes = findClasses(basePackage);

        // 1) Bean 생성
        for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(MyComponent.class)) {
                Object instance = cls.getDeclaredConstructor().newInstance();
                beans.put(cls, instance);
                System.out.println("[Container] Bean 생성: " + cls.getSimpleName());
            }
        }

        // 2) 의존성 주입
        for (Object bean : beans.values()) {
            injectDependencies(bean);
        }

        // 3) PostConstruct 호출
        for (Object bean : beans.values()) {
            invokeLifecycle(bean, MyPostConstruct.class);
        }

        System.out.println("[Container] 가동 완료. 총 " + beans.size() + " 개의 Bean");
    }

    /** 타입으로 Bean 을 찾는다. */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object found = findBeanByType(type);
        if (found == null) {
            throw new IllegalStateException("Bean 을 찾을 수 없습니다: " + type.getName());
        }
        return (T) found;
    }

    /** 컨테이너를 정리한다. 등록의 역순으로 @MyPreDestroy 호출. */
    public void shutdown() throws Exception {
        System.out.println("[Container] 종료 시작");

        List<Object> reverse = new ArrayList<>(beans.values());
        Collections.reverse(reverse);
        for (Object bean : reverse) {
            invokeLifecycle(bean, MyPreDestroy.class);
        }
        beans.clear();
        System.out.println("[Container] 종료 완료");
    }

    // ===== 내부 헬퍼 =====

    private void injectDependencies(Object bean) throws Exception {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(MyInject.class)) continue;

            field.setAccessible(true);

            // List<T> 주입 지원
            if (List.class.isAssignableFrom(field.getType())) {
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                Class<?> itemType = (Class<?>) pt.getActualTypeArguments()[0];
                List<Object> matches = new ArrayList<>();
                for (Map.Entry<Class<?>, Object> e : beans.entrySet()) {
                    if (itemType.isAssignableFrom(e.getKey())) {
                        matches.add(e.getValue());
                    }
                }
                field.set(bean, matches);
                System.out.println("[Container] 주입: " + bean.getClass().getSimpleName()
                        + "." + field.getName() + " <- " + matches.size() + " 개의 "
                        + itemType.getSimpleName() + " Bean");
                continue;
            }

            // 단일 타입 주입
            Object dep = findBeanByType(field.getType());
            if (dep == null) {
                throw new IllegalStateException(
                        "주입할 Bean 을 찾을 수 없습니다: " + field.getType().getName());
            }
            field.set(bean, dep);
            System.out.println("[Container] 주입: " + bean.getClass().getSimpleName()
                    + "." + field.getName() + " <- " + dep.getClass().getSimpleName());
        }
    }

    private Object findBeanByType(Class<?> type) {
        for (Map.Entry<Class<?>, Object> e : beans.entrySet()) {
            if (type.isAssignableFrom(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }

    private void invokeLifecycle(Object bean, Class<? extends java.lang.annotation.Annotation> ann) throws Exception {
        for (Method m : bean.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(ann)) {
                m.setAccessible(true);
                m.invoke(bean);
            }
        }
    }

    /** 클래스패스에서 해당 패키지의 .class 파일을 찾아 Class 객체로 로드. */
    private List<Class<?>> findClasses(String basePackage) throws Exception {
        String path = basePackage.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resource == null) return Collections.emptyList();
        File dir = new File(resource.toURI());
        List<Class<?>> result = new ArrayList<>();
        collect(dir, basePackage, result);
        return result;
    }

    private void collect(File dir, String pkg, List<Class<?>> out) throws Exception {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                collect(f, pkg + "." + f.getName(), out);
            } else if (f.getName().endsWith(".class")) {
                String name = pkg + "." + f.getName().replace(".class", "");
                out.add(Class.forName(name));
            }
        }
    }
}
