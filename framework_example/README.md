# Mini IoC Framework — 동물원 예제

수업용으로 만든 **아주 작은 IoC 컨테이너**입니다.
Spring 의 핵심 개념(컴포넌트 스캔, 의존성 주입, 생명주기)을 ~200줄의 자바 코드로 직접 만들어 봅니다.

> 이 예제는 **백엔드 전용 Java 프로젝트**입니다. 프론트엔드는 없습니다.

---

## 1. 사전 요구사항

| 도구 | 버전 | 확인 |
|---|---|---|
| **JDK** | 21 이상 | `java -version` |
| **Maven** | 3.9 이상 | `mvn -version` |

> 상위 프로젝트(`backend/`)와 동일하게 SDKMAN 으로 Java 21 (Temurin) 을 잡아두면 됩니다.

---

## 2. 빌드 도구

이 예제는 **Maven** 만 사용합니다.

- 외부 의존성은 **0개** (의도적으로 표준 라이브러리만 사용)
- 그래서 인터넷 없이도 빌드/실행이 가능합니다.

---

## 3. 디렉터리 구조

```
framework_example/
├── README.md                                ← 이 파일
├── pom.xml                                  ← Maven 설정
└── src/main/java/com/example/myframework/
    ├── annotation/
    │   ├── MyComponent.java                 ← @MyComponent
    │   ├── MyInject.java                    ← @MyInject
    │   ├── MyPostConstruct.java             ← @MyPostConstruct
    │   └── MyPreDestroy.java                ← @MyPreDestroy
    ├── container/
    │   └── MyContainer.java                 ← 핵심 IoC 컨테이너
    ├── domain/
    │   ├── Animal.java                      ← 동물 인터페이스
    │   ├── Lion.java                        ← @MyComponent
    │   ├── Elephant.java                    ← @MyComponent
    │   └── Zoo.java                         ← @MyComponent + @MyInject List<Animal>
    └── Main.java                            ← 실행 진입점
```

---

## 4. 빌드 방법

`framework_example/` 디렉터리로 이동해서:

```bash
cd framework_example
mvn clean package
```

성공하면 `target/myframework-1.0.0.jar` 가 생성됩니다.

---

## 5. 실행 방법

### 방법 A. Maven 으로 직접 실행 (개발 중 추천)

```bash
mvn -q compile exec:java -Dexec.mainClass=com.example.myframework.Main
```

> Maven 의 `exec-maven-plugin` 이 없어도 동작합니다 (compile 만 한 뒤 자체 클래스패스로 실행하려면 다음 방법 B 를 사용).

### 방법 B. 컴파일된 클래스 디렉터리에서 직접 실행 (추천)

```bash
mvn -q package
java -cp target/classes com.example.myframework.Main
```

> **참고**: `java -jar target/myframework-1.0.0.jar` 는 **동작하지 않습니다.**
> 이 교육용 컨테이너의 `findClasses()` 는 파일시스템 디렉터리만 스캔합니다.
> JAR 안의 `jar:file:...` URI 는 `new File(URI)` 가 지원하지 않아서 `URI is not hierarchical` 에러가 납니다.
> 실제 Spring 은 이를 위해 별도의 `JarFile` 처리 로직을 갖고 있습니다 — 이 예제에서는 학습 범위를 좁히기 위해 의도적으로 제외했습니다.

### 방법 C. javac 로 직접 컴파일 (Maven 없이)

```bash
mkdir -p out
find src/main/java -name "*.java" -print | xargs javac -d out
java -cp out com.example.myframework.Main
```

---

## 6. 예상 출력

```
==================================================
 Mini IoC Framework - 동물원 예제
==================================================
[Container] 'com.example.myframework' 패키지 스캔 시작
[Container] Bean 생성: Lion
[Container] Bean 생성: Elephant
[Container] Bean 생성: Zoo
[Container] 주입: Zoo.animals <- 2 개의 Animal Bean
[Lion] 레오 태어났다! (PostConstruct)
[Elephant] 엘리 태어났다! (PostConstruct)
[Zoo] 개장! 동물 2 마리가 입주했습니다.
[Container] 가동 완료. 총 3 개의 Bean

[Zoo] === 동물원 둘러보기 ===
  - 레오 : 어흥!
  - 엘리 : 푸우우~

[Container] 종료 시작
[Zoo] 폐장합니다.
[Elephant] 엘리 잠들었다... (PreDestroy)
[Lion] 레오 잠들었다... (PreDestroy)
[Container] 종료 완료
```

> Bean 생성 순서는 OS/JDK 의 파일 탐색 순서에 따라 **Lion/Elephant 의 출력 순서가 바뀔 수 있습니다.** 동작 자체는 동일합니다.

---

## 7. 학습 가이드

상세한 단계별 설명과 Spring 개념과의 매핑은 같은 폴더의 **[`강의.md`](./강의.md)** 를 참고하세요.

학습 순서 (강의.md 의 목차에 대응):
1. 어노테이션 4개 작성 (3장)
2. 도메인 클래스 작성 — Animal / Lion / Elephant (4장), Zoo (5장)
3. `MyContainer` 구현 — 스캔 → 생성 → 주입 → 생명주기 (6장)
4. `Main` 으로 실행 / 출력 분석 (7~9장)
5. Spring 과의 매핑 비교 (10~11장)
6. 직접 확장해 보기 — 5가지 과제 (12장)

---

## 8. 자주 묻는 질문

**Q. 왜 외부 라이브러리(예: Reflections)를 안 쓰나요?**
A. 학습용이라 표준 JDK 기능만으로 컨테이너의 본질을 보여주기 위해서입니다. Spring 도 내부적으로는 비슷한 방식으로 컴포넌트 스캔을 수행합니다.

**Q. JAR 안에 패키징된 상태로 스캔하려면?**
A. 이 예제의 `findClasses()` 는 파일시스템 디렉터리만 다룹니다. JAR 스캔은 `JarFile` API 를 추가로 다뤄야 하며, 학습 범위를 좁히기 위해 의도적으로 제외했습니다. 그래서 `java -jar` 로 실행해도 컴파일된 클래스가 `target/classes` 디렉터리에 그대로 있어야 동작합니다 (Maven 빌드 결과물이 그렇습니다).

**Q. Spring 과 정확히 어떻게 매핑되나요?**

| 이 프로젝트 | Spring 등가 |
|---|---|
| `@MyComponent` | `@Component`, `@Service`, `@Repository` |
| `@MyInject` | `@Autowired` (필드 주입) |
| `@MyPostConstruct` | `@PostConstruct` (JSR-250) |
| `@MyPreDestroy` | `@PreDestroy` (JSR-250) |
| `MyContainer` | `ApplicationContext` (`AnnotationConfigApplicationContext`) |
| `container.scan(...)` | `@ComponentScan` |
| `container.getBean(...)` | `ApplicationContext#getBean(...)` |
