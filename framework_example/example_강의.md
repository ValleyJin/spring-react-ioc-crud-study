# 미니 IoC 프레임워크 만들기 (실습)

> 🎯 **이 강의의 목적**: Spring 의 핵심(IoC 컨테이너 + 컴포넌트 스캔 + 의존성 주입 + 생명주기)을  
> 약 200줄의 순수 Java 코드로 **직접 만들어 보고**, 그 과정을 통해 Spring 이 무엇을 자동화해 주는지 몸으로 이해한다.

순차적으로 따라 코딩하면 작동하는 미니 프레임워크가 완성됩니다.
모든 코드는 이 `framework_example/` 디렉터리에 들어갑니다.

> 💬 **이 강의의 전제**: Java 기본 문법(클래스, 인터페이스, 패키지, import)은 아신다고 가정합니다.
> OOP 기초·어노테이션·리플렉션·제네릭 타입 소거는 **2장 사전 지식** 에서 정리합니다.

---

## 목차

0. [클래스 익히기 (사전 학습)](#sec-0)
   - [0.0 실습 환경 준비 — `myframework/chapter_0/` 폴더에서 코딩하기](#sec-0-0)
   - [0.1 클래스와 객체](#sec-0-1)
   - [0.2 객체 변수란?](#sec-0-2)
   - [0.3 메서드란?](#sec-0-3)
   - [0.4 객체 변수는 공유되지 않는다](#sec-0-4)
   - [0.5 인터페이스는 왜 필요한가?](#sec-0-5)
   - [0.6 인터페이스 작성하기](#sec-0-6)
   - [0.7 인터페이스의 메서드](#sec-0-7)
   - [0.8 인터페이스 더 파고들기](#sec-0-8)
   - [0.9 디폴트 메서드](#sec-0-9)
   - [0.10 추상 클래스 (abstract class)](#sec-0-10)
   - [0.11 다음 장으로 가는 다리](#sec-0-11)
1. [무엇을 만드는가? 개방-폐쇄원칙 (OCP: Open-Closed Principle)](#sec-1)
   - [1.1 DIP 와 IoC — 두 개의 "역전"](#sec-1-1)
   - [1.2 사례: 각종 Pay 추가](#sec-1-2)
2. [사전 지식 — OOP · 어노테이션 · 리플렉션](#sec-2)
3. [프로젝트 셋업 (사전 요구사항 포함)](#sec-3)
4. [어노테이션 4개 작성](#sec-4)
5. [도메인 클래스 — Animal, Lion, Elephant](#sec-5)
6. [도메인 클래스 — Zoo (의존성을 받는 쪽)](#sec-6)
7. [핵심 — MyContainer 구현](#sec-7)
8. [실행 진입점 — Main](#sec-8)
9. [빌드 및 실행](#sec-9)
10. [점진적 빌드 — 5단계로 직접 검증하기](#sec-10)
11. [자가 검증 — JUnit 테스트](#sec-11)
12. [예상 출력과 해석](#sec-12)
13. [Spring 과의 정확한 매핑 (+ 전체 생명주기 표)](#sec-13)
14. [Spring Boot에서 동일 예제 재현](#sec-14)
15. [Spring 이 추가로 해주는 것들 + AOP 맛보기](#sec-15)
16. [흔한 에러와 해결법](#sec-16)
17. [IDE 사용 안내](#sec-17)
18. [직접 확장해 보기 (과제)](#sec-18)
19. [한 줄 요약](#sec-19)

---

<a id="sec-0"></a>
## 0. 클래스 익히기 (사전 학습)

> 🎯 **이 장의 목적**: 이 강의 전체는 `Animal`, `Lion`, `Elephant`, `Zoo` 같은 **클래스 + 인터페이스** 들을 만들고,
> 그 객체들을 컨테이너가 자동으로 조립해 주는 과정을 다룹니다.
> 그래서 **"클래스가 무엇이고, 객체가 무엇이며, 객체 변수·메서드·인터페이스가 어떻게 동작하는가"** 가 모든 출발점입니다.
> 이 장은 박응용 저 **"점프 투 자바"** 의 **05-02 클래스**, **05-07 인터페이스**, **05-09 추상 클래스** 절을 발췌·재구성하여, 본 강의에 가장 직접적으로 필요한 부분만 짧고 단단하게 다집니다.
> (원문: https://wikidocs.net/214 , https://wikidocs.net/217 , https://wikidocs.net/219)

> 💡 **이 책에서는 클래스에 대한 개념적인 설명을 하기보다는 가장 간단한 클래스를 작성하는 것에서부터 시작하여 조금씩 클래스의 규모를 키워 가며 설명한다.**
>
> 다룰 항목:
> - 클래스와 객체
> - 객체 변수란?
> - 메서드란?
> - 객체 변수는 공유되지 않는다
> - **인터페이스는 왜 필요한가?**
> - **인터페이스 작성하기 / 인터페이스의 메서드**
> - **인터페이스 더 파고들기 / 디폴트 메서드**
> - **추상 클래스 (abstract class)**

---

<a id="sec-0-0"></a>
### 0.0 실습 환경 준비 — `myframework/chapter_0/` 폴더에서 코딩하기

> 🎯 **이 절의 목적**: 0장의 모든 코드 예제(`Sample.java`)를 **실제로 만들고, 컴파일하고, 실행** 해 보기 위한 가장 간단한 작업 폴더를 만듭니다.
> 본 강의의 미니 IoC 프레임워크(7장에서 만들 `framework_example/`)와는 **분리된 연습용 워크스페이스**입니다. Maven 도, IDE 도 필요 없습니다 — `javac` 와 `java` 두 명령만 씁니다.

#### 0.0.1 사전 확인 — JDK 가 설치되어 있나?

```bash
java -version    # openjdk version "21" 같은 출력이 나오면 OK
javac -version   # javac 21 같은 출력이 나오면 OK
```

> 위 두 명령 중 하나라도 "command not found" 가 나오면 **JDK 21 이 설치되어 있지 않은 상태** 입니다. **3.1 사전 요구사항 → JDK 21 설치 (macOS)** 절을 먼저 보고 SDKMAN 옵션 A 로 설치한 뒤 돌아오세요. (이 강의는 Java 21 기준입니다.)

#### 0.0.2 작업 폴더 만들기

이 강의의 모든 연습 코드를 한 곳에 모아두기 위해 `myframework/` 라는 부모 워크스페이스를 만들고, 그 안에 장(章) 별 폴더를 둡니다.

```bash
# 원하는 위치에서 (예: 홈 디렉터리 또는 강의 폴더 옆)
mkdir -p ~/myframework/chapter_0
cd       ~/myframework/chapter_0
pwd       # /Users/<당신>/myframework/chapter_0
```

이후 0장의 모든 예제는 **이 `chapter_0/` 폴더 안** 에서 만들고 실행합니다.

```
~/myframework/
├── chapter_0/        ← 0장 연습 (지금 만드는 폴더)
├── chapter_1/        ← (선택) 이후 장 별로 폴더를 늘려도 됨
└── ...
```

> 📎 폴더 이름·위치는 자유입니다. 이 강의에서는 설명의 일관성을 위해 `~/myframework/chapter_0/` 로 지칭합니다.
> Windows 라면 `C:\myframework\chapter_0\` 또는 `%USERPROFILE%\myframework\chapter_0\` 정도로 읽어 주세요.

#### 0.0.3 첫 파일 작성 — `Sample.java`

가장 간단한 "Hello, class!" 예제부터. 0.1 절 첫 코드와 같습니다.

```bash
# 빈 파일 생성 (또는 에디터로 직접 만들어도 됨)
touch Sample.java
```

`Sample.java` 를 에디터로 열어 다음 내용을 그대로 붙여넣고 저장합니다.

```java
class Animal {
}

public class Sample {
    public static void main(String[] args) {
        System.out.println("hello, class!");
    }
}
```

> ⚠️ **파일 이름 규칙 — 자바의 절대 원칙**
> - 자바 소스 파일 이름은 **그 안에 들어 있는 `public class` 의 이름과 정확히 같아야** 합니다(대소문자 포함).
> - 위 코드는 `public class Sample` 이므로 파일 이름은 반드시 **`Sample.java`** 여야 컴파일됩니다.
> - 같은 파일 안의 비-public 클래스(`class Animal { }`)는 파일 이름과 같지 않아도 됩니다 — **한 파일에 여러 클래스** 를 쓸 수 있다는 의미입니다.

#### 0.0.4 컴파일하기 — `javac`

```bash
cd ~/myframework/chapter_0   # 폴더 안에 있는지 확인
javac Sample.java
ls                            # Sample.java + Animal.class + Sample.class 가 보임
```

- `javac Sample.java` 한 번이면 **그 파일 안의 모든 클래스가 각각 `.class` 로 컴파일** 됩니다.
- 결과로 `Animal.class`, `Sample.class` 두 파일이 생깁니다.
- **에러가 나면** 가장 흔한 원인:
  - 오타 (`pubic` ← `public`, `static` 빠뜨림 등) — 메시지의 줄 번호를 보고 수정
  - 파일 이름과 `public class` 이름 불일치 — 둘을 동일하게 맞추기

#### 0.0.5 실행하기 — `java`

```bash
java Sample        # ⚠️ Sample.class 가 아니라 "Sample" (확장자 없이!)
# 출력: hello, class!
```

> ⚠️ `java Sample.class` 또는 `java Sample.java` 가 아니라 **`java Sample`** 입니다 — **클래스 이름** 을 인자로 넘기는 것이지 파일을 넘기는 것이 아닙니다.
> (Java 11+ 부터는 `java Sample.java` 처럼 소스 파일을 바로 실행할 수도 있지만, 본 강의에서는 표준 방식인 `javac` → `java` 2단계로 진행합니다.)

#### 0.0.6 한 줄 워크플로우 — 매 예제마다 이대로 반복

이후 0.1 ~ 0.10 의 모든 예제는 다음 한 줄 흐름으로 실행하면 됩니다.

```bash
# (1) Sample.java 를 새 코드로 덮어쓰거나 수정한 뒤
javac Sample.java && java Sample
```

- `&&` 는 "앞 명령이 성공해야 뒤 명령을 실행" 한다는 의미라, **컴파일 에러가 있으면 실행은 자동으로 건너뜁니다.**
- 매번 `javac` 결과로 생기는 `.class` 파일들은 그대로 두면 다음 컴파일 시 덮어써집니다. 깔끔히 지우고 싶다면:

```bash
rm -f *.class
```

#### 0.0.7 코드를 바꿔가며 익히는 권장 패턴

0장의 절(0.1 → 0.2 → … → 0.10)을 진행하면서 **`Sample.java` 를 계속 덮어쓰며** 다음을 반복하세요.

1. 해당 절의 코드 블록을 `Sample.java` 에 통째로 붙여넣기
2. `javac Sample.java && java Sample` 로 실행
3. 출력이 절의 "예상 출력" 과 일치하는지 확인
4. **한두 줄을 일부러 바꿔보고** 결과가 어떻게 달라지는지 관찰 (예: `cat.setName("boby")` 를 `cat.setName("나비")` 로)
5. 다음 절로

> 💡 **소스를 잃기 싫다면**: 절마다 별도 파일로 보관해도 좋습니다.
>
> ```bash
> cp Sample.java Sample_0_1.java   # 0.1 절 끝난 뒤 백업
> ```
> 단, 백업본은 `public class` 이름이 `Sample` 이라 그대로는 컴파일이 안 되므로 (이름 충돌), **공부 기록용 보관** 으로만 쓰세요. 실제 컴파일/실행은 항상 `Sample.java` 한 파일에서 합니다.

#### 0.0.8 자주 만나는 에러 빠른 해결

| 증상 | 원인 | 해결 |
| --- | --- | --- |
| `command not found: java` / `javac` | JDK 가 설치 안 됨 또는 `PATH` 누락 | 3.1 절로 가서 SDKMAN 으로 설치, 새 터미널 열기 |
| `error: class Sample is public, should be declared in a file named Sample.java` | 파일 이름과 `public class` 이름 불일치 | 파일 이름 또는 클래스 이름을 둘 중 하나로 통일 |
| `Error: Could not find or load main class Sample` | 현재 폴더에 `Sample.class` 가 없음 / 폴더가 다름 | `ls` 로 `.class` 존재 확인, 같은 폴더에서 실행 |
| `java: cannot find symbol` | 오타, 또는 다른 파일의 클래스를 참조 | 메시지의 심볼·줄 번호 확인, 같은 파일 안에 있는지 점검 |
| 한글이 깨져 출력 | 터미널/파일 인코딩 문제 | 파일을 UTF-8 로 저장, `java -Dfile.encoding=UTF-8 Sample` |

> ✅ **여기까지 따라왔다면 준비 완료**. 이제 0.1 로 가서 같은 `Sample.java` 에 코드를 채워 넣고 `javac Sample.java && java Sample` 한 번이면 결과를 눈으로 확인할 수 있습니다.

---

<a id="sec-0-1"></a>
### 0.1 클래스와 객체

객체가 무엇인지를 알기 위해 먼저 `Animal` 클래스를 다음과 같이 `Sample.java` 파일에 작성하자.

```java
class Animal {
}

public class Sample {
    public static void main(String[] args) {
    }
}
```

> 📌 **꼭 읽고 넘어가기**
>
> `Animal` 클래스를 `Sample.java` 파일에 작성한 것은 책의 원활한 설명을 위해서이다. 보통 클래스는 특별한 경우가 아니라면 파일 단위로 하나씩 작성한다. 그러므로 `Animal` 클래스는 `Animal.java` 파일에 단독으로 작성하는 것이 일반적인 방법이다. 하지만 이 책의 예제 대부분은 `Sample.java` 파일만 작성하여 실행할 수 있도록 구성하였으니 참고하기 바란다.

`Animal` 클래스는 **가장 간단한 형태의 클래스** 이다. 클래스의 선언만 있고 내용이 없는 **껍데기뿐인 클래스** 이다. 하지만 이런 껍데기뿐인 클래스도 아주 중요한 기능을 가지고 있다. 바로 **객체(object)를 만드는 기능** 이다. 객체는 다음과 같이 만들 수 있다.

```java
class Animal {
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
    }
}
```

`new` 는 객체를 생성할 때 사용하는 키워드이다. 이렇게 하면 `Animal` 클래스의 **인스턴스(instance)** 인 `cat`, 즉 `Animal` 의 객체가 만들어진다.

> ❓ **객체와 인스턴스의 차이는 무엇일까?**
>
> 클래스에 의해서 만들어진 객체를 인스턴스라고도 한다. 그렇다면 객체와 인스턴스의 차이는 무엇일까? `Animal cat = new Animal()` 이렇게 만들어진 `cat` 은 객체이다. 그리고 `cat` 이라는 객체는 `Animal` 의 인스턴스이다. **인스턴스라는 말은 특정 객체(여기서는 cat)가 어떤 클래스(여기서는 Animal)의 객체인지를 관계 위주로 설명할 때 사용된다.** 즉, **'cat 은 인스턴스'** 보다는 **'cat 은 객체'** 라는 표현이, **'cat 은 Animal 의 객체'** 보다는 **'cat 은 Animal 의 인스턴스'** 라는 표현이 훨씬 잘 어울린다.

#### 🍪 비유 — 과자 틀과 과자

다음 비유를 통해 클래스를 이해해 보자.

```
   [클래스]                    [객체들]
   ┌─────┐                ┌──────┐ ┌──────┐ ┌──────┐
   │ 과자│   ──찍어내면─▶ │ 과자 │ │ 과자 │ │ 과자 │
   │  틀 │                │  1   │ │  2   │ │  3   │
   └─────┘                └──────┘ └──────┘ └──────┘
```

**과자 모양을 찍어 내는 과자 틀 = 클래스** 에 비유할 수 있고,
**과자 틀에 의해 만들어진 과자들 = 객체** 에 비유할 수 있다.

다음과 같이 무수히 많은 동물 객체(여기서는 `cat`, `dog`, `horse`, …)들을 `Animal` 클래스로 만들 수 있다.

```java
Animal cat = new Animal();
Animal dog = new Animal();
Animal horse = new Animal();
...
```

> 💡 **여기서 설명한 내용이 이해되었다면 클래스는 70% 이상 알았다 할 수 있다. 클래스는 결코 어렵지 않다.**

---

<a id="sec-0-2"></a>
### 0.2 객체 변수란?

`Animal` 이라는 '껍데기' 클래스를 조금 더 발전시켜 보자. `Animal` 클래스에 의해 만들어진 동물 객체의 이름을 지어 보자.

```java
class Animal {
    String name;
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
    }
}
```

`Animal` 클래스에 `name` 이라는 `String` 변수를 추가했다. 이렇게 클래스에 선언된 변수를 **객체 변수(instance variable)** 라고 한다. 클래스에 의해 생성되는 것은 객체, 그리고 그 클래스에 선언된 변수는 객체 변수라고 생각하면 쉽다.

> 📎 **객체 변수는 인스턴스 변수, 멤버 변수, 속성** 이라고도 한다.

객체 변수를 만들었으니 이제 객체 변수를 사용해 보자. 먼저 객체 변수 또한 변수이므로 값을 대입할 수 있다. 대입하기 전에 **객체 변수는 현재 어떤 값을 가지고 있는지를 출력해 보자**. 객체 변수를 출력하려면 먼저 객체 변수에 어떻게 접근해야 하는지를 알아야 한다. 객체 변수는 다음과 같이 **도트(`.`) 연산자** 를 이용하여 접근할 수 있다.

```
객체.객체변수
```

`Animal cat = new Animal()` 처럼 `cat` 이라는 객체를 생성했다면 이 `cat` 객체의 객체 변수 `name` 에는 다음과 같이 접근할 수 있다.

```java
cat.name   // 객체: cat, 객체변수: name
```

객체 변수에 어떤 값이 대입되어 있는지 다음과 같이 작성하여 출력해 보자.

```java
class Animal {
    String name;
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
        System.out.println(cat.name);
    }
}
```

실행 결과:

```
null
```

`cat.name` 을 출력한 결과값으로 `null` 이 나왔다. **`null` 은 값이 할당되어 있지 않은 상태** 를 말한다. 객체 변수로 `name` 을 선언했지만 아무런 값도 대입하지 않았기 때문에 `null` 이라는 값이 출력된 것이다.

---

<a id="sec-0-3"></a>
### 0.3 메서드란?

앞서 객체 변수에 접근하는 방법에 대해서 알아보았다. 이번에는 **객체 변수에 값을 대입하는 방법** 에 대해서 알아보자. 객체 변수에 값을 대입하는 방법에는 여러 가지가 있지만 여기서는 **메서드를 이용하는 방법** 에 대해서 알아보려고 한다. 클래스에는 객체 변수와 함께 메서드가 있다. **메서드(method)는 클래스 내에 구현된 함수** 를 말한다.

이제 메서드를 이용하여 `Animal` 클래스의 객체 변수인 `name` 에 값을 대입해 보자. 다음과 같이 `setName` 메서드를 추가해 보자.

> 🔰 **잠깐 — 코드를 읽기 전에: 변수 선언 · 메서드 정의 · 실행 흐름 (30초 정리)**
>
> 자바를 처음 보는 학생이라면 아래 코드에 등장하는 세 가지가 한꺼번에 낯설게 느껴질 수 있습니다. 코드 본문을 보기 전에 다음을 한 번 정리하고 가세요.
>
> ##### ① 변수 선언 — "이름이 붙은 빈 칸을 만든다"
>
> ```java
> String name;
> ```
>
> - **`String`** = 자료형(데이터 종류). "이 칸에는 문자열만 담을 수 있다" 는 약속.
> - **`name`** = 내가 짓는 변수 이름.
> - **`;`** = 한 문장(statement) 의 끝. 자바는 모든 문장이 세미콜론으로 끝납니다.
> - 클래스 `{ }` 바로 안에 적혀 있으면 **객체 변수(필드)** — **이 클래스로 만들어지는 모든 객체가 각자 하나씩 갖는 칸**.
>
> 📦 비유: `String name;` = "**여기에 이름표를 하나 붙이겠다. 그 이름표 위에는 문자(`String`)만 적을 수 있다.**"
> 아직 값은 안 넣었습니다 — 0.2 에서 본 것처럼 출력하면 `null` 이 나옵니다.
>
> ##### ② 메서드(함수) 정의 — "동작 하나를 묶어서 이름을 붙여둔다"
>
> ```java
> public void setName(String name) {
>     this.name = name;
> }
> ```
>
> 한 줄을 5조각으로 쪼개면:
>
> | 조각 | 의미 |
> | --- | --- |
> | `public` | 누구나 이 메서드를 호출할 수 있다 (접근 권한) |
> | `void` | **반환값 없음** — "결과를 돌려주지 않는다" 는 표시 |
> | `setName` | 메서드 이름 — 내가 짓는다. 동사로 시작하는 게 관습 (`set...`, `get...`, `print...`) |
> | `(String name)` | **입력 매개변수** — "호출할 때 `String` 하나를 넘겨라. 그걸 `name` 이라고 부르겠다" |
> | `{ ... }` | **메서드 본문** — 이 메서드가 호출될 때 실행될 코드 |
>
> 📦 비유: 메서드 정의 = "**전자레인지에 새 버튼을 만들어 두는 것**".
> 버튼을 만든 것만으로는 아무 일도 안 일어납니다. **눌러야(= 호출해야)** 비로소 안의 동작이 일어납니다.
>
> 그래서 위 코드처럼 `setName` 을 **정의만** 해두고 `main` 에서 호출하지 않으면, `setName` 안의 `this.name = name;` 줄은 **단 한 번도 실행되지 않습니다.** (호출은 다음 코드에서 `cat.setName("boby")` 로 등장합니다.)
>
> ##### ③ 실행 흐름 — JVM 은 어디서부터 시작하는가
>
> 자바 프로그램을 실행(`java Sample`)하면 JVM은 **오직 `main` 메서드 한 곳을 찾아서 그 안의 줄을 위에서 아래로 한 줄씩** 실행합니다.
>
> ```java
> public static void main(String[] args) {
>     Animal cat = new Animal();      // 줄 A
>     System.out.println(cat.name);   // 줄 B
> }
> ```
>
> - **줄 A** `Animal cat = new Animal();`
>   1. 오른쪽 `new Animal()` 이 먼저 실행됨 → 메모리에 **새 Animal 객체 한 개** 가 만들어짐.
>   2. 왼쪽 `Animal cat` = "Animal 타입의 객체를 가리킬 `cat` 라는 이름표를 만든다."
>   3. `=` 가 둘을 연결 → `cat` 이름표가 방금 만든 객체를 가리키게 됨.
> - **줄 B** `System.out.println(cat.name);`
>   1. `cat.name` = "`cat` 이 가리키는 객체의 `name` 칸을 꺼낸다."
>   2. 꺼낸 값을 화면에 출력. 아직 `setName` 을 호출하지 않았으니 `null` 이 출력됩니다.
>
> ##### 🎯 핵심 한 줄
>
> | 개념 | 무엇을 하는가 | 비유 |
> | --- | --- | --- |
> | **선언** | "여기에 칸이 있다" 를 알린다 | 빈 라벨 붙이기 |
> | **정의** | "이런 동작이 있다" 를 등록한다 | 전자레인지 버튼 만들기 |
> | **호출/실행** | 그 동작을 실제로 시킨다 | 그 버튼을 누른다 |
>
> 👉 이 세 가지가 머릿속에 분리되면, 아래 코드와 이후 0.3 ~ 0.4 의 모든 코드가 **"무엇이 만들어지는 줄이고 무엇이 실행되는 줄인가"** 가 한눈에 보이기 시작합니다.

```java
class Animal {
    String name;

    public void setName(String name) {
        this.name = name;
    }
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
        System.out.println(cat.name);
    }
}
```

`Animal` 클래스에 추가된 `setName` 메서드는 다음과 같은 형태의 메서드이다.

- **입력**: `String name`
- **출력**: `void` ('반환값 없음' 을 의미)

즉, 입력으로 `name` 이라는 문자열을 받고 출력은 없는 형태의 메서드이다.

> 📎 **메서드의 입출력에 대한 자세한 내용은 05-3절에 준비되어 있다.** 여기서 메서드가 도무지 이해가 되지 않는다면 05-3절을 먼저 읽고 와도 좋다.

이번에는 `setName` 메서드의 내부를 살펴보자. `setName` 메서드는 다음의 문장을 가지고 있다.

```java
this.name = name;
```

이 문장에서 `this` 에 대해서 이해하는 것은 꽤 중요하다. 하지만 이 문장에 대한 설명은 잠시 보류하고 우선 이 메서드를 호출하는 방법을 먼저 알아보자. 객체 변수에 접근하기 위해서 도트 연산자(`.`)로 접근할 수 있었던 것과 마찬가지로 **객체가 메서드를 호출하기 위해서는 `객체.메서드` 로 호출해야 한다.** 즉, 우리가 만든 `setName` 메서드를 호출하려면 다음과 같이 호출해야 한다.

```java
cat.setName("boby");
```

이때 `setName` 메서드는 입력 항목으로 `"boby"` 와 같이 문자열을 전달해야 한다. `setName` 메서드를 호출할 수 있도록 `main` 메서드를 다음과 같이 수정해 보자.

```java
class Animal {
    String name;

    public void setName(String name) {
        this.name = name;
    }
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
        cat.setName("boby");   // 메서드 호출
        System.out.println(cat.name);
    }
}
```

> 📎 이렇게 수정하면 `cat.name` 을 출력하기 전에 `setName` 메서드가 먼저 호출된다.

#### 🧭 `this` 의 정체 — 보류했던 설명

아까 설명을 잠시 보류한 `setName` 메서드의 다음 문장을 다시 살펴보자.

```java
this.name = name;
```

`main` 메서드에서 `cat.setName("boby")` 는 `"boby"` 를 입력값으로 하여 `setName` 메서드를 호출했기 때문에 `setName` 메서드의 입력 항목 `name` 에는 `"boby"` 라는 문자열이 전달될 것이다.

따라서 `setName` 메서드의 `this.name = name;` 문장은 다음과 같은 뜻이기도 하다.

```java
this.name = "boby";
```

그리고 **`setName` 메서드의 `this` 는 `Animal` 클래스에 의해서 생성된 객체를 지칭** 한다. `Animal cat = new Animal()` 과 같이 `cat` 이라는 객체를 만들고, `cat.setName("boby")` 와 같이 `cat` 객체를 사용하여 `setName` 메서드를 호출하면 `setName` 메서드 내부에 선언된 `this` 는 바로 **`cat` 객체** 를 지칭한다.

> 📎 만약 `Animal dog = new Animal()` 로 `dog` 객체를 만든 후 `dog.setName("happy")` 와 같이 호출한다면 `setName` 메서드 내부에 선언된 `this` 는 바로 **`dog` 객체** 를 가리킨다.

따라서 `this.name = "boby";` 문장은 다시 다음과 같이 해석된다.

```java
cat.name = "boby";
```

`cat.name` 과 같이 하면 객체 변수에 접근할 수 있음을 우리는 알고 있다. 객체 변수에 값을 대입하는 방법도 아주 쉽다. 그냥 변수에 값을 대입하는 것과 마찬가지 방법이다.

```
객체.객체변수 = 값
```

따라서 `cat.name = "boby"` 라는 문장은 객체 `cat` 의 객체 변수 `name` 에 `boby` 라는 값을 대입한다.

이제 `main` 메서드를 다시 실행해 보자. 다음과 같은 문자열이 출력되는 것을 확인할 수 있을 것이다.

```
boby
```

`cat.name` 은 이제 `null` 이 아니라 `boby` 임을 확인할 수 있다.

---

<a id="sec-0-4"></a>
### 0.4 객체 변수는 공유되지 않는다

`main` 메서드를 다음과 같이 변경해 보자.

```java
class Animal {
    String name;

    public void setName(String name) {
        this.name = name;
    }
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
        cat.setName("boby");

        Animal dog = new Animal();
        dog.setName("happy");
    }
}
```

`cat` 객체에는 `"boby"` 라는 이름을 대입하고, `dog` 객체에는 `"happy"` 라는 이름을 대입했다. 이렇게 하면 `setName` 메서드에 의해 다음과 같은 문장이 두 번 실행될 것이다.

```java
cat.name = "boby";
dog.name = "happy";
```

그런데 이럴 경우 `dog.name = "happy"` 라는 문장이 나중에 수행되므로 `cat.name` 의 값도 `"happy"` 라는 값으로 변경되지는 않을까? `Animal` 클래스의 객체 변수 `name` 이 `cat` 객체와 `dog` 객체 간 서로 공유되는 변수라면 아마도 그럴 것이다. 다음과 같이 작성하여 확인해 보자.

```java
class Animal {
    String name;

    public void setName(String name) {
        this.name = name;
    }
}

public class Sample {
    public static void main(String[] args) {
        Animal cat = new Animal();
        cat.setName("boby");   // 메서드 호출

        Animal dog = new Animal();
        dog.setName("happy");

        System.out.println(cat.name);
        System.out.println(dog.name);
    }
}
```

실행 결과:

```
boby
happy
```

그런데 결과를 보면 `name` 객체 변수는 **공유되지 않는다는 것** 을 확인할 수 있다. 이 부분은 정말 너무너무 중요해서 강조하고 또 강조해도 지나치지 않다. 클래스에서 가장 중요한 부분은 **객체 변수의 값이 독립적으로 유지된다는 점이다.** 사실 이 점이 바로 클래스 존재의 이유이기도 하다.

**객체 지향적(object oriented)** 이라는 말의 의미도 곱씹어 보면 결국 이 객체 변수의 값이 **독립적으로 유지되기 때문에 가능한 것** 이다.

> 📎 객체 변수의 값은 공유되지 않지만 07-3절에서 공부할 **`static`** 을 이용하게 되면 객체 변수를 공유하도록 만들 수도 있다.

---

<a id="sec-0-5"></a>
### 0.5 인터페이스는 왜 필요한가?

> 💬 **인터페이스(interface)는 초보 개발자를 괴롭히는 단골손님이다.**
> 인터페이스에 대한 개념 없이 코드로만 이해하려고 하면 곧 미궁에 빠지게 된다.
> 이렇게 이해하기 힘든 인터페이스는 도대체 왜 필요할까? 새로운 예제를 통해 인터페이스를 차근차근 알아보자.

다음은 어떤 동물원의 사육사가 하는 일이다.

```
난 동물원(zoo)의 사육사(zookeeper)이다.
육식동물(predator)이 들어오면 난 먹이를 던져준다(feed).
호랑이(tiger)가 오면 사과(apple)를 던져준다.
사자(lion)가 오면 바나나(banana)를 던져준다.
```

이와 같은 내용을 코드로 표현해 보자. 먼저 `Animal`, `Tiger`, `Lion`, `ZooKeeper` 클래스를 작성하자.

> 📎 `Sample.java` 를 수정해 다음과 같이 코딩해 보자.

```java
class Animal {
    String name;

    void setName(String name) {
        this.name = name;
    }
}

class Tiger extends Animal {
}

class Lion extends Animal {
}

class ZooKeeper {
    void feed(Tiger tiger) {   // 호랑이가 오면 사과를 던져 준다.
        System.out.println("feed apple");
    }

    void feed(Lion lion) {     // 사자가 오면 바나나를 던져준다.
        System.out.println("feed banana");
    }
}

public class Sample {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = new ZooKeeper();
        Tiger tiger = new Tiger();
        Lion lion = new Lion();
        zooKeeper.feed(tiger);   // feed apple 출력
        zooKeeper.feed(lion);    // feed banana 출력
    }
}
```

05-05 절에서 보았던 `Dog` 클래스와 마찬가지로 이번에는 `Animal` 을 상속한 `Tiger` 와 `Lion` 이 등장했다. 그리고 `ZooKeeper` 클래스를 정의하였다. `ZooKeeper` 클래스는 `tiger` 가 왔을 때, `lion` 이 왔을 때, 각각 다른 `feed` 메서드가 호출된다.

> 📎 `ZooKeeper` 클래스의 `feed` 메서드처럼 입력값의 자료형 타입이 다르지만(앞에서는 `Tiger`, `Lion` 으로 서로 다르다.) 메서드명은 동일하게(여기서는 메서드명이 `feed` 로 동일하다) 사용할 수 있다. 이런 것을 **메서드 오버로딩** 이라고 한다.

프로그램을 실행하면 다음과 같은 결과가 출력된다.

```
feed apple
feed banana
```

만약 `Tiger` 와 `Lion` 뿐이라면 `ZooKeeper` 클래스는 더 이상 할 일이 없겠지만 `Crocodile`, `Leopard` 등이 계속 추가된다면 `ZooKeeper` 는 클래스가 추가될 때마다 매번 다음과 같은 `feed` 메서드를 추가해야 한다.

> 📎 다음 코드는 눈으로만 살펴보고 넘어가자.

```java
(... 생략 ...)

class ZooKeeper {
    void feed(Tiger tiger) {
        System.out.println("feed apple");
    }

    void feed(Lion lion) {
        System.out.println("feed banana");
    }

    void feed(Crocodile crocodile) {
        System.out.println("feed strawberry");
    }

    void feed(Leopard leopard) {
        System.out.println("feed orange");
    }
}

(... 생략 ...)
```

이렇게 동물 클래스가 추가될 때마다 `feed` 메서드를 추가해야 한다면 `ZooKeeper` 클래스가 얼마나 복잡해질까? **이런 문제를 해결하기 위해 바로 인터페이스가 필요하다.**

---

<a id="sec-0-6"></a>
### 0.6 인터페이스 작성하기

다음과 같이 코드 상단에 `Predator` 인터페이스를 추가하자.

```java
interface Predator {
}

class Animal {
    String name;

    void setName(String name) {
        this.name = name;
    }
}

(... 생략 ...)
```

이 코드와 같이 인터페이스는 `class` 가 아닌 **`interface`** 키워드로 작성한다.

> 📎 인터페이스는 클래스와 마찬가지로 `Predator.java` 와 같은 단독 파일로 저장하는 것이 일반적이나 여기서는 설명의 편의를 위해 `Sample.java` 파일의 최상단에 작성하였다.

그리고 `Tiger`, `Lion` 클래스는 작성한 인터페이스를 구현하도록 다음과 같이 **`implements`** 라는 키워드를 사용해 수정하자.

```java
(... 생략 ...)

class Tiger extends Animal implements Predator {
}

class Lion extends Animal implements Predator {
}

(... 생략 ...)
```

이렇게 `Tiger`, `Lion` 클래스가 `Predator` 인터페이스를 구현하게 되면 `ZooKeeper` 클래스의 `feed` 메서드를 다음과 같이 변경할 수 있다.

**변경 전**

```java
(... 생략 ...)

class ZooKeeper {
    void feed(Tiger tiger) {
        System.out.println("feed apple");
    }

    void feed(Lion lion) {
        System.out.println("feed banana");
    }
}

(... 생략 ...)
```

**변경 후**

```java
(... 생략 ...)

class ZooKeeper {
    void feed(Predator predator) {
        System.out.println("feed apple");
    }
}

(... 생략 ...)
```

`feed` 메서드의 입력으로 `Tiger`, `Lion` 을 각각 필요로 했지만 이제 이것을 `Predator` 라는 인터페이스로 대체할 수 있게 되었다. `tiger`, `lion` 은 각각 `Tiger`, `Lion` 의 객체이기도 하지만 **`Predator` 인터페이스의 객체이기도 하기 때문에** 이와 같이 `Predator` 를 자료형으로 사용할 수 있는 것이다. 05-5절에서 공부했던 **IS-A** 관계가 인터페이스에도 적용된다. 즉, **'Tiger is a Predator', 'Lion is a Predator'** 가 성립된다.

- `tiger`: `Tiger` 클래스의 객체이자 `Predator` 인터페이스의 객체
- `lion`: `Lion` 클래스의 객체이자 `Predator` 인터페이스의 객체

다시 말해 `Tiger` 클래스로 만든 객체는 `Tiger` 객체이면서 동시에 `Predator` 객체로도 사용할 수 있다는 의미이다.

> 📎 이와 같이 객체가 1개 이상의 자료형 타입을 갖게 되는 특성을 **다형성(폴리모피즘)** 이라고 하는데 이는 05-08 절에서 자세히 다룬다.

이제 어떤 육식동물 클래스가 추가되더라도 `ZooKeeper` 는 `feed` 메서드를 추가할 필요가 없다. 다만 육식동물 클래스가 추가될 때마다 다음과 같이 `Predator` 인터페이스를 구현해야 한다.

> 📎 여기서 말하는 육식동물 클래스는 `Crocodile` 이나 `Leopard` 와 같이 육식 동물들의 이름을 한 클래스들을 말한다.

```java
class Crocodile extends Animal implements Predator {
}
```

> 📎 `Crocodile` 클래스는 실제 코드에 적용하지 말고 눈으로만 살펴보자.

**이제 왜 인터페이스가 필요한지 감을 잡았을 것이다.** 보통 중요 클래스(`ZooKeeper`)를 작성하는 시점에서는 클래스(`Animal`)의 구현체(`Tiger`, `Lion`)가 몇 개가 될지 알 수 없으므로 **인터페이스(`Predator`)를 정의하여 인터페이스를 기준으로 메서드(`feed`)를 만드는 것이 효율적이다.**

---

<a id="sec-0-7"></a>
### 0.7 인터페이스의 메서드

하지만 앞서 살펴본 `ZooKeeper` 클래스에 한 가지 문제가 있다. `ZooKeeper` 클래스의 `feed` 메서드를 보면 `tiger` 가 오든지, `lion` 이 오든지 무조건 `"feed apple"` 이라는 문자열을 출력한다. `tiger` 가 오면 `"feed apple"` 을 출력하는 것이 맞지만 `lion` 이 오면 `"feed banana"` 를 출력해야 한다.

```java
(... 생략 ...)

class ZooKeeper {
    public void feed(Predator predator) {
        System.out.println("feed apple");  // 항상 feed apple 만을 출력한다.
    }
}

(... 생략 ...)
```

실행 결과:

```
feed apple
feed apple
```

이번에도 인터페이스의 마법을 부려 보자. `Predator` 인터페이스에 다음과 같은 `getFood` 메서드를 추가해 보자.

```java
interface Predator {
    String getFood();
}

(... 생략 ...)
```

**여기서 주목할 점이 있다. 메서드에 몸통이 없다는 것이다.**

인터페이스의 메서드는 메서드의 이름과 입출력에 대한 정의만 있고 그 내용은 없다. 그 이유는 **인터페이스는 '규칙'이기 때문이다.** 즉, `getFood` 메서드는 인터페이스를 `implements` 한 클래스들이 **강제적으로 구현해야 하는 규칙** 이 된다.

이제 `Predator` 인터페이스에 `getFood` 메서드를 추가하면 `Tiger`, `Lion` 등의 `Predator` 인터페이스를 구현한 클래스에서 컴파일 오류가 발생할 것이다. 오류를 해결하려면 다음처럼 `Tiger`, `Lion` 클래스에 `getFood` 메서드를 구현해야 한다.

```java
(... 생략 ...)

class Tiger extends Animal implements Predator {
    public String getFood() {
        return "apple";
    }
}

class Lion extends Animal implements Predator {
    public String getFood() {
        return "banana";
    }
}

(... 생략 ...)
```

`Tiger`, `Lion` 클래스의 `getFood` 메서드는 각각 `apple` 과 `banana` 를 반환하게 했다.

> ⚠️ **인터페이스의 메서드는 항상 `public` 으로 구현해야 한다.**

이어서 `ZooKeeper` 클래스도 다음과 같이 변경이 가능하다.

```java
(... 생략 ...)

class ZooKeeper {
    void feed(Predator predator) {
        System.out.println("feed "+predator.getFood());
    }
}

(... 생략 ...)
```

`feed` 메서드가 `feed apple` 대신 `"feed "+predator.getFood()` 를 출력하도록 코드를 수정하였다. `predator.getFood()` 를 호출하면 **`Predator` 인터페이스를 구현한 구현체(`Tiger`, `Lion`)의 `getFood()` 메서드** 가 호출된다.

이제 프로그램을 실행해 보자. 원하던 대로 다음과 같은 결괏값이 출력되는 것을 확인할 수 있다.

```
feed apple
feed banana
```

---

<a id="sec-0-8"></a>
### 0.8 인터페이스 더 파고들기

> 🎯 **여기서는 왜 인터페이스가 필요한지를 이해하고 넘어가는 것이 가장 중요하다.**

동물(`Tiger`, `Lion`, `Crocodile` 등) 클래스의 종류만큼 `feed` 메서드가 필요했던 `ZooKeeper` 클래스를 `Predator` 인터페이스를 이용하여 구현했더니 **단 한 개의 `feed` 메서드로 구현이 가능해졌다.** 여기서 중요한 점은 단순히 메서드의 개수가 줄어들었다는 것이 아니다. **`ZooKeeper` 클래스가 특정 동물 클래스에 의존하던 코드에서 동물 클래스와 무관한 독립적인 코드로 변했다는 것이 핵심이다.** 바로 이 점이 **인터페이스의 가장 중요한 장점이다.**

#### 🔌 비유 — USB 포트

이번에는 좀 더 개념적으로 인터페이스를 생각해 보자. 아마도 여러분은 컴퓨터의 **USB 포트** 를 잘 알고 있을 것이다. USB 포트에 연결할 수 있는 기기는 하드디스크, 메모리 스틱, 스마트폰 등 무척 다양하다. 바로 이 USB 포트가 **물리적 세계의 인터페이스** 라고 할 수 있다.

USB 포트의 규격만 알면 어떤 기기도 연결할 수 있다. 또, 컴퓨터는 **USB 포트만 제공하고 어떤 기기가 연결되는지 신경 쓸 필요가 없다.** 바로 이 점이 자바의 인터페이스와 매우 비슷하다. 앞서 만든 `ZooKeeper` 클래스가 **어떤 동물 클래스(`Tiger`, `Lion`, …)이든 상관하지 않고 `feed` 메서드를 구현한 것처럼** 말이다.

| 물리적 세계 | 자바 세계 |
| --- | --- |
| 컴퓨터 | `ZooKeeper` |
| USB 포트 | `Predator` |
| 하드디스크, 메모리 스틱, 스마트폰, … | `Tiger`, `Lion`, `Crocodile`, … |

> 📌 **상속과 인터페이스**
>
> `Predator` 인터페이스 대신 `Animal` 클래스에 `getFood` 메서드를 추가하고 `Tiger`, `Lion` 등에서 `getFood` 메서드를 오버라이딩한 후 `Zookeeper` 의 `feed` 메서드가 `Predator` 대신 `Animal` 을 입력 자료형으로 사용해도 동일한 효과를 거둘 수 있다. 하지만 상속은 자식 클래스가 부모 클래스의 메서드를 오버라이딩하지 않고 사용할 수 있기 때문에 **해당 메서드를 반드시 구현해야 한다는 '강제성'을 갖지 못한다.** 그래서 상황에 맞게 상속을 사용할 것인지, 인터페이스를 사용해야 할지를 결정해야 한다. **인터페이스는 인터페이스의 메서드를 반드시 구현해야 하는 강제성을 갖는다는 점을 반드시 기억하자.**

---

<a id="sec-0-9"></a>
### 0.9 디폴트 메서드

자바 8 버전 이후부터는 **디폴트 메서드(default method)** 를 사용할 수 있다. 인터페이스의 메서드는 구현체를 가질 수 없지만 디폴트 메서드를 사용하면 **실제 구현된 형태의 메서드** 를 가질 수 있다. 예를 들어 `Predator` 인터페이스에 다음과 같은 디폴트 메서드를 추가할 수 있다.

```java
interface Predator {
    String getFood();

    default void printFood() {
        System.out.printf("my food is %s\n", getFood());
    }
}
```

디폴트 메서드는 메서드명 가장 앞에 **`default`** 라고 표기해야 한다. 이렇게 `Predator` 인터페이스에 `printFood` 디폴트 메서드를 구현하면 `Predator` 인터페이스를 구현한 `Tiger`, `Lion` 등의 실제 클래스는 `printFood` 메서드를 **구현하지 않아도 사용할 수 있다.** 그리고 디폴트 메서드는 **오버라이딩이 가능하다.** 즉, `printFood` 메서드를 실제 클래스에서 다르게 구현하여 사용할 수 있다.

**디폴트 메서드는 기존 인터페이스에 새로운 기능을 추가해야 할 때 매우 유용하다.** 디폴트 메서드를 사용하면 기존에 인터페이스를 구현한 모든 클래스를 수정하지 않고도 새로운 메서드를 추가할 수 있기 때문이다.

---

#### 🧠 잠깐 — "인터페이스를 이용한 이 과정이 결국은 DIP 아닌가?"

> 학생이 0.5~0.8 의 흐름을 보고 던지기 쉬운 통찰 질문입니다.
> **정확히 맞습니다.** 박응용 선생님은 0.5~0.8 절에서 **DIP(의존 역전 원칙)** 라는 단어를 한 번도 쓰지 않지만, 사실 그 모든 과정은 **DIP 의 교과서적 예시** 입니다. 한 단계씩 매핑해 봅시다.

##### (1) ZooKeeper / Predator 예제를 DIP 언어로 다시 쓰면

DIP 의 원문 정의 (Robert C. Martin, 1996):

> "**상위 모듈은 하위 모듈에 의존해서는 안 된다. 둘 다 추상화에 의존해야 한다.**
> **추상화는 세부 사항에 의존해서는 안 된다. 세부 사항이 추상화에 의존해야 한다.**"

이걸 0.5~0.7 의 코드에 그대로 매핑:

| DIP 용어 | 0.5~0.7 의 실제 코드 |
| --- | --- |
| **상위 모듈** (정책·중요 비즈니스) | `ZooKeeper` |
| **하위 모듈** (구체 세부) | `Tiger`, `Lion`, `Crocodile`, `Leopard`, … |
| **추상화** (계약·역할) | `Predator` 인터페이스 |
| **세부 사항이 추상화에 의존** | `Tiger implements Predator`, `Lion implements Predator` |

##### (2) 의존 화살표가 진짜 "역전" 되는 모습

**0.5 절 — DIP 위반 (수직 의존, 자연스럽지만 깨지기 쉬움)**

```
         ZooKeeper
            │  ← "Tiger 와 Lion 을 직접 안다"
            ├──────▶ Tiger
            └──────▶ Lion
                    (새 동물 추가될 때마다 ZooKeeper 코드 수정 필요)
```

`ZooKeeper.feed(Tiger)`, `ZooKeeper.feed(Lion)` 처럼 **메서드 시그니처에 구체 타입 이름이 박혀 있음** → 상위(`ZooKeeper`)가 하위(`Tiger`, `Lion`)에 직접 의존.

**0.6~0.7 절 — DIP 적용 후 (의존 화살표 역전)**

```
         ZooKeeper
            │
            ▼
        Predator (인터페이스)
            ▲
            ├──────── Tiger     ← "내가 Predator 를 따른다"
            └──────── Lion         하위가 위로 의존
```

이제:
- `ZooKeeper.feed(Predator predator)` — **상위는 추상에만 의존**
- `class Tiger implements Predator` — **하위가 추상으로 (위로) 의존**

→ 박응용 선생님이 0.8 에서 강조하신 다음 문장이 곧 **DIP 의 정의** 입니다:

> 💬 "`ZooKeeper` 클래스가 특정 동물 클래스에 **의존하던** 코드에서 동물 클래스와 무관한 **독립적인** 코드로 변했다는 것이 핵심이다."

이 문장만 봐도 — "의존하던 → 독립적인" — 정확히 DIP 의 효과를 표현하고 있습니다.

##### (3) "역전" 의 두 번째 의미도 이미 일어났다 — 인터페이스 소유권

DIP 가 "역전" 인 두 번째 이유는 **인터페이스를 누가 정의하는가** 입니다.

| 전통적 흐름 | 박응용 예제의 흐름 (DIP) |
| --- | --- |
| `Tiger`, `Lion` 이 자기 API 를 정의 → `ZooKeeper` 가 그것에 맞춰 호출 | **`ZooKeeper` 의 입장에서 "나는 `Predator` 한 가지 역할만 받겠다" 라고 `Predator` 인터페이스를 정의** → `Tiger`, `Lion` 이 거기에 맞춰 구현 |

박응용 선생님이 0.6 끝에서 말한 그 문장:

> 💬 "보통 **중요 클래스(`ZooKeeper`)를 작성하는 시점** 에서는 클래스의 구현체(`Tiger`, `Lion`)가 몇 개가 될지 알 수 없으므로 **인터페이스(`Predator`)를 정의하여** 인터페이스를 기준으로 메서드(`feed`)를 만드는 것이 효율적이다."

→ 이게 정확히 **"상위 모듈(중요 클래스)이 인터페이스의 소유권을 갖는다"** 는 DIP 의 핵심을 말씀하신 겁니다. 이름만 안 붙였을 뿐.

##### (4) 그런데 — 아직 DIP "절반" 만 일어났다

여기서 멈추면 DIP 의 절반만 한 셈입니다. 0.7 코드를 다시 보면:

```java
public class Sample {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = new ZooKeeper();
        Tiger tiger = new Tiger();        // ← 누군가는 여전히 new Tiger() 를 한다
        Lion lion = new Lion();           // ← 누군가는 여전히 new Lion() 을 한다
        zooKeeper.feed(tiger);
        zooKeeper.feed(lion);
    }
}
```

`ZooKeeper` 본체는 깔끔해졌지만, **`main` 에 `new Tiger()`, `new Lion()` 이 그대로 박혀 있습니다.** 이 줄들은 **누가 어떤 구체 클래스를 만들 것인가** 를 여전히 우리 손으로 결정하고 있다는 뜻입니다.

이 마지막 책임을 **컨테이너(프레임워크)에 넘기는 단계** 가 바로 **IoC(제어의 역전) + DI(의존성 주입)** 입니다.

| 단계 | 무엇이 일어났나 | 어디서 배우나 |
| --- | --- | --- |
| **1단계 — DIP** | `ZooKeeper` 가 `Predator` 인터페이스에만 의존하도록 설계 변경 | **0.5~0.8 (이미 완료)** |
| **2단계 — IoC / DI** | `new Tiger()`, `new Lion()` 도 컨테이너가 대신 해주고, `ZooKeeper` 에 자동 주입 | 1.1, 1.2, 그리고 7장 `MyContainer` |
| **3단계 — OCP 자동 달성** | 새 `Crocodile` 이 들어와도 `main` 한 줄도 안 고침. `Crocodile implements Predator` 만 추가하면 됨 | 1.2.7 의 "DIP + IoC = OCP" |

> 💡 그래서 1.1 절 도입부에서 **"DIP 가 '어떻게 설계할까' 의 원칙이라면, IoC 는 '그 설계를 어떻게 굴릴까' 의 메커니즘"** 이라고 정리한 겁니다. 0.5~0.8 이 **설계(DIP)** 까지를, 본 강의 1장~7장이 **운영(IoC)** 까지를 책임집니다.

##### (4-A) 2단계 — IoC / DI 를 코드로 깊이 보기

> 🎯 이 절에서는 0.7 의 ZooKeeper 코드를 **세 번에 걸쳐 점진적으로 리팩터링** 하면서, "new 를 어디로 옮기는가" 가 곧 IoC 의 본질임을 직접 눈으로 확인합니다.
> 모든 코드는 `~/myframework/chapter_0/Sample.java` 한 파일에 그대로 붙여넣고 `javac Sample.java && java Sample` 로 실행할 수 있습니다.

###### Step 0 — 출발점 (DIP 만 적용된 0.7 상태)

```java
interface Predator {
    String getFood();
}

class Animal {
    String name;
    void setName(String name) { this.name = name; }
}

class Tiger extends Animal implements Predator {
    @Override public String getFood() { return "apple"; }
}

class Lion extends Animal implements Predator {
    @Override public String getFood() { return "banana"; }
}

class ZooKeeper {
    void feed(Predator predator) {
        System.out.println("feed " + predator.getFood());
    }
}

public class Sample {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = new ZooKeeper();   // ⚠️ main 이 직접 만든다
        Tiger tiger = new Tiger();               // ⚠️ main 이 직접 만든다
        Lion lion = new Lion();                  // ⚠️ main 이 직접 만든다

        zooKeeper.feed(tiger);
        zooKeeper.feed(lion);
    }
}
```

실행:
```
feed apple
feed banana
```

문제는 보이는 그대로입니다. **`main` 이 모든 객체 생성을 떠맡고 있고**, 새 동물이 추가되면 `main` 도 함께 수정됩니다. "**누가, 언제, 어떤 구체 클래스로** 객체를 만들지" 의 제어권이 100% 개발자 손에 있는 상태 — 이게 바로 IoC 가 적용되지 않은 모습입니다.

###### Step 1 — **수동 DI (생성자 주입)**: ZooKeeper 가 자기 의존성을 외부에서 받기

가장 작은 한 걸음입니다. `ZooKeeper` 가 자기에게 필요한 `Predator` 목록을 **생성자에서 받아 보관** 하고, 한 번에 모두에게 `feed` 합니다.

```java
import java.util.List;

interface Predator { String getFood(); }

class Tiger implements Predator {
    @Override public String getFood() { return "apple"; }
}

class Lion implements Predator {
    @Override public String getFood() { return "banana"; }
}

class ZooKeeper {
    private final List<Predator> predators;   // ① 외부에서 받은 의존성을 보관하는 변수 predators를 선언

    public ZooKeeper(List<Predator> predators) {   // ② 생성자 매개변수로 predators 를 주입받음
        this.predators = predators;               // ③ 받은 의존성을 필드에 저장 (this 로 자기 객체의 필드를 지칭)
    }

    void feedAll() {
        for (Predator p : predators) {
            System.out.println("feed " + p.getFood());
        }
    }
}

public class Sample {
    public static void main(String[] args) {
        // main 은 "조립 책임자" 역할만 한다 — 어떤 동물을 ZooKeeper 에게 줄지 결정
        ZooKeeper zooKeeper = new ZooKeeper(List.of(new Tiger(), new Lion()));
        zooKeeper.feedAll();
    }
}
```

**무엇이 달라졌나** — 위 코드의 ①②③ 주석을 한 줄씩 짚어 봅시다:

- **① 필드 선언** (`private final List<Predator> predators;`)
  - `ZooKeeper` 가 자기에게 필요한 의존성을 **`predators` 라는 필드로 보관** 합니다. `final` 이라 한 번 들어오면 못 바꿈 → 안전.
- **② 생성자 주입** (`public ZooKeeper(List<Predator> predators)`)
  - 의존성이 **밖에서 매개변수로 들어옵니다(injected)** → 이게 바로 **DI(Dependency Injection)**.
  - `ZooKeeper` 안에 `new Tiger()`, `new Lion()` 이 **한 글자도 없습니다.** `ZooKeeper` 는 이제 "동물을 어떻게 만드는가" 를 모릅니다.
- **③ 필드에 저장** (`this.predators = predators;`)
  - 생성자 매개변수(`predators`)를 **자기 객체(`this`)의 필드** 에 저장 → 이 순간부터 `ZooKeeper` 객체는 평생 자기 동물 목록을 들고 다닙니다.
  - 매개변수 이름과 필드 이름이 똑같이 `predators` 라서 **`this.` 가 필수** 입니다 (안 붙이면 매개변수에 자기 자신을 대입하는 무의미한 코드가 됨).

> 👉 그 결과, **누가 어떤 동물을 넣을지** 결정하는 책임은 `ZooKeeper` 가 아니라 **`main` (= 조립 책임자)** 으로 옮겨졌습니다. `ZooKeeper` 본문에서는 "어떤 구체 동물" 이라는 개념이 사라졌습니다 — 이것이 DI 의 효과입니다.

**테스트하기가 갑자기 쉬워졌다는 점** 에 주목하세요:

```java
// 가짜(Mock) Predator 를 넣어 ZooKeeper 의 동작만 검증할 수 있다
ZooKeeper zk = new ZooKeeper(List.of(() -> "mock-food"));   // 람다로 즉석 구현체
zk.feedAll();   // → "feed mock-food"
```

> 🔰 **잠깐 — 위 한 줄 안에 처음 보는 두 가지: `List.of(...)` 와 `() -> "mock-food"`**
>
> 위 한 줄에는 초보자가 처음 보는 자바 문법 두 가지가 함께 등장합니다. 하나씩 풀어봅시다.
>
> ##### ① `List.of(...)` — "그 자리에서 바로 만드는 읽기 전용 List"
>
> ```java
> List.of("a", "b", "c")    // → 문자열 3개가 든 List
> List.of(new Tiger())      // → Tiger 1개가 든 List
> List.of()                 // → 빈 List
> ```
>
> - `List.of` 는 **자바 9 부터 추가된 짧은 List 생성 문법** 입니다.
> - 괄호 안에 원소를 콤마로 나열하면 그 원소들이 들어 있는 `List` 가 만들어집니다.
> - 비유하자면 **"즉석에서 봉투를 만들어 안에 물건을 담아 건네주는" 한 줄짜리 표현** 입니다.
>
> **기존 방식과 비교**:
>
> ```java
> // (가) 옛날 스타일 — 3줄
> List<String> names = new ArrayList<>();
> names.add("a");
> names.add("b");
>
> // (나) List.of — 한 줄
> List<String> names = List.of("a", "b");
> ```
>
> 🔒 한 가지 주의: `List.of` 로 만든 List 는 **수정 불가(immutable)** 입니다. 나중에 `names.add(...)` 같이 추가하려 하면 에러가 납니다. **"한 번 만들고 그대로 쓰는 데이터"** 에 적합합니다 — 우리 예제처럼 "ZooKeeper 에 한 번 넘기고 끝" 인 경우에 딱 맞습니다.
>
> ##### ② `() -> "mock-food"` — 람다(lambda): "이름 없는 미니 함수"
>
> ```java
> () -> "mock-food"
> ```
>
> 이게 바로 **람다 표현식(lambda expression)** 입니다. 한마디로 **"이름 없이 그 자리에서 만들어 쓰는 미니 함수"** 입니다.
>
> 세 조각으로 쪼개면:
>
> | 조각 | 의미 |
> | --- | --- |
> | `()` | 매개변수 목록 — 비어 있음 = "입력값 없음" |
> | `->` | 람다 화살표 — "왼쪽의 입력을 받아 → 오른쪽의 동작을 한다" |
> | `"mock-food"` | 본문 — 그 함수의 결과(반환값). `return` 을 생략한 형태 |
>
> 즉 위 람다는 **"입력은 없고, 호출되면 항상 `"mock-food"` 라는 문자열을 돌려주는 함수"** 입니다.
>
> ##### ③ 그런데 왜 자바가 이 람다를 `Predator` 로 받아주나? — **함수형 인터페이스**
>
> 우리의 `Predator` 인터페이스를 다시 보면:
>
> ```java
> interface Predator {
>     String getFood();   // 추상 메서드 단 1개
> }
> ```
>
> **추상 메서드가 정확히 1개뿐인 인터페이스** 를 자바는 **함수형 인터페이스(functional interface)** 라고 부릅니다. 이런 인터페이스가 필요한 자리에는 **람다 한 줄을 그 메서드의 구현으로 자동 인식** 해줍니다.
>
> 그래서 자바는 우리 코드의 람다 `() -> "mock-food"` 를 다음과 같이 **읽어줍니다**:
>
> ```java
> new Predator() {
>     @Override
>     public String getFood() {
>         return "mock-food";
>     }
> }
> ```
>
> 위 6줄을 람다 한 줄로 줄여 쓴 셈이지요.
>
> ##### ④ 그래서 전체 한 줄은 결국 이런 뜻
>
> ```java
> ZooKeeper zk = new ZooKeeper(List.of(() -> "mock-food"));
> ```
>
> 풀어 쓰면:
>
> ```java
> // (1) "getFood() 가 호출되면 'mock-food' 를 돌려주는" 가짜 Predator 한 개를 즉석에서 만들고
> Predator fake = new Predator() {
>     @Override public String getFood() { return "mock-food"; }
> };
>
> // (2) 그 가짜 하나를 담은 List 를 만들어
> List<Predator> list = List.of(fake);
>
> // (3) ZooKeeper 에게 주입해서 인스턴스를 생성한다
> ZooKeeper zk = new ZooKeeper(list);
> ```
>
> 9 줄짜리 의도를 **딱 1 줄로 표현** 한 것이 람다 + `List.of` 의 매력입니다.
>
> 📦 **테스트 측면의 의미**: 진짜 `Tiger`, `Lion` 클래스를 만들 필요도 없이, 그 자리에서 "동작만 흉내내는 가짜 Predator" 를 끼워 넣어 `ZooKeeper` 가 제대로 동작하는지 확인할 수 있습니다. 이것이 **"DI 가 적용된 코드는 테스트가 쉽다"** 라는 말의 실체입니다.

> 🧠 **한 걸음 더 — 그래서 "수정 불가(immutable)" 가 왜 좋은 거지? 그리고 흔한 오해 한 가지**
>
> 위에서 `List.of` 가 immutable 이라는 점만 짚었는데, **"그게 왜 좋은 건데?"** 가 자연스러운 질문입니다. 또한 자바를 처음 배우는 학생들이 **자주 빠지는 한 가지 오해** 부터 먼저 정리하고 본론으로 가겠습니다.
>
> ##### 🚨 흔한 오해 먼저 짚고 가기 — "immutable 은 스택에 만들어져서 그런 거 아닌가?"
>
> 학생들이 자주 떠올리는 추측입니다: *"immutable 객체는 스택에 만들어지고, 새 값을 대입하면 새 객체를 만들고 옛것은 GC 가 없애는 것 아닌가?"* 결론부터 — **반은 맞고 반은 틀립니다.**
>
> 두 개의 서로 다른 분류 기준이 머릿속에서 합쳐졌기 때문입니다.
>
> | 축 | 종류 | 차이 |
> | --- | --- | --- |
> | **축 A — 변수가 무엇을 담는가** | 기본형(primitive) vs 참조형(reference) | `int`, `double` 같은 기본형은 변수 자체에 값을 담음. 객체는 변수에 **주소(참조) 만** 담음. |
> | **축 B — 객체의 상태가 바뀔 수 있는가** | 가변(mutable) vs 불변(immutable) | 객체가 만들어진 뒤 내부 필드를 바꿀 수 있는가의 문제. |
>
> 이 두 축은 **완전히 독립** 입니다. `String`, `List.of(...)` 결과는 **참조형 + 불변** 입니다. "불변이라서 기본형 같다" 는 잘못된 짝짓기입니다.
>
> | 예시 | 축 A | 축 B |
> | --- | --- | --- |
> | `int x = 5;` | 기본형 | (해당 없음) |
> | `String s = "hi";` | **참조형** | **불변** ← 참조형이면서 불변! |
> | `new ArrayList<>()` | 참조형 | 가변 |
> | `List.of("a")` 결과 | 참조형 | 불변 |
>
> ##### 📍 그래서 immutable 객체는 어디에 만들어지나? — **항상 힙(heap)**
>
> 자바 객체는 mutable 이든 immutable 이든 **모두 힙에 만들어집니다.** "불변이라서 스택" 이라는 개념은 자바에 없습니다.
>
> ```
> 실행:  String s = "hello";
>        s = "world";
>
> [스택]                            [힙]
> ┌─────────────────┐          ┌────────────────┐
> │ s : 주소 0x100  │ ────▶    │  "hello"       │  ← 처음에 가리키던 객체
> └─────────────────┘          └────────────────┘
>         │
>         │ s = "world" 실행 후
>         ▼
> ┌─────────────────┐          ┌────────────────┐
> │ s : 주소 0x200  │ ────▶    │  "world"       │  ← 새로 만들어진 객체
> └─────────────────┘          └────────────────┘
>                              ┌────────────────┐
>                              │  "hello"       │  ← 이제 아무도 안 가리킴
>                              └────────────────┘    → GC 대상
> ```
>
> 학생 추측의 ✅ **맞는 부분** — "새 객체를 만들어 대입한다 + 옛 객체는 GC 된다" 는 정확합니다.
> 학생 추측의 ❌ **틀린 부분** — 그 이유가 "스택" 이나 "참조형이 아니라서" 가 아닙니다. **객체의 클래스가 변경 메서드를 거부하도록 만들어졌기 때문에**, 변경처럼 보이는 동작은 새 객체를 만들 수밖에 없는 것입니다.
>
> 가변/불변의 진짜 차이는 **상태 변경 요청에 대한 반응** 입니다:
>
> ```java
> // (A) 가변 — 같은 객체의 내부가 바뀜. 주소 변화 없음
> StringBuilder sb = new StringBuilder("hello");
> sb.append(" world");   // sb 가 가리키는 그 객체 본체가 직접 수정됨
>
> // (B) 불변 — 새 객체를 만들어 변수 주소를 갈아끼움
> String s = "hello";
> s = s + " world";      // 새 "hello world" 객체 생성, s 가 그쪽을 가리키도록 갱신
>                        // 옛 "hello" 는 참조 0개 → GC 대상
> ```
>
> ##### 🎯 본론 — 그래서 **불변(immutable) 이 왜 필요한가? 6가지 이유**
>
> 이게 가장 중요한 부분입니다. "왜 그냥 가변으로 쓰지 않고 굳이 불변을 만들까?" 의 답:
>
> ###### ① **예측 가능성** — "한 번 만들면 영원히 그 값"
>
> ```java
> String s = "hello";
> someMethod(s);                // 이 메서드 안에서 s 가 바뀌었을까?
> System.out.println(s);        // String 은 불변이라 무조건 "hello" — 의심할 필요 없음
> ```
>
> 가변 객체면 메서드에 넘기는 순간 **"안에서 누가 내 객체를 몰래 바꿨을지" 를 항상 의심해야 합니다.** 불변은 이 의심을 0으로 만듭니다.
>
> ###### ② **스레드 안전(thread-safe) — 무료로**
>
> 멀티스레드 프로그램에서 여러 스레드가 같은 객체를 동시에 읽고 쓰면 데이터가 깨집니다. 막으려면 `synchronized`, `Lock` 등이 필요하고 **버그가 가장 많이 발생하는 영역** 입니다.
>
> 불변 객체는 **변경 자체가 불가능하므로** 여러 스레드가 동시에 읽어도 **절대 깨질 일이 없습니다.** "동기화 코드 없이 공유 가능" — 이게 String 이 그렇게 널리 쓰이는 가장 큰 이유 중 하나입니다.
>
> ###### ③ **HashMap 의 key 로 안전하게 쓸 수 있음**
>
> ```java
> Map<String, Integer> scores = new HashMap<>();
> scores.put("alice", 100);
> // ... 나중에 ...
> Integer s = scores.get("alice");   // 100 — 정상
> ```
>
> 만약 key 가 가변이라면, 한번 put 한 뒤 그 key 객체의 내부가 바뀌어 버리면 **HashMap 의 해시값이 어긋나서 값을 영영 못 찾게 됩니다.** 그래서 자바는 `String`, `Integer` 같은 **불변 타입을 key 로 권장** 합니다.
>
> ###### ④ **방어적 복사(defensive copy) 불필요 → 성능**
>
> 가변 객체를 외부에 넘기면 외부가 몰래 바꿀까봐 **복사본을 만들어 넘기는 게 안전** 합니다.
>
> ```java
> // 가변이면…
> public List<String> getNames() {
>     return new ArrayList<>(this.names);   // 매번 복사. 비용 발생
> }
>
> // 불변이면…
> public List<String> getNames() {
>     return this.names;   // 그냥 넘겨도 안전. 복사 비용 0
> }
> ```
>
> 호출이 잦으면 이 차이가 누적되어 성능과 메모리에 큰 영향을 줍니다.
>
> ###### ⑤ **캐싱(caching) / 공유 가능** — String pool 이 가능한 이유
>
> 같은 값이 여러 곳에서 필요할 때, 불변이면 **하나만 만들어 공유** 해도 안전합니다.
>
> ```java
> String s1 = "hello";   // String pool 에 "hello" 가 만들어짐
> String s2 = "hello";   // 같은 pool 의 "hello" 를 재사용 — 새 객체 안 만듦
> System.out.println(s1 == s2);   // true — 진짜 같은 객체!
> ```
>
> 자바가 이 최적화를 할 수 있는 것은 String 이 불변이라 "공유해도 누가 망가뜨릴 일이 없다" 는 보장이 있기 때문입니다.
>
> ###### ⑥ **버그가 줄어든다 — "부수효과(side effect) 의 봉쇄"**
>
> 함수형 프로그래밍의 핵심 통찰입니다. 객체가 바뀌지 않으면 **"왜 갑자기 이 값이 이렇게 되어 있지?" 류의 버그가 원천 차단** 됩니다. 디버깅이 극적으로 쉬워집니다.
>
> ##### 🤝 그럼 모든 걸 불변으로? — 적재적소
>
> | 불변이 어울리는 경우 | 가변이 어울리는 경우 |
> | --- | --- |
> | 한 번 만들고 그대로 쓰는 데이터 (설정값, 식별자, 좌표, …) | 자주 추가/삭제되는 컬렉션 (장바구니, 로그 버퍼) |
> | 여러 스레드가 공유해야 함 | 단일 스레드 안의 임시 작업 데이터 |
> | HashMap/HashSet 의 key | 점진적으로 채워 나가는 빌더 객체 |
> | API 의 입출력 — "건드리지 마라" 를 코드로 보장 | 성능이 극단적으로 중요해 in-place 수정이 필수 |
>
> 일반적인 권장: **"기본은 불변, 필요할 때만 가변"** 이 현대 자바·코틀린·Rust 의 흐름입니다.
>
> ##### 🔬 직접 확인 — `~/myframework/chapter_0/Sample.java`
>
> "불변은 새 객체를 만들고, 가변은 같은 객체를 수정한다" 를 눈으로 확인:
>
> ```java
> public class Sample {
>     public static void main(String[] args) {
>         // (A) 불변 — 새 객체가 생기는지 객체 id 로 확인
>         String s = "hello";
>         System.out.println("s 의 객체 id: " + System.identityHashCode(s));
>         s = s + " world";
>         System.out.println("s 의 객체 id: " + System.identityHashCode(s));
>         //  └─ 두 id 가 다르면 → 새 객체가 만들어진 것!
>
>         // (B) 가변 — 같은 객체가 유지되는지 확인
>         StringBuilder sb = new StringBuilder("hello");
>         System.out.println("sb 의 객체 id: " + System.identityHashCode(sb));
>         sb.append(" world");
>         System.out.println("sb 의 객체 id: " + System.identityHashCode(sb));
>         //  └─ 두 id 가 같으면 → 같은 객체 내부만 수정된 것!
>     }
> }
> ```
>
> 실행 결과 (id 숫자는 실행마다 다름):
> ```
> s 의 객체 id: 1735600054     ← 처음의 "hello"
> s 의 객체 id: 21685669       ← "+" 후 새 객체 ("hello world")
> sb 의 객체 id: 2133927002    ← 처음의 StringBuilder
> sb 의 객체 id: 2133927002    ← append 후도 "동일" 객체!
> ```
>
> ##### 🔗 본 강의 코드와의 연결
>
> - 우리가 위에서 쓴 `List.of(() -> "mock-food")` — `ZooKeeper` 가 받은 동물 목록은 **누구도 (의도치 않게) 바꿀 수 없음** 이 보장됩니다. `ZooKeeper.feedAll()` 이 도는 중에 외부가 목록을 흔들 일이 없다는 뜻 → 버그 봉쇄.
> - 1.2 의 `PaymentService` 도 `Map<String, Pay>` 를 `@MyPostConstruct` 에서 한 번 만든 뒤 절대 수정하지 않습니다. 같은 패턴.
> - 7장 `MyContainer` 가 Bean 목록을 외부에 노출할 때, **`Collections.unmodifiableMap(...)` 으로 감싸 immutable 처럼 만드는 패턴** 이 자주 등장하는 이유도 위 6가지 가치를 얻기 위함입니다.
>
> ##### ✅ 한 줄 정리표
>
> | 질문 | 답 |
> | --- | --- |
> | 불변 객체는 스택에 있나? | ❌ **아니요. 모든 자바 객체는 힙.** 스택에는 (지역) 변수의 주소만. |
> | 참조형이 아닌가? | ❌ String/List.of 결과 모두 **참조형 + 불변** . 두 축은 독립. |
> | 새 값 대입 시 새 객체 생성 + 옛 객체 GC? | ✅ 정확. 단, 이유는 "스택이라서" 가 아니라 **"클래스가 변경을 거부하도록 만들어져서"**. |
> | 그래서 왜 불변이 좋은가? | **예측 가능성 / 스레드 안전 / HashMap key / 복사 불필요 / 캐싱 / 부수효과 봉쇄** — 6가지 가치를 동시에 얻음. |
>
> 👉 핵심: **위치(스택/힙) 와 변경 가능성(가변/불변) 은 서로 다른 차원** 입니다.
> 자바에서 **모든 객체는 힙에 살고**, 불변성은 **그 객체의 클래스가 변경 메서드를 거부하도록 만들어졌기 때문에** 생깁니다.
> 이걸 활용해 얻는 6가지 가치(예측·스레드·key·복사·캐시·부수효과 봉쇄)가 **현대 자바·코틀린·Rust 가 점점 더 불변을 기본으로 두는 이유** 입니다.

> 💡 **수동 DI 만으로도 큰 진전입니다.** 하지만 `main` 안에는 여전히 `new Tiger()`, `new Lion()` 이 박혀 있습니다. 새 동물이 추가되면 `main` 도 고쳐야 합니다 — 즉, **OCP 는 아직 절반만 달성**.

###### Step 2 — **미니 컨테이너**: `new` 책임까지 외부로 빼기

이번에는 객체 생성 자체를 **`AnimalContainer` 라는 별도 객체** 에게 맡깁니다. `main` 은 "컨테이너 켜라" 정도만 합니다.

```java
import java.util.ArrayList;
import java.util.List;

interface Predator { String getFood(); }

class Tiger implements Predator {
    @Override public String getFood() { return "apple"; }
}

class Lion implements Predator {
    @Override public String getFood() { return "banana"; }
}

class ZooKeeper {
    private final List<Predator> predators;
    public ZooKeeper(List<Predator> predators) { this.predators = predators; }

    void feedAll() {
        for (Predator p : predators) System.out.println("feed " + p.getFood());
    }
}

// 🎯 미니 IoC 컨테이너 — 객체 생성과 조립을 전담
class AnimalContainer {
    private final List<Predator> predators = new ArrayList<>();
    private ZooKeeper zooKeeper;

    public void init() {
        // ① 모든 Predator 구현체를 직접 new 한다 (= 이 책임이 main 에서 컨테이너로 이동!)
        predators.add(new Tiger());
        predators.add(new Lion());

        // ② 만들어진 객체들을 ZooKeeper 에게 주입한다 (= DI)
        zooKeeper = new ZooKeeper(predators);
    }

    public ZooKeeper getZooKeeper() { return zooKeeper; }
}

public class Sample {
    public static void main(String[] args) {
        AnimalContainer container = new AnimalContainer();
        container.init();                          // 컨테이너에게 조립을 부탁
        ZooKeeper zk = container.getZooKeeper();   // 완성된 ZooKeeper 를 받아옴
        zk.feedAll();
    }
}
```

실행:
```
feed apple
feed banana
```

**이제 일어난 일을 분명히 봅시다:**

| 책임 | Step 0 | Step 1 | **Step 2** |
| --- | --- | --- | --- |
| `Predator` 인터페이스로 추상화 | ✅ (이미 0.6에서) | ✅ | ✅ |
| `ZooKeeper` 가 구체 동물을 모름 | ✅ | ✅ | ✅ |
| `new Tiger()` 위치 | `main` | `main` | **`AnimalContainer`** |
| `main` 이 동물 종류를 앎? | 예 (직접 new) | 예 (직접 new) | **❌ 모름** |
| 새 동물 추가 시 고칠 곳 | `main` + 어디든 | `main` | **`AnimalContainer` 한 곳만** |

> 🎯 **이게 바로 "제어의 역전(Inversion of Control)" 의 핵심입니다.**
> Step 0/1 에서는 **내(main) 가 컨테이너를 부르고 객체를 만들었**다면, Step 2 에서는 **컨테이너가 객체를 만들어서 내(`ZooKeeper`)에게 던져 줍니다.**
> 비유로 자주 쓰는 헐리우드 원칙: **"Don't call us, we'll call you."**

###### Step 3 — 어노테이션이 더해지면? (본 강의 7장의 미리보기)

위 `AnimalContainer.init()` 안에는 여전히 `predators.add(new Tiger())` 같은 줄이 **사람 손으로** 적혀 있습니다. **이 마지막 한 줄까지 자동화** 하는 게 본 강의 4~7장에서 만들 `MyContainer` 입니다.

```java
@MyComponent
class Tiger implements Predator { ... }

@MyComponent
class Lion implements Predator { ... }

@MyComponent
class ZooKeeper {
    @MyInject private List<Predator> predators;   // 컨테이너가 자동으로 채워줌
    ...
}

// main 은 이게 전부:
MyContainer ctx = new MyContainer("com.example");
ZooKeeper zk = ctx.getBean(ZooKeeper.class);
zk.feedAll();
```

- `@MyComponent` 가 붙은 클래스를 **컨테이너가 스캔해서 자동으로 `new`** — Step 2 의 `predators.add(new Tiger())` 줄이 **사라집니다.**
- `@MyInject` 가 붙은 필드를 **컨테이너가 리플렉션으로 채워줌** — Step 1·2 의 생성자 인자 전달이 **사라집니다.**
- **결과적으로 `main` 에 더는 어떤 동물의 이름도 등장하지 않습니다.**

> 👉 그래서 본 강의가 "DI 컨테이너" 라는 단어를 강조하는 겁니다. **Step 1 = DI, Step 2 = IoC 컨테이너, Step 3 = 어노테이션 기반 자동 IoC 컨테이너(= Spring 스타일)**. 셋 다 같은 방향의 다른 깊이일 뿐입니다.

---

##### (4-B) 3단계 — OCP 가 자동으로 달성되는 모습

> 🎯 이 절은 짧지만 가장 통쾌한 부분입니다. 위 Step 2 코드를 만들어 둔 상태에서 **새 육식동물 `Crocodile`** 을 추가하는 실험을 해 봅니다.
> "기존 코드를 한 줄도 안 고치고 새 기능이 들어간다" 는 **OCP(개방-폐쇄 원칙)** 의 정의가 코드로 어떻게 나타나는지 직접 확인할 수 있습니다.

###### BEFORE — DIP 도 IoC 도 없는 세계에서 Crocodile 을 추가하면?

(0.5 절 초반의 `ZooKeeper` 를 떠올려 보세요.)

```java
class ZooKeeper {
    void feed(Tiger tiger)         { System.out.println("feed apple"); }
    void feed(Lion lion)           { System.out.println("feed banana"); }
    void feed(Crocodile crocodile) { System.out.println("feed strawberry"); } // ⚠️ 추가
}

public class Sample {
    public static void main(String[] args) {
        ZooKeeper zk = new ZooKeeper();
        Tiger t = new Tiger();
        Lion l = new Lion();
        Crocodile c = new Crocodile();   // ⚠️ 추가
        zk.feed(t); zk.feed(l); zk.feed(c);  // ⚠️ 호출 줄도 추가
    }
}
```

**고쳐야 하는 곳**: `ZooKeeper` (메서드 추가) + `main` (변수 + 호출 추가) + 새 `Crocodile` 파일. **세 군데** 가 동시에 손을 탑니다.
이건 **"변경에 열려 있는" 코드 = OCP 위반** 입니다.

###### AFTER — Step 2 코드에 Crocodile 을 추가하면?

위 Step 2 의 `Sample.java` 에 **`Crocodile` 클래스 한 개만** 추가하고, **`AnimalContainer.init()` 에 한 줄만** 더하면 끝납니다.

**변경 1 — 새 클래스 추가 (확장에 "열려있음")**

```java
class Crocodile implements Predator {
    @Override public String getFood() { return "strawberry"; }
}
```

**변경 2 — 컨테이너에 새 동물 등록 (단 한 줄)**

```java
class AnimalContainer {
    private final List<Predator> predators = new ArrayList<>();
    private ZooKeeper zooKeeper;

    public void init() {
        predators.add(new Tiger());
        predators.add(new Lion());
        predators.add(new Crocodile());   // ✨ 이 한 줄만 추가
        zooKeeper = new ZooKeeper(predators);
    }
    public ZooKeeper getZooKeeper() { return zooKeeper; }
}
```

**변경되지 않는 곳들 (변경에 "닫혀있음")**

- ❌ `Predator` 인터페이스 — 그대로
- ❌ `Tiger`, `Lion` — 그대로
- ❌ `ZooKeeper` — **한 글자도 안 바뀜** ← 이 한 줄이 OCP 의 정수
- ❌ `main` — **한 글자도 안 바뀜**

실행 결과:
```
feed apple
feed banana
feed strawberry
```

**왜 이게 가능한가?**

- `ZooKeeper` 는 `Predator` 라는 **계약(인터페이스)** 만 알기 때문에 (= 1단계 DIP의 효과).
- 객체 생성은 `AnimalContainer` 가 책임지기 때문에 (= 2단계 IoC의 효과).
- 새 구현체(`Crocodile`)는 **계약을 지키기만 하면** 기존 코드를 한 줄도 깨지 않고 합류할 수 있음 (= 3단계 OCP의 결과).

###### 본 강의 7장 `MyContainer` 에서는 더 짧아집니다

Step 3 (어노테이션 기반) 으로 가면 `AnimalContainer.init()` 의 그 한 줄(`predators.add(new Crocodile())`) 마저도 사라집니다:

```java
@MyComponent
class Crocodile implements Predator {
    @Override public String getFood() { return "strawberry"; }
}
```

**이 파일 하나만 추가** 하면 `MyContainer` 가 스캔 → `new` → `ZooKeeper` 의 `List<Predator>` 에 자동 주입까지 다 해 줍니다. **다른 어떤 파일도 손대지 않습니다.** 이것이 Spring 이 칭송받는 "**플러그인처럼 갈아끼우는 확장성**" 의 정체입니다.

###### 한 줄 정리 — 3단계 흐름의 가치

| 단계 | 한 줄 효과 | 실험 |
| --- | --- | --- |
| 1단계 (DIP) | `ZooKeeper` 가 구체 동물을 모르게 만듦 | 0.6~0.7 의 `void feed(Predator p)` |
| 2단계 (IoC/DI) | `ZooKeeper` 도, `main` 도 `new` 를 하지 않게 만듦 | Step 2 의 `AnimalContainer` |
| **3단계 (OCP)** | **새 동물이 들어와도 기존 파일을 안 건드림** | 위 Crocodile 추가 실험 |

> 👉 1단계 + 2단계가 만들어 낸 **자동 결과** 가 3단계입니다.
> 즉, **OCP 는 따로 노력해서 얻는 것이 아니라, DIP + IoC 를 제대로 하면 저절로 따라오는 보너스** 입니다. 이게 1.2.7 의 **"DIP + IoC = OCP"** 가 의미하는 바입니다.

---

##### (5) 박응용 예제를 본 강의 코드로 한 번에 변환해 보기

방금 발견한 그 통찰이 본 강의 코드에서 어떻게 등장하는지 비교:

| 박응용 (0.5~0.8) | 본 강의 5~7장 | 본 강의 1.2 (Pay 사례) |
| --- | --- | --- |
| `interface Predator` | `interface Animal` | `interface Pay` |
| `class Tiger implements Predator` | `class Lion implements Animal` | `class KakaoPay implements Pay` |
| `class Lion implements Predator` | `class Elephant implements Animal` | `class NaverPay implements Pay` |
| `class ZooKeeper { void feed(Predator p) }` | `class Zoo { List<Animal> animals }` | `class PaymentService { List<Pay> pays }` |
| `new Tiger()` (main 에 박혀 있음) | `@MyComponent` + `MyContainer` 가 대신 `new` | `@MyComponent` + `MyContainer` 가 대신 `new` |
| `new Lion()` (main 에 박혀 있음) | `@MyInject List<Animal>` | `@MyInject List<Pay>` |
| **결과**: ZooKeeper 는 동물 종류와 무관 | **결과**: Zoo 는 동물 종류와 무관 + `new` 도 안 함 | **결과**: PaymentService 는 결제 종류와 무관 + `new` 도 안 함 |

##### (6) 한 줄 정리

| 질문 | 답 |
| --- | --- |
| "0.5~0.8 의 과정이 DIP 인가?" | ✅ **정확히 DIP 입니다.** 박응용 선생님은 이름을 안 붙였을 뿐, 의존 화살표 역전 + 인터페이스 소유권 역전이 모두 등장합니다. |
| "그럼 0장에서 이미 DIP 를 다 배운 건가?" | ✅ 설계 측면은 다 배웠습니다. 남은 건 **객체 생성을 컨테이너에 위임하는 IoC** 만. 이게 1.1, 1.2, 그리고 7장 `MyContainer` 의 주제입니다. |
| "왜 그럼 1.1 에서 DIP 를 또 설명하나?" | 0장은 **"이게 가능하구나"** 를 보여 줬다면, 1.1 은 **"이게 왜 중요한가 — 그리고 IoC 와 어떻게 만나는가"** 를 설명합니다. 한 번에 두 단계를 보여 주면 학생이 지치니까, **0장에서 씨앗 → 1장에서 발아** 의 구조로 분리한 것입니다. |
| "이 통찰을 잡으면 뭐가 좋은가?" | 7장의 모든 코드가 **"이미 알고 있는 그것을, 컨테이너가 대신 해주는 것뿐"** 으로 자연스럽게 읽힙니다. 정확히 그 시각이 본 강의가 의도한 학습 경로입니다. |

> 👉 그래서 0.11(다리) 절 매핑 표에 미리 적어 둔 한 줄이 결국 이 통찰을 가리킵니다:
>
> 💬 *"특히 1.1 의 DIP 와 1.2 의 Pay 사례는, 결국 **'`ZooKeeper` 가 `Predator` 라는 인터페이스에만 의존했더니 새 동물이 들어와도 `ZooKeeper` 코드를 안 고쳐도 된다'** 는 0.5~0.8 의 통찰을 결제 도메인으로 옮긴 것입니다."*

이 통찰을 잡았다면 1장은 거의 복습처럼 읽힐 겁니다.

---

<a id="sec-0-10"></a>
### 0.10 추상 클래스 (abstract class)

> 💬 **추상 클래스(abstract class)는 인터페이스의 역할도 하면서 클래스의 기능도 가지고 있는 특별한 클래스이다.**
> 추상 클래스는 인터페이스처럼 **추상 메서드** 를 가질 수 있으면서도, **일반 클래스처럼 구현된 메서드, 생성자, 인스턴스 변수** 등을 가질 수 있다.

이러한 추상 클래스를 알아보기 위해 우리가 작성했던 (0.7) `Predator` **인터페이스** 를 다음과 같이 **추상 클래스** 로 변경해 보자.

> 📎 앞서 언급했듯이 05장에서 사용되는 예제는 모두 연속되므로 순서대로 예제를 따라 해야 한다.

#### 0.10.1 추상 클래스로 바꿔보기

```java
abstract class Predator extends Animal {
    abstract String getFood();

    default void printFood() {   // default 를 제거한다.
        System.out.printf("my food is %s\n", getFood());
    }
}

(... 생략 ...)
```

> 📝 위 코드의 `default void printFood` 에서 **`default` 키워드는 삭제** 해야 한다(추상 클래스에서는 default 메서드 문법을 사용할 수 없으므로). 원문에서도 `default` 에 취소선이 그어져 있다.

추상 클래스를 만들려면 `class` 앞에 **`abstract`** 키워드를 붙여야 한다. 또한 인터페이스의 메서드와 같은 역할을 하는 메서드(여기서는 `getFood` 메서드)에도 **`abstract`** 를 붙여야 한다. `abstract` 메서드는 인터페이스의 메서드와 마찬가지로 **메서드 본문이 없는 선언만 존재한다.** 따라서 추상 클래스를 상속하는 자식 클래스에서는 **반드시 모든 abstract 메서드를 구현해야 한다.** 그리고 `Animal` 클래스의 기능을 유지하기 위해 `Animal` 클래스를 상속했다.

추상 클래스에서는 인터페이스의 `default` 메서드 문법을 사용할 수 없으므로 **`default` 키워드를 삭제하여 일반 메서드로 변경했다.**

> ⚠️ **추상 클래스는 일반 클래스와 달리 단독으로 객체를 생성할 수 없다. 반드시 추상 클래스를 상속한 실제 클래스를 통해서만 객체를 생성할 수 있다.**

#### 0.10.2 클래스 계층 다시 정리하기

`Predator` 인터페이스를 이와 같이 추상 클래스로 변경하면 `Predator` 인터페이스를 상속했던 `BarkablePredator` 인터페이스는 더 이상 사용이 불가능하므로, 다음과 같이 삭제해야 한다. 그리고 `Tiger`, `Lion` 클래스도 `Animal` 클래스 대신 `Predator` 추상 클래스를 상속하도록 변경해야 한다.

> 📎 `Barkable` / `BarkablePredator` 는 점프 투 자바 05-08(다형성) 절에 등장한 예제이다. **"짖을 수 있다(`bark()`)"** 라는 또 다른 인터페이스를 만들어 두고 `Predator` 와 함께 다중으로 구현하던 흐름인데, 추상 클래스로 통합하면서 그 중간 다리(`BarkablePredator`)가 사라진다는 뜻이다. 이 강의에서는 추상 클래스 자체만 이해하면 충분하다.

```java
abstract class Predator extends Animal {
    (... 생략 ...)
}

interface Barkable {
    (... 생략 ...)
}

interface BarkablePredator extends Predator, Barkable {   // ← 삭제
}

class Animal {
    (... 생략 ...)
}

class Tiger extends Predator implements Barkable {
    (... 생략 ...)
}

class Lion extends Predator implements Barkable {
    (... 생략 ...)
}

class ZooKeeper {
    (... 생략 ...)
}

class Bouncer {
    (... 생략 ...)
}

public class Sample {
    (... 생략 ...)
}
```

`Predator` 추상 클래스에 선언된 `getFood` 메서드는 `Tiger`, `Lion` 클래스에 **이미 구현되어 있으므로 추가로 구현할 필요는 없다.** 추상 클래스의 `abstract` 메서드는 인터페이스의 메서드와 마찬가지로 **상속받는 클래스에서 반드시 구현해야 한다.**

#### 0.10.3 추상 클래스의 큰 장점

**추상 클래스의 큰 장점은 `abstract` 메서드뿐만 아니라 일반 메서드(구현체가 있는 메서드)도 함께 가질 수 있다는 점이다.** 추상 클래스에 일반 메서드를 추가하면 `Tiger`, `Lion` 등 상속받은 모든 객체에서 그 메서드를 바로 사용할 수 있다. 원래 인터페이스에서 `default` 메서드로 사용했던 `printFood` 가 추상 클래스의 **일반 메서드** 에 해당된다.

#### 0.10.4 인터페이스 vs 추상 클래스 — 30초 정리

| 비교 항목 | 인터페이스 (`interface`) | 추상 클래스 (`abstract class`) |
| --- | --- | --- |
| 키워드 | `interface` / `implements` | `abstract class` / `extends` |
| 다중 상속 | 가능 (여러 인터페이스 `implements`) | 불가능 (단일 상속만) |
| 메서드 본문 | 원칙적으로 없음 (Java 8+ `default` 만 예외) | **자유롭게 가능** — `abstract` 메서드와 일반 메서드 공존 |
| 인스턴스 변수(필드) | 가질 수 없음 (상수만 가능) | **가질 수 있음** |
| 생성자 | 없음 | **있음** (자식 클래스에서 `super(...)` 호출) |
| 직접 객체 생성 (`new`) | 불가 | **불가** (단독 인스턴스화 금지) |
| 사용 시기 | "여러 부류가 같은 **역할**(USB 규격)을 공유" | "공통 **뼈대 + 일부 공통 구현**" 을 자식들에게 물려주고 싶을 때 |

> 📌 **핵심 한 줄**:
> **인터페이스 = "규격(계약)만 있음"**, **추상 클래스 = "규격 + 공통 구현 + 상태"** 를 자식에게 모두 물려주는 강력한 부모.
> 따라서 추상 클래스는 **"인터페이스 + 일반 클래스 = 하이브리드"** 라고 보면 됩니다.

#### 0.10.5 본 강의에서 추상 클래스가 쓰이는가?

본 강의의 `MyContainer` 예제는 **인터페이스(`Animal`) + 구현체(`Lion`, `Elephant`)** 구조만 사용합니다(추상 클래스는 직접 등장하지 않습니다). 다만 다음 두 가지는 알아두면 좋습니다.

- **Spring 내부** 는 추상 클래스를 매우 적극적으로 사용합니다. 예를 들어 `AbstractApplicationContext` 는 컨테이너의 공통 골격(`refresh()`, `getBean(...)` 의 기본 흐름)을 일반 메서드로 제공하고, 구체 구현(`AnnotationConfigApplicationContext` 등)이 그 위에 특수 동작을 얹는 식입니다. 우리가 7장에서 만들 `MyContainer` 도, 만약 두 가지 컨테이너 변형(예: 어노테이션 기반 / XML 기반)을 만든다면 **추상 클래스로 공통 로직을 묶는 선택지** 가 자연스럽게 등장합니다.
- 따라서 추상 클래스는 **이 강의에서 직접 작성하지는 않지만, "왜 Spring 코드를 읽다 보면 `Abstract...` 가 자주 보이는가" 를 이해하는 열쇠** 입니다.

---

<a id="sec-0-11"></a>
### 0.11 다음 장으로 가는 다리 — 이 기초가 본 강의에서 어떻게 쓰이는가

지금까지 배운 **클래스 4개 개념 + 인터페이스 5개 개념 + 추상 클래스 1개 개념** 은, 다음 장부터 만들 미니 IoC 프레임워크의 **모든** 부분에 그대로 등장합니다.

| 0장에서 배운 것 | 본 강의에서 만나는 모습 | 등장하는 장 |
| --- | --- | --- |
| `class Animal { }` — 클래스 선언 | `Lion`, `Elephant` 등 도메인 클래스 | 5장 |
| `new Animal()` — 객체 생성 | `MyContainer` 가 리플렉션으로 `clazz.getDeclaredConstructor().newInstance()` 호출 | 7장 |
| `String name;` — 객체 변수 | `Zoo` 의 `List<Animal> animals` 필드 (컨테이너가 주입) | 6장, 7장 |
| `cat.name` — 객체 변수 접근 (도트 연산자) | 리플렉션의 `field.set(target, value)` / `field.get(target)` | 7장 |
| `setName` — 메서드 정의·호출 | `@MyPostConstruct` / `@MyPreDestroy` 메서드를 컨테이너가 `method.invoke(target)` 로 호출 | 7장 |
| `this` — "이 객체를 가리킴" | 컨테이너가 들고 있는 Bean 인스턴스(`target`)가 곧 그 메서드의 `this` | 7장 |
| **객체 변수는 공유되지 않는다** | 각 Bean(`Lion`, `Elephant`, `Zoo`)은 자기만의 상태를 가진 **독립된 객체** — 컨테이너는 이 독립된 객체들을 모아 연결한다 | 전 장 |
| `interface Predator { }` — 인터페이스 선언 | `Animal` 인터페이스 (`Zoo` 가 의존할 "역할") | 5장, 6장 |
| `class Tiger implements Predator` — 구현체 | `Lion implements Animal`, `Elephant implements Animal` | 5장 |
| `feed(Predator predator)` — 인터페이스를 자료형으로 받기 | `Zoo` 의 `List<Animal>` (구체 타입이 아니라 추상에만 의존) | 6장 |
| **메서드 강제 구현 (규칙으로서의 인터페이스)** | `Animal` 을 구현한 모든 동물은 `makeSound()` 같은 메서드를 **반드시** 가져야 함 | 5장 |
| **USB 포트 비유 — 어떤 기기든 연결 가능** | `MyContainer` 가 **어떤 `Animal` 구현체든** `Zoo` 의 `List<Animal>` 에 꽂아 넣음 (DIP 의 실체) | 7장 |
| **디폴트 메서드 (default)** | (이 강의에서는 직접 쓰지 않지만) Spring 의 `Repository` 인터페이스가 자주 활용 | 참고 |
| `abstract class Predator extends Animal` — **추상 클래스** | 본 강의에서는 직접 쓰지 않지만, Spring 의 `AbstractApplicationContext` 처럼 **"공통 골격 + 일부 공통 구현"** 을 자식에게 물려주는 패턴 | 참고 |
| `abstract String getFood();` — **추상 메서드 강제 구현** | 인터페이스의 메서드 강제성과 동일 — 자식 클래스가 반드시 구현 | 5장 |

#### 한 줄 비유

- **클래스** = 과자 틀 / **객체** = 그 틀로 찍어낸 과자
- **인터페이스** = USB 포트 / **구현체** = USB 에 꽂히는 다양한 기기
- **추상 클래스** = 반쯤 완성된 과자 틀 — 모양의 일부(공통 구현)는 미리 박혀 있고, 나머지(추상 메서드)는 자식이 채워 넣어야 한다
- 우리 강의의 `MyContainer` 는 **"어떤 과자 틀(`@MyComponent` 가 붙은 클래스)들이 있는지 찾아내고, 각 틀로 과자를 한 개씩 찍어내고(`new`), 그 과자들이 모두 같은 USB 규격(`Animal` 인터페이스)을 따른다는 것을 이용해 어떤 접시(`@MyInject` 필드)에 자동으로 꽂아 주는 공장 자동화 라인"** 입니다.

#### 핵심 한 줄 (0장 전체 요약)

> **클래스** → **객체** → **객체 변수** → **메서드** → **`this`** → **객체별 독립 상태** → **인터페이스(규칙·USB 포트)** → **다형성** → **추상 클래스(인터페이스 + 일반 클래스 하이브리드)**.
>
> 이 9단계가 머릿속에 자연스럽게 흐르면, 이후 등장하는 모든 어노테이션·리플렉션·DI 코드는 **"이미 알고 있는 그 동작을, 컨테이너가 대신 해주는 것뿐"** 으로 읽히게 됩니다.

> 📌 특히 1.1 의 **DIP(의존 역전 원칙)** 와 1.2 의 **Pay 사례** 는, 결국 **"`ZooKeeper` 가 `Predator` 라는 인터페이스에만 의존했더니 새 동물이 들어와도 `ZooKeeper` 코드를 안 고쳐도 된다"** 는 0.5~0.8 의 통찰을 **결제 도메인** 으로 옮긴 것입니다. 인터페이스의 가치가 손에 잡히지 않으면 0.5~0.8 을, 추상 클래스가 헷갈리면 **0.10** 을 한 번 더 정독하세요.

---

<a id="sec-1"></a>
## 1. 무엇을 만드는가 : 개방-폐쇄원칙 (OCP: Open-Closed Principle)

### 비유: 동물원

- **Lion**, **Elephant** = `Animal` 인터페이스를 구현한 동물 (Bean 후보)
- **Zoo** = 동물들을 모아 운영하는 곳 (Bean 후보, `List<Animal>` 을 주입받음)
- **MyContainer** = 동물을 만들어 동물원에 채워주는 IoC 컨테이너

### 우리가 만들 4가지 어노테이션 (Spring 대응)


| 우리 어노테이션           | 역할               | Spring 등가                               |
| ------------------ | ---------------- | --------------------------------------- |
| `@MyComponent`     | 컨테이너에 등록할 클래스 표시 | `@Component`, `@Service`, `@Repository` |
| `@MyInject`        | 주입받을 필드 표시       | `@Autowired` (필드 주입)                    |
| `@MyPostConstruct` | "태어날 때" 호출될 메서드  | `@PostConstruct`                        |
| `@MyPreDestroy`    | "죽을 때" 호출될 메서드   | `@PreDestroy`                           |


### 컨테이너가 자동으로 수행할 5단계

1. 패키지를 **스캔**해서 `@MyComponent` 클래스 찾기
2. 각 클래스를 `**new`로 인스턴스 생성** (= Bean 생성)
3. `@MyInject` 필드에 **알맞은 Bean 주입**
4. `@MyPostConstruct` 메서드 **호출** (태어날 때)
5. `shutdown()` 시 `@MyPreDestroy` **호출** (죽을 때, 등록 역순)

---

<a id="sec-1-1"></a>
### 1.1 왜 이 구조인가 — DIP 와 IoC, 두 개의 "역전"

> 이 강의에서 만드는 모든 코드(어노테이션, 도메인 클래스, 컨테이너)는 결국 **두 가지 원리** 를 코드로 옮긴 것입니다.
> 한 번만 정확히 이해하면, 이후 등장하는 모든 패턴이 "왜 그렇게 생겼는지" 가 자명해집니다.

#### A. DIP — 의존성 역전 원칙 (Dependency Inversion Principle)

OOP 5원칙(SOLID) 중 "D" 에 해당하는 **설계 원칙**입니다.

**나쁜 예** — `Zoo` 가 `Lion`, `Elephant` 를 직접 알고 의존

```java
class Zoo {
    Lion lion = new Lion();           // 구체 클래스에 직접 의존
    Elephant elephant = new Elephant();
}
```

→ 새 동물(`Tiger`)을 추가하려면 `Zoo` 의 코드를 수정해야 함. 변경에 약함.

**좋은 예** — `Zoo` 는 `Animal` 인터페이스만 알고, 어떤 동물이든 `Animal` 이면 OK

```java
class Zoo {
    List<Animal> animals;             // 추상에만 의존
}
```

→ `Tiger` 가 추가돼도 `Animal` 만 구현하면 `Zoo` 는 한 줄도 안 바꿈.

**핵심 한 줄**:

> 상위 모듈(`Zoo`)이 하위 모듈(`Lion`)에 의존하지 말고,
> **둘 다 추상화(`Animal`)에 의존하라.**

#### 왜 "역전" 이라는 이름이 붙었나?

DIP 의 이름이 "Inversion(역전)" 인 이유는 두 가지가 동시에 뒤집히기 때문입니다.

**① 의존 화살표의 방향이 뒤집힌다**

전통적인(자연스러운) 방향:

```
Zoo (상위 모듈)  ──의존──▶  Lion, Elephant (하위 모듈)
```

상위가 하위를 직접 `import` 하고 `new` 한다. 즉 **상위 → 하위**. 우리가 평소 코드를 짤 때 자연스럽게 나오는 방향입니다(호출하는 쪽이 호출되는 쪽 이름을 알아야 하니까).

DIP 적용 후:

```
Zoo (상위)  ──의존──▶  Animal (추상)  ◀──의존──  Lion (하위)
```

이제 `Lion` 의 의존 화살표가 **위쪽 추상으로 뒤집힙니다**:

- 전: `Zoo → Lion` (상위 → 하위)
- 후: `Lion → Animal` (하위 → 상위쪽 추상)

**하위 모듈의 의존 화살표가 위쪽으로 뒤집혔다** — 이것이 "역전" 의 첫 번째 의미.

**② 인터페이스 "소유권" 이 뒤집힌다**

또 하나 뒤집힌 게 있습니다 — **누가 인터페이스를 정의하는가**.

- 전통: 하위 모듈(`Lion`)이 자기 API 를 정의 → 상위(`Zoo`)가 거기에 맞춰 호출
- DIP: **상위 모듈(`Zoo`)이 "나는 이런 동물이 필요해"라며 `Animal` 인터페이스를 정의** → 하위(`Lion`)가 거기에 맞춰 구현

즉 **"클라이언트(상위)가 계약을 정하고, 공급자(하위)가 따라온다"**. 갑을 관계가 뒤집힌 셈이죠.

> 💡 그래서 잘 설계된 프로젝트에서는 `Animal` 인터페이스가 `Zoo` 와 같은 상위 패키지에 함께 있고, `Lion` 은 별도 하위 패키지에서 그것을 구현합니다.

---

#### B. IoC — 제어의 역전 (Inversion of Control)

DIP 가 **"어떻게 설계할까"** 의 원칙이라면, IoC 는 **"누가 흐름을 제어하나"** 의 패턴입니다.

전통적인 코드의 제어 흐름:

```java
// 내(개발자)가 직접 만들고 직접 호출
Lion lion = new Lion();
Elephant elephant = new Elephant();
Zoo zoo = new Zoo();
zoo.setAnimals(List.of(lion, elephant));
zoo.open();
lion.makeSound();
```

"누가 객체를 만들고, 언제 호출되는가" 의 **제어권이 내 코드에 있다**.

IoC 적용 후:

```java
@MyComponent
class Zoo {
    @MyInject List<Animal> animals;    // 컨테이너가 채워줌
    @MyPostConstruct void open() {... } // 컨테이너가 시점 결정
}
```

내 코드는 **"무엇을"** 만 어노테이션으로 선언하고, **"언제 / 어떻게 / 어떤 순서로"** 의 제어는 **프레임워크에게 넘긴다**. 이게 바로 "제어의 역전(Inversion of Control)" 입니다.

> 🎬 **헐리우드 원칙**: **"Don't call us, we'll call you."**
> 내가 프레임워크를 부르지 않는다 — 프레임워크가 나를 부른다.

---

#### C. DIP 와 IoC 는 무엇이 다른가? — 비교표

둘 다 이름에 "Inversion(역전)" 이 들어가서 헷갈리는데, **뒤집는 대상이 다릅니다**.

| 구분 | DIP | IoC |
| --- | --- | --- |
| **무엇을 뒤집나?** | 의존 방향 (누가 누구 타입을 아는가) | 제어 흐름 (누가 누구를 호출/생성하는가) |
| **어느 차원?** | 설계 / 컴파일 타임 | 실행 / 런타임 |
| **결정 주체** | 개발자(설계 선택) | 프레임워크 |
| **질문 형태** | "어떻게 설계할까?" | "그 설계를 어떻게 굴릴까?" |
| **이 강의에서 대응** | `Animal` 인터페이스 도입 | `MyContainer` 의 스캔·생성·주입·생명주기 |

---

#### D. 둘이 함께 작동하는 이유 — `MyContainer` 안에서

`MyContainer` 예제를 보면 DIP 와 IoC 가 동시에 작동합니다:

```
[DIP]  Zoo  ──▶  Animal  ◀──  Lion        ← 설계: 타입 의존 방향이 뒤집힘
                   ▲
                   │ 그런데 누가 이 연결을 실제로 해줄까?
                   │
[IoC]          MyContainer                 ← 실행: 제어권이 컨테이너로 넘어감
```

- **DIP** 만 지키고 IoC 가 없다면: `Zoo` 가 `Animal` 인터페이스에 의존은 하지만, 결국 누군가가 `new Lion()` 을 해서 `Zoo` 에 넣어줘야 한다. 그 "누군가" 를 우리 코드가 직접 하면 DIP 의 의미가 반감됩니다.
- **IoC** 컨테이너가 등장해서 그 "연결" 을 대신 해주니까 DIP 가 실전에서 완성됩니다.

즉 한 문장으로:

> **DIP 가 "어떻게 설계할까" 의 원칙**이라면,
> **IoC 는 "그 설계를 어떻게 굴릴까" 의 메커니즘**이고,
> **DI(Dependency Injection)** 는 IoC 를 구현하는 가장 흔한 기법(=`@MyInject` 처럼 의존성을 외부에서 꽂아주는 방식)입니다.

---

#### E. 비유 한 줄 정리

- **DIP**: 동물원이 "사자" 가 아니라 "동물" 을 받겠다고 **계약서를 다시 쓴 것**
- **IoC**: 그 계약대로 동물을 모집·배치·운영하는 일을 **동물원장이 아닌 컨테이너가 맡은 것**
- **DI**: 컨테이너가 동물을 동물원 안에 **실제로 넣어주는 행위**

설계(DIP) + 실행(IoC) + 수단(DI) 이 합쳐져야 우리가 Spring 에서 보는 **"느슨한 결합(loose coupling)"** 이 완성됩니다.

---

#### F. 이 강의의 코드에서 어디가 DIP 이고 어디가 IoC 인가

미리 보는 매핑입니다(자세한 구현은 4~8장).

| 강의 코드 | 어떤 원리의 표현인가 |
| --- | --- |
| `Animal` 인터페이스 (`domain/Animal.java`) | **DIP** — 상위 모듈이 정의한 추상 계약 |
| `Lion`, `Elephant` 가 `implements Animal` | **DIP** — 하위 모듈이 추상에 의존(화살표 역전) |
| `Zoo` 의 `List<Animal>` 필드 | **DIP** — 구체 타입 아닌 추상에만 의존 |
| `@MyComponent` 어노테이션 | **IoC** — 객체 생성 제어를 컨테이너에 위임하겠다는 선언 |
| `@MyInject` 어노테이션 | **IoC + DI** — 의존성 채우는 책임을 컨테이너에 위임 |
| `@MyPostConstruct` / `@MyPreDestroy` | **IoC** — 생명주기(호출 시점) 제어권을 컨테이너에 위임 |
| `MyContainer` 의 스캔·`newInstance`·필드 주입·메서드 호출 | **IoC 컨테이너의 본체** — 헐리우드 원칙의 "we'll call you" 측 |

> 👉 다음 장부터 이 표의 각 줄을 코드로 직접 구현합니다. 코드를 칠 때마다 **"지금 이 줄은 DIP 를 표현하는가, IoC 를 표현하는가?"** 를 의식하면 학습 효과가 극대화됩니다.

---

<a id="sec-1-2"></a>
### 1.2 사례: 각종 Pay 추가

> 동물원 비유가 추상적으로 느껴진다면, 실제 업무에서 가장 자주 마주치는 사례로 옮겨 봅시다.
> **"결제 수단(Payment) 이 자꾸 추가되는 시스템"** — 카카오페이, 네이버페이, 토스페이, 페이코, 삼성페이…
> 이 도메인에서 DIP 와 IoC 가 없으면 어떤 고통이 생기고, 적용하면 무엇이 달라지는지를 같은 코드로 비교합니다.

#### 1.2.0 개념 복습 — IoC 와 DIP (한 번 더)

IoC(제어의 역전)와 DIP(의존 역전 원칙)는 객체지향 프로그래밍에서 **결합도를 낮추고 유연한 코드** 를 작성하기 위해 **함께 사용되는** 핵심 개념입니다.

- **DIP (의존 역전 원칙)**
  - **정의**: 상위 모듈이 하위 모듈에 의존하지 않고, 둘 다 **"추상화(인터페이스)"** 에 의존해야 한다는 **설계 원칙**.
  - **핵심**: 구체적인 클래스가 아닌 **"역할"** 에 의존하게 만듭니다.
- **IoC (제어의 역전)**
  - **정의**: 객체의 생성·생명주기 관리 등의 **제어권** 을 개발자가 아닌 **프레임워크(예: Spring)** 가 대신 담당하는 디자인 패턴.
  - **핵심**: 내가 직접 객체를 생성하던 흐름을 뒤집어, **프레임워크가 나에게 객체를 제공** 하도록 만듭니다.

> 두 개념은 **"클래스 간의 직접적인 연결을 끊고 추상화를 통해 느슨한 결합(loose coupling)을 만든다"** 는 공통 목적을 가집니다.

이제 이 두 가지 원리를 **결제 수단** 도메인에 적용해 봅시다.

---

#### 1.2.1 나쁜 예 — `PaymentService` 가 각종 Pay 를 직접 알고 의존

처음 카카오페이 하나만 붙일 때는 누구나 이렇게 짭니다.

```java
// ❌ DIP 위반 + IoC 없음
public class KakaoPay {
    public void pay(int amount) {
        System.out.println("[KakaoPay] " + amount + "원 결제");
    }
}

public class PaymentService {
    private KakaoPay kakaoPay = new KakaoPay();   // 구체 클래스에 직접 의존 + 직접 생성

    public void checkout(int amount) {
        kakaoPay.pay(amount);
    }
}
```

문제가 없어 보이지만, **요구사항이 늘면서 지옥문이 열립니다**.

> 기획자: "이번 분기에 **네이버페이, 토스페이, 페이코, 삼성페이** 도 붙여주세요. 사용자는 결제 시 선택할 수 있어야 하고요."

```java
// ❌ "그냥 if-else 로 늘리면 되겠지" — 흔한 실수
public class PaymentService {
    private KakaoPay  kakaoPay  = new KakaoPay();
    private NaverPay  naverPay  = new NaverPay();
    private TossPay   tossPay   = new TossPay();
    private Payco     payco     = new Payco();
    private SamsungPay samsungPay = new SamsungPay();

    public void checkout(String method, int amount) {
        if      ("KAKAO".equals(method))   kakaoPay.pay(amount);
        else if ("NAVER".equals(method))   naverPay.pay(amount);
        else if ("TOSS".equals(method))    tossPay.pay(amount);
        else if ("PAYCO".equals(method))   payco.pay(amount);
        else if ("SAMSUNG".equals(method)) samsungPay.pay(amount);
        else throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
    }
}
```

**무엇이 잘못됐나** — 한 줄씩 짚어봅시다.

| 증상 | 원인 (어떤 원칙 위반?) |
| --- | --- |
| 새 결제 수단(예: `ApplePay`)이 추가될 때마다 `PaymentService` 코드 수정 | **DIP 위반** — 상위 모듈(`PaymentService`)이 구체 클래스에 의존 |
| `new KakaoPay()` 처럼 객체 생성 책임이 `PaymentService` 안에 있음 | **IoC 부재** — 제어권이 개발자(서비스 코드) 손에 있음 |
| `if-else` 가 결제 수단 수만큼 길어짐 (OCP 위반의 전형) | DIP 위반의 자연스러운 결과 |
| 테스트 시 가짜(Mock) 결제 모듈로 교체 불가 — `new` 가 박혀 있음 | IoC 부재로 의존성 교체 불가 |
| `KakaoPay` 가 외부 API 호출이라도 추가되면 `PaymentService` 단위 테스트 = 실제 결제 호출 | 같은 원인 |

---

#### 1.2.2 좋은 예 — `Pay` 인터페이스 도입 (DIP 적용)

먼저 **DIP 만** 적용해 봅시다. 추상화(`Pay` 인터페이스)를 도입하고, `PaymentService` 는 구체 클래스가 아니라 **역할** 에만 의존합니다.

```java
// ✅ Step 1: 상위 모듈이 정의한 "계약(Contract)"
public interface Pay {
    String name();              // 식별자 — "KAKAO", "NAVER" ...
    void pay(int amount);       // 동작 — 결제 처리
}
```

```java
// ✅ Step 2: 하위 모듈(구체 결제 수단)이 추상에 맞춰 구현
public class KakaoPay implements Pay {
    @Override public String name() { return "KAKAO"; }
    @Override public void pay(int amount) {
        System.out.println("[KakaoPay] " + amount + "원 결제");
    }
}

public class NaverPay implements Pay {
    @Override public String name() { return "NAVER"; }
    @Override public void pay(int amount) {
        System.out.println("[NaverPay] " + amount + "원 결제");
    }
}

public class TossPay implements Pay {
    @Override public String name() { return "TOSS"; }
    @Override public void pay(int amount) {
        System.out.println("[TossPay] " + amount + "원 결제");
    }
}
```

```java
// ✅ Step 3: 상위 모듈은 "역할" 에만 의존, if-else 도 사라짐
public class PaymentService {
    private final Map<String, Pay> registry;   // "KAKAO" -> KakaoPay 인스턴스 ...

    public PaymentService(List<Pay> pays) {
        this.registry = pays.stream()
            .collect(Collectors.toMap(Pay::name, p -> p));
    }

    public void checkout(String method, int amount) {
        Pay pay = registry.get(method);
        if (pay == null) throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        pay.pay(amount);
    }
}
```

**무엇이 달라졌나**:

- `PaymentService` 의 코드에는 `KakaoPay`, `NaverPay` 같은 **구체 이름이 한 글자도 등장하지 않는다**.
- `ApplePay` 를 추가하려면? **`Pay` 를 구현한 `ApplePay` 클래스 한 개만 만들면 끝**. `PaymentService` 는 한 줄도 안 바뀝니다 → **OCP(개방-폐쇄 원칙)** 자동 만족.
- 의존 화살표:
  - 전: `PaymentService → KakaoPay` (상위 → 하위, 구체)
  - 후: `PaymentService → Pay ← KakaoPay` (양쪽 모두 추상으로) ← **DIP 의 "역전"**

> 그런데 한 가지 찜찜한 점이 남았습니다.
> **누군가는 `new KakaoPay()`, `new NaverPay()` 를 호출해서 `PaymentService` 에 넘겨줘야** 합니다.
> 그 일을 누가 할까요?

#### 1.2.3 IoC 적용 — 제어권을 컨테이너에게 (우리 `MyContainer` 버전)

이제 객체 생성·연결 책임을 **컨테이너에게 넘깁니다**. 우리 강의에서 직접 만들 `MyContainer` 의 어노테이션으로 작성하면 이렇게 됩니다.

```java
// pay 패키지 — 추상
public interface Pay {
    String name();
    void pay(int amount);
}
```

```java
// pay 패키지 — 구체 구현체에 @MyComponent
@MyComponent
public class KakaoPay implements Pay {
    @Override public String name() { return "KAKAO"; }
    @Override public void pay(int amount) {
        System.out.println("[KakaoPay] " + amount + "원 결제");
    }
}

@MyComponent
public class NaverPay implements Pay {
    @Override public String name() { return "NAVER"; }
    @Override public void pay(int amount) {
        System.out.println("[NaverPay] " + amount + "원 결제");
    }
}

@MyComponent
public class TossPay implements Pay {
    @Override public String name() { return "TOSS"; }
    @Override public void pay(int amount) {
        System.out.println("[TossPay] " + amount + "원 결제");
    }
}
```

```java
// service 패키지 — 상위 모듈도 @MyComponent
@MyComponent
public class PaymentService {

    @MyInject
    private List<Pay> pays;          // ← 컨테이너가 모든 Pay 구현체를 자동 주입

    private Map<String, Pay> registry;

    @MyPostConstruct
    public void init() {             // ← 컨테이너가 의존성 주입 후 자동 호출
        this.registry = pays.stream()
            .collect(Collectors.toMap(Pay::name, p -> p));
        System.out.println("[PaymentService] 결제 수단 " + registry.keySet() + " 준비 완료");
    }

    public void checkout(String method, int amount) {
        Pay pay = registry.get(method);
        if (pay == null) throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        pay.pay(amount);
    }

    @MyPreDestroy
    public void close() {            // ← 컨테이너 종료 시 자동 호출
        System.out.println("[PaymentService] 종료");
    }
}
```

`Main` 은 단 세 줄.

```java
public class Main {
    public static void main(String[] args) {
        MyContainer ctx = new MyContainer("com.example.pay");
        PaymentService svc = ctx.getBean(PaymentService.class);

        svc.checkout("KAKAO", 10_000);
        svc.checkout("NAVER", 25_000);
        svc.checkout("TOSS",   5_500);

        ctx.shutdown();
    }
}
```

**예상 출력**:

```
[PaymentService] 결제 수단 [KAKAO, NAVER, TOSS] 준비 완료
[KakaoPay] 10000원 결제
[NaverPay] 25000원 결제
[TossPay] 5500원 결제
[PaymentService] 종료
```

**여기서 무슨 일이 일어났나** — 각 줄을 원리로 매핑.

| 코드 | 원리 |
| --- | --- |
| `interface Pay` | DIP — 상위 모듈이 정의한 추상 계약 |
| `KakaoPay implements Pay` 등 | DIP — 하위 모듈이 추상에 의존(화살표 역전) |
| `@MyComponent` (각 클래스 위) | IoC — "이 객체의 생성·관리를 컨테이너에 맡깁니다" 라는 선언 |
| `@MyInject List<Pay>` | IoC + DI — 의존성을 채우는 책임을 컨테이너에게 위임 |
| `@MyPostConstruct`, `@MyPreDestroy` | IoC — 생명주기 호출 시점을 컨테이너에게 위임 |
| `new MyContainer(...).getBean(...)` | IoC 컨테이너 본체 — "we'll call you" 의 실행자 |

---

#### 1.2.4 Spring 버전 (어노테이션만 표준으로 교체)

같은 코드를 Spring 에서는 어노테이션 이름만 표준으로 바꾸면 그대로 동작합니다.

```java
public interface Pay {
    String name();
    void pay(int amount);
}

@Component
public class KakaoPay implements Pay {
    @Override public String name() { return "KAKAO"; }
    @Override public void pay(int amount) {
        System.out.println("[KakaoPay] " + amount + "원 결제");
    }
}

@Component
public class NaverPay implements Pay { /* 동일 */ }

@Component
public class TossPay  implements Pay { /* 동일 */ }

@Service
public class PaymentService {

    private final Map<String, Pay> registry;

    // 생성자 주입 — Spring 이 모든 Pay 구현체를 List 로 모아 주입
    public PaymentService(List<Pay> pays) {
        this.registry = pays.stream()
            .collect(Collectors.toMap(Pay::name, p -> p));
    }

    @PostConstruct
    void init() {
        System.out.println("[PaymentService] 결제 수단 " + registry.keySet() + " 준비 완료");
    }

    public void checkout(String method, int amount) {
        Pay pay = registry.get(method);
        if (pay == null) throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        pay.pay(amount);
    }

    @PreDestroy
    void close() { System.out.println("[PaymentService] 종료"); }
}
```

```java
@SpringBootApplication
public class PayApp implements CommandLineRunner {
    private final PaymentService svc;
    public PayApp(PaymentService svc) { this.svc = svc; }

    public static void main(String[] args) { SpringApplication.run(PayApp.class, args); }

    @Override public void run(String... args) {
        svc.checkout("KAKAO", 10_000);
        svc.checkout("NAVER", 25_000);
        svc.checkout("TOSS",   5_500);
    }
}
```

> **본질이 똑같다는 게 보이시나요?** `@MyComponent → @Component`, `@MyInject → 생성자 주입`, `@MyPostConstruct → @PostConstruct`. 우리가 만들 미니 컨테이너는 Spring 의 작동 원리를 그대로 따라합니다.

---

#### 1.2.5 핵심: 새 결제 수단을 추가할 때 변경량 비교

가장 결정적인 차이는 **"새 Pay 가 추가될 때 무엇을 고쳐야 하는가"** 입니다.

| 시나리오 | 나쁜 예(1.2.1) | 좋은 예(1.2.2~1.2.4) |
| --- | --- | --- |
| `ApplePay` 추가 | `PaymentService` 의 필드·`if-else`·생성자 모두 수정 | `ApplePay implements Pay` 클래스 **하나만 새로 작성** (기존 코드 0줄 수정) |
| 결제 모듈 단위 테스트 | `new KakaoPay()` 가 박혀 있어 Mock 대체 불가 | 생성자/필드에 가짜 `Pay` 구현체를 넣어 즉시 테스트 |
| 결제 순서 변경 / 우선순위 | 서비스 코드 안에서 분기 수정 | `@Order` 같은 메타데이터로 외부에서 제어 |
| 빌드 시점에 결제 수단 끼우기/빼기 | 코드 수정 → 재컴파일 | 어노테이션 on/off 또는 프로파일로 토글 |

**한 줄 요약**:

> 나쁜 예의 `PaymentService` 는 **"내가 어떤 Pay 들을 쓰는지 다 안다"**.
> 좋은 예의 `PaymentService` 는 **"나는 `Pay` 라는 역할만 안다. 누가 들어오든 컨테이너가 알아서 채워준다."**
>
> 이 차이가 **DIP(설계의 역전) + IoC(제어의 역전)** 가 코드에 가져다주는 실제 가치입니다.

---

#### 1.2.6 "두 개의 역전" 이 여기서 어떻게 보이는가

마지막으로, 1.1 의 두 가지 역전이 이 사례에서 **눈으로** 어떻게 보이는지 정리합니다.

- **DIP 의 역전** — 의존 화살표
  - 전: `PaymentService → KakaoPay, NaverPay, TossPay …` (상위가 모든 구체를 안다)
  - 후: `PaymentService → Pay  ←  KakaoPay, NaverPay, TossPay …` (모두 추상으로 향함)
- **IoC 의 역전** — 제어 흐름
  - 전: `PaymentService` 가 직접 `new KakaoPay()` 호출 (내 코드가 컨테이너 역할까지 함)
  - 후: 컨테이너가 `new KakaoPay()`, `new NaverPay()` … 를 만들어 `PaymentService` 의 `List<Pay>` 에 **꽂아준다**. `@PostConstruct` 호출 시점도 컨테이너가 결정.

> 👉 1.1 의 동물원 비유에서 본 두 가지 역전이, 결제 도메인에서도 글자 하나 안 바뀌고 똑같이 작동한다는 점을 확인했습니다.

---

#### 1.2.7 한 걸음 더 — **DIP + IoC = OCP 달성** (Pay 예제로 본 SOLID 의 진짜 목적지)

여기까지 보면 자연스럽게 떠오르는 의문이 있습니다.

> "그래서 DIP 와 IoC 를 적용하면 **결국 무엇이 좋아지는가?**"

그 답이 바로 **OCP(Open-Closed Principle, 개방-폐쇄 원칙)** 입니다.
DIP 와 IoC 는 **그 자체가 목적이 아니라**, OCP 를 달성하기 위한 **수단** 입니다.

##### OCP 란?

객체지향 설계 5대 원칙(SOLID) 중 "O" 에 해당합니다.

> **"소프트웨어 요소(클래스, 모듈, 함수)는 확장에는 열려 있고(Open for extension), 변경에는 닫혀 있어야(Closed for modification) 한다."**

쉽게 풀면:

- **확장(Open)**: 새 기능(예: 네이버페이)을 추가하는 길은 **열려 있어야** 한다.
- **닫힘(Closed)**: 그렇다고 기존 코드(예: `OrderService`)는 **건드리지 말아야** 한다.

##### Pay 도메인으로 본 OCP 위반 vs 준수

❌ **OCP 위반 — 새 결제 수단마다 `OrderService` 를 고쳐야 함**

```java
class OrderService {
    public void processPayment(String type) {
        if (type.equals("KakaoPay")) {
            // 카카오페이 결제 로직
        } else if (type.equals("TossPay")) {
            // 토스페이 결제 로직
        } else if (type.equals("NaverPay")) { // ⚠️ 기존 코드를 수정해야만 함!
            // 네이버페이 결제 로직
        }
    }
}
```

**문제점**: 새로운 결제 방식이 추가될 때마다 `OrderService` 라는 **기존 코드를 계속 수정** 해야 합니다.
실수로 기존 코드를 잘못 건드리면 잘 작동하던 카카오페이·토스페이 결제까지 망가질 위험이 있습니다.

⭕ **OCP 준수 — 인터페이스(추상화)로 확장 가능하게 설계**

```java
// 1. 변하지 않는 기준(인터페이스)을 정의
interface Payment {
    void pay();
}

// 2. 각 결제 수단은 인터페이스를 구현 (확장에 열려있음)
class KakaoPay implements Payment { public void pay() { /* ... */ } }
class TossPay  implements Payment { public void pay() { /* ... */ } }
class NaverPay implements Payment { public void pay() { /* ... */ } } // ✨ 새로 추가됨!

// 3. 주문 서비스는 인터페이스에만 의존 (변경에 닫혀있음)
class OrderService {
    public void processPayment(Payment payment) {
        payment.pay(); // 어떤 결제 수단이 들어오든 이 코드는 절대 변하지 않음!
    }
}
```

**장점**: '네이버페이', '라인페이' 등 새로운 결제 수단이 **수백 개 추가되어도** `OrderService` 코드는 **단 한 줄도 수정할 필요가 없습니다.**

##### DIP, IoC 와 OCP 의 연결 고리

앞서 살펴본 DIP, IoC 와 OCP 는 **긴밀하게 연결되어 작동** 합니다.

| 원칙 | 역할 |
| --- | --- |
| **DIP** (의존 역전) | 구체적인 결제 클래스(`KakaoPay`)가 아니라 `Payment` 라는 **인터페이스(추상화)** 를 바라보게 설계 → OCP 가 가능해지는 **구조적 기반** |
| **IoC / DI** (제어의 역전) | `OrderService` 내부에서 `new KakaoPay()` 를 직접 생성하지 않고, **외부(스프링 프레임워크 등)** 에서 실제 결제 객체를 주입 → `OrderService` 코드를 변경 없이 유지 |
| **OCP** (개방-폐쇄) | 위 둘의 결과로 **자동 달성** 되는 최종 목적지 |

> **즉, DIP 와 IoC 는 OCP 라는 대원칙을 지키기 위한 핵심 수단이자 가이드라인입니다.**

##### 톱니바퀴처럼 맞물리는 3단계 과정

우리가 달성하고 싶은 최종 목적은:

> **"기존 코드를 한 줄도 안 고치고(OCP), 새로운 기능을 플러그인처럼 갈아끼우는 것"**

이를 위해 DIP 와 IoC 가 차례대로 제 역할을 합니다.

```
[ 1단계: DIP 설계 ]  ➡️  [ 2단계: IoC/DI 적용 ]  ➡️  [ 최종 결과: OCP 달성 ]
   인터페이스 중심 설계        외부에서 객체 조립·주입        기존 코드 수정 없이 확장 가능
```

**1단계 — DIP 로 "길" 을 열기 (설계 단계)**

- **내용**: 클래스가 구체적인 실체 대신 **추상적인 인터페이스에 의존** 하도록 설계
- **효과**: 구체적인 구현체가 카카오페이든, 토스페이든, 새로 추가될 네이버페이든 관계없이 `OrderService` 는 **오직 `Payment` 인터페이스만 바라봄**
- **의의**: "변하지 않는 인터페이스라는 울타리" 를 만들어 OCP 가 작동할 수 있는 **구조적 기반** 을 마련

**2단계 — IoC 로 "연결" 하기 (실행 단계)**

- **내용**: DIP 로 구조를 잘 짰어도, **누군가는 실제로 작동할 구체적인 객체(예: `KakaoPay`)를 만들어서 `OrderService` 에 꽂아주어야** 한다. 이 일을 개발자가 코드에 직접 적지 않고 **프레임워크(IoC 컨테이너)에게 위임**.
- **효과**: 프레임워크가 외부 설정(설정 파일이나 어노테이션 등)을 보고 **상황에 맞는 객체를 알아서 조립** 하고 넣어줌(의존성 주입, DI).
- **의의**: 객체를 바꾸고 싶을 때 개발자가 소스 코드를 직접 수정(`new KakaoPay()` ➡️ `new NaverPay()`)할 필요가 **없게** 만들어 줌.

**3단계 — OCP 자동 달성 (결과)**

DIP 를 통해 인터페이스를 바라보게 만들었고, IoC 를 통해 외부에서 객체를 갈아끼워 주니 **마법 같은 일** 이 일어납니다.

> 새로운 기능(네이버페이)을 추가할 때, **기존 비즈니스 로직 소스 코드는 단 한 글자도 수정하지 않고(Closed), 새로운 기능 클래스만 새로 작성하여 파일로 추가(Open)** 하는 설계가 완벽히 실현됩니다.

##### 한 줄 비유 — "다리와 차"

> **"DIP 로 확장 가능한 다리를 놓고, IoC 로 다리 위에 필요한 차를 외부에서 보내주니,
> 기존 다리(코드)를 무너뜨리거나 공사하지 않고도 새로운 차(기능)들이 자유롭게 지나다닐 수 있게 된다(OCP)."**

##### Pay 예제로 다시 보기 — 3단계가 어디서 일어났나

지금까지 본 코드를 3단계 프레임으로 다시 매핑하면 이렇습니다.

| 단계 | 우리 Pay 예제에서의 해당 위치 |
| --- | --- |
| **1단계: DIP 설계** | `Pay` (또는 `Payment`) **인터페이스 정의** 하고, `PaymentService` / `OrderService` 가 그것에만 의존 (`1.2.2`) |
| **2단계: IoC / DI 적용** | `@MyComponent` / `@Component` 로 객체 생성 권한을 컨테이너에 넘기고, `@MyInject List<Pay>` / 생성자 주입으로 의존성을 자동 주입 (`1.2.3`, `1.2.4`) |
| **3단계: OCP 달성 (결과)** | `ApplePay` 가 추가돼도 `PaymentService` / `OrderService` 는 **한 줄도 안 바뀜** — `ApplePay implements Pay` 파일 한 개만 추가 (`1.2.5`) |

##### 정리 — SOLID 의 큰 그림 안에서

> **"DIP 와 IoC 는 그 자체로는 수단이다. 최종 목적은 OCP 다."**
>
> 그리고 OCP 가 달성되면 코드는 **"기존을 안 건드리고, 새 기능을 끼워 넣는다"** 라는 객체지향 설계의 이상에 가까워집니다.
> 다음 장부터 만들 `MyContainer` 의 모든 기능(스캔·주입·생명주기)은, 결국 **여러분이 작성하는 도메인 코드가 OCP 를 자동으로 만족하도록 돕는 인프라** 라고 보시면 됩니다.

> 👉 다음 장부터 본격적으로 코드를 직접 작성하면서, **모든 코드 라인이 결국 두 가지 역전 중 하나(DIP 또는 IoC)를 표현하고, 그 결과로 OCP 가 달성된다는 시각** 을 유지하세요.

---

<a id="sec-2"></a>
## 2. 사전 지식 — OOP · 어노테이션 · 리플렉션

본격 코딩 전에, 이 강의 전체에서 사용할 세 가지 자바 기능을 짧게 정리합니다. 이 절을 이해하면 나머지 코드가 "주문(呪文)" 이 아니라 명확한 동작으로 보입니다.

### 2.1 OOP 기초 복습 (자신 있으면 건너뛰기)

이 강의는 다음 4가지 OOP 개념을 전제로 합니다. 익숙하시면 2.2 로 건너뛰세요.

#### 인터페이스 vs 클래스

- **클래스** = 객체의 설계도 (필드 + 메서드 구현)
- **인터페이스** = 동작의 계약서 (메서드 시그니처만 있음, 구현은 없음)

```java
// 인터페이스: "동물은 이름이 있고 소리를 낼 수 있다" 는 계약
public interface Animal {
    String getName();
    String makeSound();
}

// 클래스: 그 계약을 구체적으로 구현
public class Lion implements Animal {
    @Override public String getName() { return "레오"; }
    @Override public String makeSound() { return "어흥!"; }
}
```

#### 다형성 (Polymorphism)

- 같은 인터페이스를 구현한 객체들을 **하나의 타입으로 다룰 수 있음**
- `Animal a = new Lion();` → `a.makeSound()` 는 Lion 의 makeSound() 호출됨

```java
List<Animal> animals = List.of(new Lion(), new Elephant());
for (Animal a : animals) {
    System.out.println(a.makeSound());  // 각 동물의 소리 — 어흥!, 푸우우~
}
```

→ Zoo 가 `List<Animal>` 만 알아도 모든 동물을 다룰 수 있는 이유.

#### DIP — 의존성 역전 원칙 (Dependency Inversion Principle)

- **나쁜 예**: Zoo 가 `Lion`, `Elephant` 를 직접 알고 의존
- **좋은 예**: Zoo 는 `Animal` 인터페이스만 알고, 어떤 동물이든 `Animal` 이면 OK
- 핵심: **상위 모듈(Zoo)이 하위 모듈(Lion)에 의존하지 말고, 둘 다 추상화(Animal)에 의존하라**

#### OCP — 개방-폐쇄 원칙 (Open-Closed Principle)

- **확장에는 열려있고** (새 동물 추가 가능)
- **변경에는 닫혀있다** (기존 Zoo 코드 안 바꿈)
- `Tiger implements Animal` 만들고 `@MyComponent` 붙이면 끝 → Zoo 는 그대로

#### 이 4가지가 IoC 의 토대인 이유

- DIP·OCP 를 지키려면 객체끼리 **직접 `new`** 하지 않아야 함
- 그러면 누가 `new` 를 해주나? → **IoC 컨테이너**
- 즉 이 강의 전체는 "DIP/OCP 를 자동으로 지켜주는 도구" 를 만드는 과정

### 2.2 어노테이션이란?

**한 줄 정의**: 코드(클래스/필드/메서드)에 붙이는 **메타데이터 마커**.

- 어노테이션 자체는 **아무 일도 하지 않습니다.**
- 누군가가 **읽어주기 전까지는** 그저 표시일 뿐.
- 그 "누군가" 는 보통 **컴파일러** 또는 **런타임 리플렉션 코드**.

```java
@MyComponent          // ← 이 표시 자체로는 아무 일도 안 일어남
public class Lion { } // ← 컨테이너가 리플렉션으로 읽어줘야 의미가 생김
```

### 2.3 어노테이션 정의 문법

```java
@Retention(RetentionPolicy.RUNTIME)   // ← 언제까지 살아있을지
@Target(ElementType.TYPE)              // ← 어디에 붙일 수 있는지
public @interface MyComponent {        // ← interface 가 아니라 @interface
}
```

### 2.4 `@Retention` — 어노테이션 정보가 언제까지 남는가


| 값         | 의미                                     | 예시                           |
| --------- | -------------------------------------- | ---------------------------- |
| `SOURCE`  | **컴파일 시점까지만** 살아있음. `.class` 파일에 안 들어감 | `@Override` (컴파일러 검사용)       |
| `CLASS`   | `.class` 파일에는 들어가지만 **JVM이 로드할 때 버려짐** | 잘 안 쓰임 (기본값)                 |
| `RUNTIME` | **런타임까지 살아남음**. 리플렉션으로 읽을 수 있음         | `@Component`, `@MyComponent` |


> ⚠️ **이 강의의 모든 어노테이션은 `RUNTIME`** 이어야 합니다.
> 만약 `SOURCE` 로 두면 컨테이너가 리플렉션으로 못 읽어서 **아무 Bean 도 등록되지 않습니다** (조용히 실패하는 가장 흔한 함정).

### 2.5 `@Target` — 어디에 붙일 수 있나


| 값             | 부착 위치     | 우리 강의에서 사용                          |
| ------------- | --------- | ----------------------------------- |
| `TYPE`        | 클래스/인터페이스 | `@MyComponent`                      |
| `FIELD`       | 필드        | `@MyInject`                         |
| `METHOD`      | 메서드       | `@MyPostConstruct`, `@MyPreDestroy` |
| `CONSTRUCTOR` | 생성자       | (이 강의 미사용)                          |
| `PARAMETER`   | 메서드 파라미터  | (이 강의 미사용)                          |


### 2.6 리플렉션이란?

**한 줄 정의**: 프로그램이 **자기 자신의 구조**(클래스, 필드, 메서드, 어노테이션)를 **런타임에 들여다보고 조작**하는 자바 API.

#### 일반 코드 vs 리플렉션 — 그림으로

**일반 코드 (정적)**

```java
Lion lion = new Lion();          // 컴파일러가 "아, Lion 클래스구나" 미리 앎
lion.makeSound();                // 메서드도 미리 앎
```

컴파일 시점에 **모든 게 결정됨**. 컴파일러가 다 알고 있음.

**리플렉션 (동적)**

```java
String name = "com.example.Lion";        // 문자열로만 시작
Class<?> cls = Class.forName(name);      // 그 이름의 클래스를 찾아옴
Object obj = cls.getDeclaredConstructor().newInstance();  // 인스턴스 생성
Method m = cls.getMethod("makeSound");   // 메서드 찾기
Object result = m.invoke(obj);           // 실행
```

컴파일 시점엔 `Lion` 이 뭔지 **모름**. 런타임에 알아냄.

#### 비유

**비유 1: 거울 보기**

- 일반 코드 = "나는 코를 만진다" (자기 몸을 직접 알고 동작)
- 리플렉션 = **거울로 자기 모습을 보면서** "여기 코가 있네, 만져야지" (자기 구조를 들여다본 뒤 행동)
- → "Reflection" 이라는 이름의 유래

**비유 2: 처음 보는 자동차**

- 일반 코드 = "내 차 시동을 켠다" (어디에 키 구멍이 있는지 안다)
- 리플렉션 = **처음 보는 차에 타서** "이 차에 어떤 버튼이 있는지 살피고", "이 핸들이라는 걸 돌리고", "이 페달이라는 걸 밟는다"
- 즉 **사전에 모르는 객체도** 그 구조를 조사한 뒤 조작 가능

#### 무엇을 할 수 있나 (구체적 능력)


| 능력                 | API 예시                         | 일상적 비유                   |
| ------------------ | ------------------------------ | ------------------------ |
| 클래스 찾기             | `Class.forName("...")`         | 이름표로 사람 찾기               |
| 어떤 필드가 있는지 보기      | `cls.getDeclaredFields()`      | 가방 안 물건 목록 보기            |
| 어떤 메서드가 있는지 보기     | `cls.getDeclaredMethods()`     | 이 사람이 할 줄 아는 것 목록        |
| 어떤 어노테이션이 붙어있는지 보기 | `cls.isAnnotationPresent(...)` | 이마에 붙은 라벨 읽기             |
| 인스턴스 생성            | `constructor.newInstance()`    | "Lion 한 마리 만들어줘" (이름만으로) |
| 필드 값 읽기/쓰기         | `field.get()` / `field.set()`  | private 자물쇠 따고 안의 값 조작   |
| 메서드 호출             | `method.invoke(obj)`           | "이 메서드 실행해줘" (이름만으로)     |
| 접근 제한 무시           | `setAccessible(true)`          | private 잠금장치 강제 해제       |


#### 왜 이런 게 필요한가? — 동기

**동기 1: 컴파일 시점에 클래스를 모를 때** — IDE 플러그인, 우리 컴포넌트 스캔  
**동기 2: 어노테이션 기반 자동화** — `@MyComponent` 붙은 모든 클래스 자동 등록  
**동기 3: 라이브러리 / 프레임워크 작성** — Spring, Hibernate, JUnit, Jackson 모두 사용  
**동기 4: 테스트 / 디버깅** — private 메서드 테스트, 내부 상태 확인

#### 우리 미니 프레임워크에서 리플렉션이 한 일 (미리 보기)

```java
// (1) "com.example.myframework.domain.Lion" 이라는 문자열만으로 클래스 객체 얻기
Class<?> cls = Class.forName("com.example.myframework.domain.Lion");

// (2) @MyComponent 가 붙어있는지 확인
if (cls.isAnnotationPresent(MyComponent.class)) {

    // (3) new Lion() 을 동적으로 호출
    Object lion = cls.getDeclaredConstructor().newInstance();

    // (4) private 필드 animals 에 강제로 값 넣기
    Field field = cls.getDeclaredField("animals");
    field.setAccessible(true);
    field.set(lion, listOfAnimals);

    // (5) born() 메서드를 이름으로 찾아 호출
    for (Method m : cls.getDeclaredMethods()) {
        if (m.isAnnotationPresent(MyPostConstruct.class)) {
            m.invoke(lion);
        }
    }
}
```

**일반 자바라면 불가능한 일**:

- 컴파일 시점에 `Lion` 을 `import` 하지 않고도 동작
- private 필드에 접근
- 메서드를 이름(문자열 + 어노테이션) 으로 찾아 호출

#### 대가 (단점)

리플렉션은 강력하지만 공짜는 아닙니다:

1. **느림** — 일반 호출보다 수 배~수십 배 느림 (한 번 캐싱하면 차이 줄어듦)
2. **컴파일러 검사 우회** — 메서드 이름을 문자열로 쓰니까 오타 나도 컴파일 통과, 런타임 에러
3. **리팩토링 위험** — 메서드명 바꾸면 문자열 참조가 깨짐. IDE 가 잡기 어려움
4. **보안 우회** — private 도 뚫음 → 캡슐화 깨짐
5. **코드 난해** — 일반 코드보다 읽기 어려움

→ 그래서 **꼭 필요할 때만** 씀.

#### 리플렉션 한 줄 요약

> **리플렉션 = "이 클래스/메서드 이름이 X 인데, 그게 뭐 하는 건지 살펴보고 한번 실행해줘"** 를 가능하게 하는 API.
> Spring 의 자동 설정, JPA 의 엔티티 매핑, JUnit 의 `@Test` 발견 — 모두 리플렉션 위에 세워져 있습니다.

### 2.7 이 강의에서 사용할 리플렉션 API 9선

> 이 표는 7장 `MyContainer` 코드를 읽을 때 옆에 펼쳐두세요.


| API                                          | 의미                             |
| -------------------------------------------- | ------------------------------ |
| `Class.forName("com.x.Foo")`                 | 문자열로 된 클래스 이름 → `Class` 객체     |
| `cls.isAnnotationPresent(MyComponent.class)` | 이 클래스에 `@MyComponent` 붙어있는가?   |
| `cls.getDeclaredConstructor()`               | 기본 생성자(인자 없는) `Constructor` 객체 |
| `constructor.newInstance()`                  | 그 생성자로 인스턴스 생성 = `new Foo()`   |
| `cls.getDeclaredFields()`                    | 클래스의 **모든 필드**(private 포함)     |
| `cls.getDeclaredMethods()`                   | 클래스의 **모든 메서드**(private 포함)    |
| `field.setAccessible(true)`                  | private 도 강제로 읽고/쓸 수 있게 만듦     |
| `field.set(obj, value)`                      | `obj.field = value` 와 동일       |
| `method.invoke(obj)`                         | `obj.method()` 호출              |


### 2.8 제네릭 타입 소거(Type Erasure) — 1분 핵심

**문제**: 자바 제네릭은 **컴파일 시점에 사라집니다.**

```java
List<Animal> animals;        // ← 소스코드
// 컴파일 후 런타임의 실제 모습:
List animals;                // ← 그냥 List 가 됨 (Animal 정보 사라짐)
```

그래서 `field.getType()` 은 그냥 `List.class` 만 알려줍니다. `**Animal` 정보를 얻으려면?**

→ `**field.getGenericType()`** 이라는 별도 API 가 있습니다. 이건 `ParameterizedType` 을 반환하고, 거기서 `getActualTypeArguments()` 로 `Animal` 을 꺼낼 수 있습니다.

```java
ParameterizedType pt = (ParameterizedType) field.getGenericType();
Class<?> itemType = (Class<?>) pt.getActualTypeArguments()[0];  // → Animal.class
```

> 💡 **외워둘 점**: 단순 타입은 `getType()`, 제네릭 정보까지 보려면 `getGenericType()` 후 `ParameterizedType` 캐스팅.

### 2.9 ClassLoader — 클래스를 찾아오는 자

JVM 이 클래스를 찾고 메모리에 올리는 역할을 `ClassLoader` 가 합니다.

```java
ClassLoader cl = Thread.currentThread().getContextClassLoader();
URL url = cl.getResource("com/example/myframework");  // 디렉터리 URL
```

- 우리 컨테이너는 패키지 이름을 디렉터리 경로(`.` → `/`)로 바꾸어 ClassLoader 에게 "이 폴더 어디 있어?" 라고 묻고
- 받아온 URL 을 `File` 로 변환해 그 안의 `.class` 파일들을 훑습니다.

여기까지가 사전 지식의 전부입니다. 이제 코딩으로 들어갑시다.

---

<a id="sec-3"></a>
## 3. 프로젝트 셋업 (사전 요구사항 포함)

### 3.1 사전 요구사항


| 도구           | 최소 버전  | 확인 명령                                      |
| ------------ | ------ | ------------------------------------------ |
| **JDK**      | 21 이상  | `java -version`                            |
| **Maven**    | 3.9 이상 | `mvn -version`                             |
| **IDE** (선택) | 최신     | IntelliJ IDEA Community / Cursor / VS Code |


#### JDK 21 설치 (macOS)

**옵션 A**: SDKMAN 사용 (추천 — 버전 관리 편리)

```bash
# 1) SDKMAN 설치 (최초 1회), 
# 설치후에는 반드시 셸 재시작 또는 source ~/.sdkman/bin/sdkman-init.sh
curl -s "https://get.sdkman.io" | bash
# curl: 웹 서버와 데이터를 주고받는 명령어 라인 도구입니다. 
# 주로 인터넷에서 파일이나 데이터를 다운로드할 때 사용합니다.
# -s: 'silent'의 약자로, 다운로드 진행 상황이나 에러 메시지 같은 불필요한 출력(로그)을 숨기고 조용히 실행하라는 옵션입니다.
#  "https://get.sdkman.io": 다운로드할 파일이 위치한 인터넷 주소(URL)입니다. 
# 이 주소는 설치를 위한 스크립트 파일을 제공합니다.
# |: '파이프(Pipe)'라고 부르며, 앞 명령어의 실행 결과(출력)를 뒤 명령어의 입력으로 전달하는 연결고리 역할을 합니다.
# bash: 전달받은 코드를 실행할 쉘(터미널 환경) 프로그램입니다. 
# curl로 다운로드한 SDKMAN! 설치 스크립트 코드를 bash가 실행하게 됩니다.

# 2) 설치 가능한 Java 버전 전체 리스트 조회 (Temurin, Zulu, Corretto, GraalVM 등 벤더별)
sdk list java                  # 화면을 페이지 단위로 스크롤하며 사용 가능한 모든 버전 + Identifier 표시
sdk list java | grep tem       # Temurin(=tem) 버전만 필터링해서 보기
sdk list java | grep "21\."    # 21.x 계열만 필터링해서 보기

# 3) 현재 PC 에 이미 설치된(=installed) Java 버전만 보기
sdk current java               # 지금 셸에서 사용 중인 버전 하나만 표시
sdk list java | grep installed # ">>>" / "installed" 표시가 붙은, 로컬에 설치된 버전 전체 목록

# 4) 원하는 버전 설치 + 전역 기본값 지정
sdk install java 21-tem        # 위 리스트의 Identifier 컬럼 값을 그대로 사용
sdk default java 21-tem
java -version                  # openjdk version "21"

# 5) (선택) 더 이상 안 쓰는 버전 정리
sdk uninstall java <Identifier>
```

> 📎 **`sdk list java` 화면 읽는 법**:
> 좌측에 벤더(예: `Temurin`, `Zulu`, `Corretto`, `GraalVM`), 중간에 버전, 우측에 **Identifier**(예: `21-tem`, `21.0.5-tem`, `21.0.2-graal`)가 표시됩니다.
> `sdk install java <Identifier>` 에 넣을 값은 **이 Identifier** 입니다.
> 이미 설치된 버전 옆에는 `installed`, 현재 셸에서 사용 중인 버전 옆에는 `>>>` 마커가 붙습니다.

##### 🎯 현재 프로젝트 폴더에서만 특정 버전을 자동으로 사용하기 — `.sdkmanrc`

전역 기본값(`sdk default`)과 별개로, **이 강의 프로젝트 폴더로 들어올 때만** Java 21 (Temurin) 이 자동 선택되도록 지정할 수 있습니다. SDKMAN 의 `.sdkmanrc` 기능입니다.

1) **프로젝트 폴더로 이동 후 `.sdkmanrc` 생성**

```bash
cd /Users/eugene/Dropbox/K3I_PC/coding/99_ax_seminar/04_1_javaspring/spring-react-ioc-crud-study/framework_example
sdk env init        # .sdkmanrc 파일이 생성됨 — 현재 사용 중인 버전이 자동 기록
```

생성된 `.sdkmanrc` 는 다음과 같은 형태입니다:

```
# Enable auto-env through the sdkman_auto_env config
# Add key=value pairs of SDKs to use below
java=21-tem
```

> 📎 다른 버전을 지정하려면 그 줄을 `java=21.0.5-tem` 처럼 구체 버전으로 바꾸거나, `sdk install java <원하는-버전>` 으로 먼저 설치한 뒤 같은 키를 갱신하면 됩니다. 사용 가능한 식별자는 `sdk list java` 로 확인할 수 있습니다.

2) **폴더에 들어갈 때마다 수동 적용** (가장 간단)

```bash
cd framework_example
sdk env             # .sdkmanrc 를 읽어 java=21-tem 으로 즉시 전환
java -version       # openjdk version "21" 확인
```

3) **(권장) 폴더 진입 시 자동 전환 활성화** — 한 번만 설정

```bash
sed -i.bak 's/^sdkman_auto_env=false/sdkman_auto_env=true/' ~/.sdkman/etc/config
```

또는 직접 편집:

- `~/.sdkman/etc/config` 파일을 열어
- `sdkman_auto_env=false` → **`sdkman_auto_env=true`** 로 변경 후 저장
- 새 셸을 열거나 `source ~/.sdkman/bin/sdkman-init.sh` 실행

이제 `cd framework_example` 만 해도 SDKMAN 이 `.sdkmanrc` 를 감지해 자동으로 `java=21-tem` 으로 전환합니다. 폴더를 벗어나면 자동으로 이전(전역 기본) 버전으로 돌아옵니다.

4) **검증 — 폴더 안/밖에서 버전이 달라지는지 확인**

```bash
cd framework_example   # auto_env=true 이면 "Using java version 21-tem in this shell." 메시지 출력
java -version          # 21
cd ..
java -version          # 전역 기본 버전 (만약 다른 버전이라면 그것으로 복귀)
```

> ⚠️ **팁**:
> - `.sdkmanrc` 는 **Git 으로 커밋해 두는 것을 권장**합니다 → 팀원이 이 폴더로 들어오면 동일한 JDK 버전이 자동 선택되어 **"내 PC 에서는 됐는데" 류의 버전 차이 문제** 가 사라집니다.
> - 이 강의의 `pom.xml` 도 이미 Java 21 을 타깃으로 합니다(`<maven.compiler.release>21</maven.compiler.release>`). `.sdkmanrc` 와 짝을 이루면 빌드/실행 환경이 완전히 일치합니다.

##### 🧠 한 걸음 더 — `.sh` 파일과 `rc` 파일은 같은 건가? (bash 가 둘 다 읽는 거 아닌가?)

방금 본 두 파일이 헷갈리기 쉽습니다.

- `source ~/.sdkman/bin/sdkman-init.sh` ← **`.sh`**
- `~/.sdkmanrc` 또는 폴더의 `.sdkmanrc` ← **`rc`**

> 💡 결론부터: **둘 다 텍스트 파일이라는 점만 같고, 역할·내용·"누가 언제 읽는가" 가 다 다릅니다. bash 가 항상 읽는 것도 아닙니다.**

###### 본질적 차이 — "누가 읽는가, 무엇이 들어 있는가"

| 항목 | `.sh` 파일 | `rc` 파일 |
| --- | --- | --- |
| 이름의 뜻 | Shell script | **Run Commands** (1965 년 CTSS 시절부터 내려온 관용어) |
| 내용 형식 | **반드시 셸 스크립트(bash 문법)** | **그 파일을 읽는 프로그램이 정한 형식** — 셸 문법일 수도 있고, key=value 일 수도 있음 |
| 누가 읽나 | **사용자가 명시적으로** 실행(`./x.sh`)하거나 소싱(`source x.sh`) | **특정 프로그램이 자동으로** 읽음 (`.bashrc` → bash, `.vimrc` → vim, `.sdkmanrc` → sdkman) |
| 실행 권한 필요? | 직접 실행하려면 필요 | 보통 불필요 (소싱/파싱만 되므로) |
| 트리거 | 사람이 부를 때 | **사건 발생 시 자동** (셸 시작, vim 시작, `cd`, …) |

핵심: **`rc` 는 "자동으로 읽히는 설정 파일" 이라는 *역할 이름*** 이고, **`.sh` 는 "셸 문법이라는 *내용 형식*"** 입니다. 차원이 다릅니다.

###### 지금 본 두 파일로 비교

**(1) `~/.sdkman/bin/sdkman-init.sh` — 진짜 bash 스크립트**

```bash
# 안에 이런 식의 bash 코드가 들어 있음 (발췌)
export SDKMAN_DIR="$HOME/.sdkman"
sdk() {
    # ... 수십 줄의 bash 함수 정의 ...
}
```

- **bash 문법으로 짜인 실제 코드.**
- `source ~/.sdkman/bin/sdkman-init.sh` 를 하면 위 함수·환경변수가 **현재 셸 안에 주입**됩니다. 그래서 그 다음부터 `sdk` 명령을 칠 수 있게 됩니다.
- bash 가 한 줄씩 진짜로 실행합니다.

**(2) `.sdkmanrc` — bash 가 절대 읽지 않는 key=value 파일**

```ini
# Enable auto-env through the sdkman_auto_env config
java=21-tem
```

- **이건 bash 코드가 아닙니다.** `java=21-tem` 처럼 보이지만 bash 의 변수 할당이 아니라 **SDKMAN 의 설정 항목** 입니다.
- 읽는 주체는 bash 가 아니라 `sdkman-init.sh` 안에 들어 있는 **`sdk` 함수 자체** 입니다. `cd` 가 발생하면 `sdk` 함수가 `.sdkmanrc` 를 한 줄씩 파싱해서 `sdk use java 21-tem` 같은 동작으로 변환합니다.
- 그래서 `.sdkmanrc` 에는 `if`, `function`, `echo` 같은 bash 문법을 쓸 수 없습니다 — 쓰면 무시되거나 깨집니다.

###### "rc 파일이지만 실제로 bash 가 읽는" 예외 — `.bashrc`

혼동의 원인은 보통 `.bashrc` 때문입니다.

```bash
# ~/.bashrc 안 — 진짜 bash 코드
export PATH="$HOME/bin:$PATH"
alias ll='ls -alF'
```

- `.bashrc` 도 이름은 "rc" 파일이지만, **이 rc 파일을 자동으로 읽는 프로그램이 마침 bash 자신** 입니다.
- 그러니까 `.bashrc` 의 내용 형식이 bash 스크립트인 것뿐, **"rc 라서 bash 가 읽는" 게 아니라 "bash 가 자신의 rc 로 지정한 파일이라서 읽는다"** 가 정확한 표현입니다.

###### 한 줄로 정리

| 질문 | 답 |
| --- | --- |
| "둘 다 bash 가 읽나?" | ❌ 아닙니다. `.sh` 는 bash 가 읽도록 만든 코드. `rc` 는 그 파일을 자기 설정으로 정한 **임의의 프로그램** 이 읽음. |
| "그럼 `.sdkmanrc` 도 bash 가 읽나?" | ❌ bash 가 아니라 **sdkman 의 `sdk` 함수** 가 읽음. 내용도 bash 문법이 아님. |
| "`sdkman-init.sh` 는?" | ✅ bash 가 직접 실행하는 진짜 bash 코드. |
| "그럼 `.bashrc` 가 헷갈리는 이유는?" | bash 가 정한 자기 자신의 rc 파일이라서. "rc 라서가 아니라 bash 가 자기 rc 로 지정해서" 읽는 것. |

###### 비유

- **`.sh`** = 요리 레시피 종이 (실행할 사람이 보고 그대로 따라 함)
- **`rc`** = 식당 운영 매뉴얼 (특정 식당이 매일 아침 자동으로 펴서 읽는 설정. 그 식당이 어떤 형식으로 적으라 정해놓았는지가 중요)
- **`.bashrc`** = "bash 라는 식당의 운영 매뉴얼". 마침 이 식당이 정한 매뉴얼 형식이 레시피 종이(bash 문법)와 똑같아서 헷갈림.
- **`.sdkmanrc`** = "sdkman 식당의 운영 매뉴얼". 이건 레시피 종이 형식이 아니라 **체크리스트(key=value)** 형식이라 bash 한테 들이밀면 못 읽음.

> 👉 그래서 `source .sdkmanrc` 같은 건 의미가 없고(또는 깨지고), `.sdkmanrc` 는 항상 **`sdk` 함수가 `cd` 같은 이벤트에서 자동으로 들춰본다** 는 약속으로만 동작합니다.

**옵션 B**: Homebrew

```bash
brew install openjdk@21
```

**옵션 C**: 공식 사이트 (Adoptium / Temurin) 에서 dmg 다운로드

- [https://adoptium.net](https://adoptium.net)

#### Maven 설치

**macOS**:

```bash
brew install maven
mvn -version   # Apache Maven 3.9.x
```

**Linux**:

```bash
sudo apt install maven
```

**Windows**: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) 에서 zip 다운로드 후 PATH 등록.

### 3.2 디렉터리 구조 (최종 결과)

```
framework_example/
├── README.md
├── 강의.md                                   ← 이 문서
├── pom.xml
└── src/main/java/com/example/myframework/
    ├── annotation/
    │   ├── MyComponent.java
    │   ├── MyInject.java
    │   ├── MyPostConstruct.java
    │   └── MyPreDestroy.java
    ├── container/
    │   └── MyContainer.java
    ├── domain/
    │   ├── Animal.java
    │   ├── Lion.java
    │   ├── Elephant.java
    │   └── Zoo.java
    └── Main.java
```

### 3.3 폴더와 `pom.xml` 만들기

상위 프로젝트(`spring-react-ioc-crud-study/`)의 루트에서:

```bash
mkdir -p framework_example/src/main/java/com/example/myframework/{annotation,container,domain}
```

`framework_example/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>myframework</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.1</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.example.myframework.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**의도적 선택**:

- **외부 의존성 0개** — JDK 표준 라이브러리만으로 IoC 의 본질을 보여준다.
- Java 21 — 상위 프로젝트와 동일.

### 3.4 첫 컴파일 검증 (소스가 없을 때)

```bash
cd framework_example
mvn -q compile
```

소스 파일이 없으므로 "no sources to compile" 같은 메시지가 나오지만 **에러 없이** 끝나야 합니다. 만약 다음과 같은 에러가 나면 환경 문제입니다:

- `JAVA_HOME is not set` → JDK 설치/환경변수 확인
- `release version 21 not supported` → JDK 17 이하 사용 중. 21 로 전환

---

<a id="sec-4"></a>
## 4. 어노테이션 4개 작성

이제 4개의 어노테이션을 만듭니다.

> 📌 **2장 복습**: `@Retention(RUNTIME)` 으로 런타임까지 살리고, `@Target(...)` 으로 부착 위치 명시. 이 어노테이션들은 자체로는 **아무 일도 하지 않습니다.** 7장 컨테이너가 읽어주기 시작할 때 의미가 생깁니다.

### `annotation/MyComponent.java`

```java
package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyComponent {
}
```

### `annotation/MyInject.java`

```java
package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MyInject {
}
```

### `annotation/MyPostConstruct.java`

```java
package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyPostConstruct {
}
```

### `annotation/MyPreDestroy.java`

```java
package com.example.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyPreDestroy {
}
```

### ✔️ 중간 검증

```bash
cd framework_example
mvn -q compile
```

에러 없이 끝나야 합니다. 4개의 `.class` 파일이 `target/classes/com/example/myframework/annotation/` 에 생성되면 OK.

---

<a id="sec-5"></a>
## 5. 도메인 클래스 — Animal, Lion, Elephant

### `domain/Animal.java` — 인터페이스

```java
package com.example.myframework.domain;

public interface Animal {
    String getName();

    String makeSound();
}
```

> 2.1 절 복습: Zoo 가 **구체 클래스에 의존하지 않고 인터페이스에만 의존** (DIP).

### `domain/Lion.java`

```java
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
```

### `domain/Elephant.java`

```java
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
```

> 💡 **이 시점에서 Lion/Elephant 는 그냥 평범한 자바 클래스**입니다. 어노테이션은 붙어있지만 누구도 읽지 않으니 아무 일도 일어나지 않습니다. `new Lion()` 을 직접 해도 `born()` 은 호출되지 않습니다 — 컨테이너가 만들어줘야 호출됩니다.

### ✔️ 중간 검증

```bash
mvn -q compile
```

에러 없이 통과해야 합니다.

---

<a id="sec-6"></a>
## 6. 도메인 클래스 — Zoo (의존성을 받는 쪽)

### `domain/Zoo.java`

```java
package com.example.myframework.domain;

import com.example.myframework.annotation.MyComponent;
import com.example.myframework.annotation.MyInject;
import com.example.myframework.annotation.MyPostConstruct;
import com.example.myframework.annotation.MyPreDestroy;

import java.util.List;

@MyComponent
public class Zoo {

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
```

**관전 포인트**:

- `Zoo` 는 `**new Lion()`, `new Elephant()` 를 직접 호출하지 않는다.** → 이게 IoC 의 핵심
- `Zoo` 는 어떤 동물이 들어올지 모른다 — 그저 `Animal` 인터페이스만 안다
- 새 동물(예: `Tiger`)을 추가하려면? 그냥 `@MyComponent` 붙은 `Tiger implements Animal` 클래스 하나 만들면 끝 — `**Zoo` 코드는 한 줄도 안 바뀜**

> 이게 바로 2.1 절의 **OCP(개방-폐쇄 원칙)** 가 실제로 동작하는 모습입니다.

---

<a id="sec-7"></a>
## 7. 핵심 — MyContainer 구현

여기가 **이 강의의 클라이맥스**입니다. 약 150줄의 코드로 IoC 컨테이너의 5단계를 모두 구현합니다.

### 7.0 한눈에 보는 컨테이너 흐름

코드 보기 전에 전체 흐름을 그림으로 잡습니다:

```
┌──────────────────────────────────────────────────────────┐
│  Main.java                                                │
│  new MyContainer().scan("com.example.myframework")        │
└────────────────────────┬─────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────┐
│ MyContainer.scan()                                        │
│                                                           │
│  ┌─ [1] 클래스 스캔 ─────────────────────────────────┐   │
│  │  findClasses("com.example.myframework")           │   │
│  │  → ClassLoader.getResource → File.listFiles       │   │
│  │  → List<Class<?>> (Lion, Elephant, Zoo, ...)      │   │
│  └────────────────────────────────────────────────────┘   │
│                         ↓                                 │
│  ┌─ [2] Bean 생성 ──────────────────────────────────┐   │
│  │  @MyComponent 검사 → newInstance()                │   │
│  │  beans = {                                        │   │
│  │    Lion.class      → Lion@1234,                   │   │
│  │    Elephant.class  → Elephant@5678,               │   │
│  │    Zoo.class       → Zoo@9abc                     │   │
│  │  }                                                │   │
│  └────────────────────────────────────────────────────┘   │
│                         ↓                                 │
│  ┌─ [3] 의존성 주입 ────────────────────────────────┐   │
│  │  @MyInject 필드 검사                              │   │
│  │  Zoo.animals = [Lion@1234, Elephant@5678]         │   │
│  └────────────────────────────────────────────────────┘   │
│                         ↓                                 │
│  ┌─ [4] @MyPostConstruct 호출 ──────────────────────┐   │
│  │  Lion.born()                                      │   │
│  │  Elephant.born()                                  │   │
│  │  Zoo.open()      ← 이 시점에 animals는 이미 채워짐 │   │
│  └────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────┐
│  Zoo zoo = container.getBean(Zoo.class)                   │
│  zoo.showAll()  ← 사용                                    │
└────────────────────────┬─────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────┐
│ MyContainer.shutdown()                                    │
│                                                           │
│  ┌─ [5] @MyPreDestroy 역순 호출 ────────────────────┐   │
│  │  Zoo.close()       ← 가장 마지막 등록 → 가장 먼저  │   │
│  │  Elephant.die()                                   │   │
│  │  Lion.die()        ← 가장 먼저 등록 → 가장 마지막  │   │
│  └────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────┘
```

**중요한 보장**: 각 단계는 **앞 단계가 완전히 끝난 뒤** 다음 단계가 시작됩니다.

- 모든 Bean 이 생성된 후에야 → 주입 시작
- 모든 주입이 끝난 후에야 → PostConstruct 호출
- 그래서 Zoo 의 `open()` 이 `animals.size()` 를 호출해도 NullPointerException 이 안 납니다.

### `container/MyContainer.java` 전체

```java
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

public class MyContainer {

    // 등록 순서를 보존하기 위해 LinkedHashMap 사용 (PreDestroy 역순 호출에 사용)
    private final Map<Class<?>, Object> beans = new LinkedHashMap<>();

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

        // 3) PostConstruct
        for (Object bean : beans.values()) {
            invokeLifecycle(bean, MyPostConstruct.class);
        }

        System.out.println("[Container] 가동 완료. 총 " + beans.size() + " 개의 Bean");
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object found = findBeanByType(type);
        if (found == null) {
            throw new IllegalStateException("Bean 을 찾을 수 없습니다: " + type.getName());
        }
        return (T) found;
    }

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

            // List<T> 주입
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

    private void invokeLifecycle(Object bean,
            Class<? extends java.lang.annotation.Annotation> ann) throws Exception {
        for (Method m : bean.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(ann)) {
                m.setAccessible(true);
                m.invoke(bean);
            }
        }
    }

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
```

### 7.1 코드 깊이 읽기 — 단계별 해부

#### 단계 1: 클래스 스캔 (`findClasses` + `collect`)

```java
String path = basePackage.replace('.', '/');
URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
File dir = new File(resource.toURI());
```

> 🔍 **리플렉션 박스 — ClassLoader.getResource**
>
> - 입력: `"com/example/myframework"` (디렉터리 경로)
> - 출력: 그 디렉터리의 파일시스템 URL (예: `file:/.../target/classes/com/example/myframework`)
> - 핵심: **JVM 이 `.class` 파일을 찾는 길**과 동일한 메커니즘을 사용

> 🔍 **리플렉션 박스 — Class.forName**
>
> ```java
> out.add(Class.forName(name));
> ```
>
> - 입력: 풀 네임 문자열 `"com.example.myframework.domain.Lion"`
> - 출력: `Class<?>` 객체 (= `Lion.class` 와 동일)
> - 이 객체로부터 어노테이션, 생성자, 필드, 메서드 모두 조회 가능

> ⚠️ **JAR 안 스캔 미지원**: `new File(URI)` 가 `jar:file:` URI 를 받지 못합니다. 그래서 `java -jar` 로 실행하면 깨집니다. 실제 Spring 은 `JarFile` API 를 별도로 다루지만, 이 예제는 학습 범위를 좁히기 위해 의도적으로 제외했습니다. `**java -cp target/classes ...` 방식으로 실행하세요.**

#### 단계 2: Bean 생성 (`scan` 의 1번 블록)

```java
if (cls.isAnnotationPresent(MyComponent.class)) {
    Object instance = cls.getDeclaredConstructor().newInstance();
    beans.put(cls, instance);
}
```

> 🔍 **리플렉션 박스 — isAnnotationPresent**
>
> - `cls.isAnnotationPresent(MyComponent.class)` = "이 클래스에 `@MyComponent` 가 붙어있는가?"
> - 어노테이션이 `RetentionPolicy.RUNTIME` 일 때만 동작. (`SOURCE` 면 항상 `false` 반환 → 가장 흔한 함정)

> 🔍 **리플렉션 박스 — getDeclaredConstructor + newInstance**
>
> - `cls.getDeclaredConstructor()` = **인자 없는 생성자**를 얻는다 (= `Lion.class` 의 `Lion()` 생성자)
> - `.newInstance()` = 그 생성자로 인스턴스 생성 (= `new Lion()`)
> - **함정**: 클래스에 public 기본 생성자가 없으면 `NoSuchMethodException`. 이 강의 클래스들은 명시적 생성자가 없어서 컴파일러가 자동으로 만들어 줍니다 — 그래서 동작.

#### 단계 3: 의존성 주입 (`injectDependencies`)

```java
for (Field field : bean.getClass().getDeclaredFields()) {
    if (!field.isAnnotationPresent(MyInject.class)) continue;
    field.setAccessible(true);
    ...
}
```

> 🔍 **리플렉션 박스 — getDeclaredFields**
>
> - `cls.getDeclaredFields()` = 클래스가 **자체 선언한 모든 필드** (private 포함, 상속받은 것은 제외)
> - 상속받은 필드까지 보려면 `getFields()` (단, public 만) 또는 슈퍼클래스 재귀

> 🔍 **리플렉션 박스 — setAccessible**
>
> - `field.setAccessible(true)` = "이 필드의 접근 제한자를 무시하고 강제로 읽기/쓰기 허용"
> - private 필드에 값을 넣으려면 **반드시** 필요
> - 빼먹으면 `IllegalAccessException`

```java
ParameterizedType pt = (ParameterizedType) field.getGenericType();
Class<?> itemType = (Class<?>) pt.getActualTypeArguments()[0];
```

> 🔍 **리플렉션 박스 — getGenericType + ParameterizedType**
>
> - 2.8 절 복습: 제네릭은 컴파일 시점에 소거된다.
> - `field.getType()` → `List.class` (T 정보 없음)
> - `field.getGenericType()` → `ParameterizedType` (T 정보 있음)
> - `pt.getActualTypeArguments()[0]` → 첫 번째 T (= `Animal.class`)

```java
if (itemType.isAssignableFrom(e.getKey())) { ... }
```

> 🔍 **리플렉션 박스 — isAssignableFrom (헷갈리는 방향)**
>
> - `Animal.isAssignableFrom(Lion.class)` → `true`
>   - 해석: "`Animal` 변수에 `Lion` 객체를 대입할 수 있는가?" → 가능 (Lion 이 Animal 의 자식)
> - `Lion.isAssignableFrom(Animal.class)` → `false`
> - **방향이 헷갈리면**: "**부모.isAssignableFrom(자식) = true**" 라고 외우세요.

```java
field.set(bean, matches);
```

> 🔍 **리플렉션 박스 — field.set**
>
> - `field.set(obj, value)` = `obj.field = value` 와 같음.
> - 첫 인자 = 어느 객체의 필드인가 (static 이면 `null`)
> - 두 번째 인자 = 넣을 값

#### 단계 4: PostConstruct 호출 (`invokeLifecycle(bean, MyPostConstruct.class)`)

```java
for (Method m : bean.getClass().getDeclaredMethods()) {
    if (m.isAnnotationPresent(ann)) {
        m.setAccessible(true);
        m.invoke(bean);
    }
}
```

> 🔍 **리플렉션 박스 — method.invoke**
>
> - `method.invoke(obj, arg1, arg2, ...)` = `obj.method(arg1, arg2, ...)` 호출
> - 인자가 없는 메서드면 `invoke(obj)` 만 호출
> - **반환값**은 `invoke` 의 리턴 (`Object`). 우리는 무시.

- 의존성이 모두 주입된 **후** 호출되어야 한다는 점이 중요
- 그래서 코드에서 **주입 루프가 끝난 뒤** 호출
- Zoo 의 `open()` 이 `animals.size()` 를 호출할 때 이미 `animals` 가 채워져 있는 이유

#### 단계 5: PreDestroy 호출 (`shutdown`)

```java
List<Object> reverse = new ArrayList<>(beans.values());
Collections.reverse(reverse);
```

**등록의 역순으로 종료**합니다. 왜?

- 일반적으로 나중에 만들어진 Bean 이 먼저 만들어진 Bean 에 의존하기 때문
- 의존 대상이 살아있는 동안 종료되어야 안전 (DB 닫기 전에 Service 가 먼저 정리되어야 함)
- Spring 도 동일한 원칙

### 7.2 `LinkedHashMap` 선택 이유

```java
private final Map<Class<?>, Object> beans = new LinkedHashMap<>();
```

- 일반 `HashMap` 은 **삽입 순서 보장 안 함**
- `LinkedHashMap` 은 **삽입 순서를 그대로 보존**
- 5단계의 "역순 PreDestroy" 를 위해 순서가 필요 → `LinkedHashMap` 선택

### 7.3 `getBean` 의 한계

```java
private Object findBeanByType(Class<?> type) {
    for (Map.Entry<Class<?>, Object> e : beans.entrySet()) {
        if (type.isAssignableFrom(e.getKey())) {
            return e.getValue();   // ← 첫 번째 매치를 반환하고 종료
        }
    }
    return null;
}
```

- 같은 인터페이스에 Bean 이 여러 개 있을 때(`Lion`, `Elephant` 둘 다 `Animal`) `getBean(Animal.class)` 은 **첫 번째 발견된 것만** 반환합니다.
- 이는 의도된 단순화입니다.
- 단, `**List<Animal>` 필드 주입은 모두 모음** (이건 별도 코드 경로)
- Spring 은 같은 타입 다중일 때 `@Qualifier` 로 해결하지만, 이 강의에선 생략.

### ✔️ 중간 검증

```bash
mvn -q compile
```

`MyContainer.class` 가 `target/classes/com/example/myframework/container/` 에 생성되어야 합니다.

---

<a id="sec-8"></a>
## 8. 실행 진입점 — Main

### `Main.java`

```java
package com.example.myframework;

import com.example.myframework.container.MyContainer;
import com.example.myframework.domain.Zoo;

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
```

흐름:

1. 컨테이너 인스턴스 생성
2. 패키지 스캔 → 자동으로 Bean 생성/주입/PostConstruct
3. `Zoo` Bean 을 컨테이너에서 꺼내 사용
4. `shutdown()` → PreDestroy 차례로 호출

---

<a id="sec-9"></a>
## 9. 빌드 및 실행

`framework_example/` 디렉터리에서:

```bash
cd framework_example
mvn -q clean package
java -cp target/classes com.example.myframework.Main
```

> **JAR 로는 실행되지 않습니다** (앞에서 설명한 `jar:file:` URI 한계).
> 반드시 `target/classes` 디렉터리 기준으로 실행해야 합니다.

---

<a id="sec-10"></a>
## 10. 점진적 빌드 — 5단계로 직접 검증하기

지금까지는 "완성된 최종 코드" 를 보여드렸습니다. **이미 따라 작성하셨다면 이 절을 건너뛰어도 좋습니다.** 다만 **처음 만드는 학생** 또는 **각 단계의 효과를 따로 확인하고 싶은 학생** 에게는 아래 5단계 점진 빌드를 추천합니다.

각 단계 끝에 **"이 시점의 출력은 이래야 합니다"** 가 있으니 자기 진도를 검증할 수 있습니다.

### 10.1 단계 1 — 어노테이션만 (컴파일 통과 확인)

이미 4장에서 한 작업. `MyComponent` ~ `MyPreDestroy` 4개만 만들고:

```bash
mvn -q compile
```

`target/classes/com/example/myframework/annotation/` 에 `.class` 4개가 생기면 성공. 출력 없음.

### 10.2 단계 2 — Lion 1개 + 직접 new (IoC 없이 동작 확인)

Lion 만 만들고, Main 을 임시로 이렇게 작성:

```java
public class Main {
    public static void main(String[] args) {
        Lion lion = new Lion();                       // 직접 new
        System.out.println(lion.getName() + ": " + lion.makeSound());
        // @MyPostConstruct 의 born() 은 호출 안 됨!
    }
}
```

실행:

```bash
mvn -q compile
java -cp target/classes com.example.myframework.Main
```

**예상 출력**:

```
레오: 어흥!
```

**관찰 포인트**: `@MyPostConstruct` 가 붙어있는데도 `born()` 이 호출되지 **않습니다.** 왜냐면 컨테이너가 없으니까 어노테이션을 읽어주는 사람이 없기 때문 — **이게 어노테이션의 본질**입니다.

### 10.3 단계 3 — MyContainer 의 "스캔 + 생성" 만 구현

`MyContainer` 의 `scan()` 에서 **1번 블록(Bean 생성)만 남기고 나머지 주석 처리**:

```java
public void scan(String basePackage) throws Exception {
    System.out.println("[Container] '" + basePackage + "' 스캔 시작");
    List<Class<?>> classes = findClasses(basePackage);

    // 1) Bean 생성
    for (Class<?> cls : classes) {
        if (cls.isAnnotationPresent(MyComponent.class)) {
            Object instance = cls.getDeclaredConstructor().newInstance();
            beans.put(cls, instance);
            System.out.println("[Container] Bean 생성: " + cls.getSimpleName());
        }
    }

    // 2) 주입 — 아직 안 함
    // 3) PostConstruct — 아직 안 함
}
```

Main 을 단순화:

```java
MyContainer c = new MyContainer();
c.scan("com.example.myframework");
```

**예상 출력**:

```
[Container] 'com.example.myframework' 스캔 시작
[Container] Bean 생성: Lion
[Container] Bean 생성: Elephant
```

**관찰 포인트**: Bean 은 생성됐지만 Zoo 는 아직 안 만들었거나, 만들었다면 `animals` 가 `null` 인 상태. PostConstruct 도 호출 안 됨.

### 10.4 단계 4 — `@MyInject` 지원 추가 (주입)

`injectDependencies` 와 `scan()` 의 2번 블록 추가, Zoo 클래스도 생성.

**예상 출력 (Zoo 까지)**:

```
[Container] Bean 생성: Lion
[Container] Bean 생성: Elephant
[Container] Bean 생성: Zoo
[Container] 주입: Zoo.animals <- 2 개의 Animal Bean
```

Main 에서 다음 코드 추가:

```java
Zoo zoo = c.getBean(Zoo.class);
zoo.showAll();
```

**추가 출력**:

```
[Zoo] === 동물원 둘러보기 ===
  - 레오 : 어흥!
  - 엘리 : 푸우우~
```

**관찰 포인트**: Zoo 가 `new Lion()`/`new Elephant()` 를 한 번도 호출 안 했는데 동물이 들어있음. 이게 의존성 주입의 마법.

### 10.5 단계 5 — 생명주기 (PostConstruct + PreDestroy)

`scan()` 의 3번 블록 + `shutdown()` 메서드 추가. 4장의 최종 코드가 완성됨.

**최종 출력**: 12장 참조.

> 💡 **점진적 빌드를 다 따라했다면**: 각 단계에서 컨테이너가 무엇을 **추가로 해주는지** 손에 잡힙니다. 이게 Spring 학습의 핵심 감각입니다.

---

<a id="sec-11"></a>
## 11. 자가 검증 — JUnit 테스트

콘솔 출력만으로 정확히 동작했는지 확인하기는 부족합니다. **JUnit 테스트로 자동 검증**해봅시다.

### 11.1 의존성 추가

`pom.xml` 에 JUnit 5 추가:

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- (기존 maven-jar-plugin 유지) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
        </plugin>
    </plugins>
</build>
```

### 11.2 테스트 디렉터리 생성

```bash
mkdir -p src/test/java/com/example/myframework
```

### 11.3 컨테이너 검증 테스트

`src/test/java/com/example/myframework/MyContainerTest.java`:

```java
package com.example.myframework;

import com.example.myframework.container.MyContainer;
import com.example.myframework.domain.Animal;
import com.example.myframework.domain.Zoo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyContainerTest {

    private MyContainer container;

    @BeforeEach
    void setUp() throws Exception {
        container = new MyContainer();
        container.scan("com.example.myframework");
    }

    @AfterEach
    void tearDown() throws Exception {
        container.shutdown();
    }

    @Test
    void Zoo_Bean_이_등록되어야_한다() {
        Zoo zoo = container.getBean(Zoo.class);
        assertNotNull(zoo, "Zoo Bean이 컨테이너에 없습니다");
    }

    @Test
    @SuppressWarnings("unchecked")
    void Zoo_에_동물이_2마리_주입되어야_한다() throws Exception {
        Zoo zoo = container.getBean(Zoo.class);
        Field field = Zoo.class.getDeclaredField("animals");
        field.setAccessible(true);
        List<Animal> animals = (List<Animal>) field.get(zoo);

        assertEquals(2, animals.size(), "동물 수가 2가 아닙니다");
    }

    @Test
    void 존재하지_않는_Bean_을_요청하면_예외가_발생해야_한다() {
        assertThrows(IllegalStateException.class,
            () -> container.getBean(String.class));
    }

    @Test
    void 같은_Bean_을_두_번_조회하면_같은_인스턴스를_반환해야_한다() {
        Zoo zoo1 = container.getBean(Zoo.class);
        Zoo zoo2 = container.getBean(Zoo.class);
        assertSame(zoo1, zoo2, "singleton 보장 실패");
    }
}
```

### 11.4 실행

```bash
mvn test
```

성공하면:

```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

### 11.5 테스트가 알려주는 것

- 컨테이너의 핵심 보장사항(Bean 등록, 주입, singleton, 에러 처리)이 **코드로 명세**됨
- 학생이 자신의 구현이 정답과 다르게 동작하면 **자동으로 발견**
- Spring 학습 시에도 동일한 사고방식 — Bean 동작은 항상 테스트로 검증

### 11.6 점진적 빌드 검증에 응용

10장의 5단계에 대응해 각 단계마다 새 테스트를 추가하면 점진 학습 효과가 극대화됩니다:


| 10장 단계       | 추가할 테스트                     |
| ------------ | --------------------------- |
| 단계 3 (스캔+생성) | Lion/Elephant Bean 이 등록되는가? |
| 단계 4 (주입)    | Zoo.animals 가 비어있지 않은가?     |
| 단계 5 (생명주기)  | (검증 어려움 — 출력 캡처가 필요)        |


> 💡 **응용 과제**: 생명주기 검증을 위해 `System.out` 을 가로채는 테스트를 작성해보세요 (`PrintStream` 교체 패턴).

---

<a id="sec-12"></a>
## 12. 예상 출력과 해석

### 예상 출력

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

### 출력 한 줄씩 해석


| 줄                                     | 의미                                                  |
| ------------------------------------- | --------------------------------------------------- |
| `Bean 생성: Lion/Elephant/Zoo`          | `@MyComponent` 가 붙은 클래스를 발견 → 리플렉션으로 인스턴스 생성        |
| `주입: Zoo.animals <- 2 개의 Animal Bean` | `List<Animal>` 필드에 `Lion`+`Elephant` 두 Bean 을 모아 주입 |
| `[Lion] 태어났다 (PostConstruct)`         | `@MyPostConstruct` 호출. 이때 의존성은 이미 모두 주입된 상태         |
| `[Zoo] 개장! 동물 2 마리`                   | Zoo 의 `open()` 이 `animals.size()` 를 안전하게 호출         |
| `[Zoo] 폐장합니다`                         | shutdown 시작 — 가장 마지막에 등록된 Zoo 가 먼저 정리               |
| `[Elephant/Lion] 잠들었다`                | 등록의 역순(LIFO)으로 PreDestroy 호출                        |


> 💡 **Bean 생성 순서는 OS/JDK 의 파일 탐색 순서에 좌우됩니다.** Lion/Elephant 의 출력 순서가 바뀌어도 정상입니다. 핵심은 **단계의 순서**(생성 → 주입 → PostConstruct → 사용 → PreDestroy)가 유지되는 것입니다.

---

<a id="sec-13"></a>
## 13. Spring 과의 정확한 매핑 (+ 전체 생명주기 표)

### 13.1 동일 예제의 Spring 버전

같은 동물원 예제를 Spring 으로 작성하면:

```java
// Spring 버전
@Component
public class Lion implements Animal {
    @PostConstruct void born() { ... }
    @PreDestroy   void die()  { ... }
    // ...
}

@Component
public class Zoo {
    @Autowired
    private List<Animal> animals;

    @PostConstruct void open() { ... }
    @PreDestroy   void close() { ... }
}

// main
var ctx = new AnnotationConfigApplicationContext("com.example.myframework");
Zoo zoo = ctx.getBean(Zoo.class);
zoo.showAll();
ctx.close();
```

### 13.2 매핑 표


| 우리 미니 프레임워크                    | Spring                                                      |
| ------------------------------ | ----------------------------------------------------------- |
| `@MyComponent`                 | `@Component`, `@Service`, `@Repository`, `@Controller`      |
| `@MyInject`                    | `@Autowired`, `@Inject`, `@Resource`                        |
| `@MyPostConstruct`             | `@PostConstruct` (JSR-250)                                  |
| `@MyPreDestroy`                | `@PreDestroy` (JSR-250)                                     |
| `MyContainer`                  | `ApplicationContext` (`AnnotationConfigApplicationContext`) |
| `container.scan("...")`        | `@ComponentScan("...")` 또는 위 생성자 인자                         |
| `container.getBean(Zoo.class)` | `ctx.getBean(Zoo.class)`                                    |
| `container.shutdown()`         | `ctx.close()` (AutoCloseable)                               |


### 13.3 Spring 의 진짜 Bean 생명주기 (참고)

우리 미니 프레임워크는 PostConstruct/PreDestroy 2단계만 구현했지만, Spring 의 실제 생명주기는 다음과 같이 풍부합니다:


| 단계                       | 콜백 인터페이스 / 어노테이션                                                            | 우리 강의에서 구현? |
| ------------------------ | --------------------------------------------------------------------------- | ----------- |
| 1. 인스턴스 생성               | (생성자 호출)                                                                    | ✅           |
| 2. 이름 인식                 | `BeanNameAware.setBeanName(String)`                                         | ❌           |
| 3. BeanFactory 인식        | `BeanFactoryAware.setBeanFactory(...)`                                      | ❌           |
| 4. ApplicationContext 인식 | `ApplicationContextAware.setApplicationContext(...)`                        | ❌           |
| 5. 의존성 주입                | `@Autowired`, `@Inject`, 생성자 주입                                             | ✅ (필드 주입만)  |
| 6. **초기화 직전 후처리**        | `BeanPostProcessor#postProcessBeforeInitialization`                         | ❌           |
| 7. `**@PostConstruct`**  | JSR-250                                                                     | ✅           |
| 8. 사용자 정의 init           | `InitializingBean.afterPropertiesSet()` 또는 `@Bean(initMethod=...)`          | ❌           |
| 9. **초기화 직후 후처리**        | `BeanPostProcessor#postProcessAfterInitialization` (← **AOP 프록시가 여기서 끼워짐**) | ❌           |
| 10. **사용 (Ready)**       | (애플리케이션 동작)                                                                 | ✅           |
| 11. `**@PreDestroy`**    | JSR-250                                                                     | ✅           |
| 12. 사용자 정의 destroy       | `DisposableBean.destroy()` 또는 `@Bean(destroyMethod=...)`                    | ❌           |


**핵심 통찰**:

- 우리는 1, 5, 7, 10, 11 만 구현 (= 핵심 5단계)
- 나머지는 모두 그 위에 쌓은 **추가 hook 포인트**
- 특히 **단계 9 의 `BeanPostProcessor#postProcessAfterInitialization` 는 AOP 프록시가 Bean 을 감싸는 자리** — 15.1 에서 본 프록시 생성이 정확히 여기서 일어납니다.

---

<a id="sec-14"></a>
## 14. Spring Boot에서 동일 예제 재현

우리 미니 프레임워크의 동물원 예제를 **진짜 Spring Boot 로 똑같이** 만들어봅니다. 차이를 비교하면 우리가 만든 것이 무엇을 자동화한 건지가 명확해집니다.

### 14.1 새 Spring Boot 프로젝트 만들기

가장 빠른 방법: [https://start.spring.io](https://start.spring.io) 에서:

- Project: Maven
- Language: Java
- Spring Boot: 3.5.x
- Dependencies: 없음 (기본 IoC 만 필요)
- Group: `com.example`
- Artifact: `spring-zoo`

또는 상위 프로젝트의 `backend/` 모듈을 그대로 활용해도 됩니다.

### 14.2 어노테이션을 모두 Spring 표준으로 교체


| 우리 강의의 어노테이션       | Spring 등가 (그대로 치환)                                                      |
| ------------------ | ----------------------------------------------------------------------- |
| `@MyComponent`     | `@Component` (`org.springframework.stereotype.Component`)               |
| `@MyInject`        | `@Autowired` (`org.springframework.beans.factory.annotation.Autowired`) |
| `@MyPostConstruct` | `@PostConstruct` (`jakarta.annotation.PostConstruct`)                   |
| `@MyPreDestroy`    | `@PreDestroy` (`jakarta.annotation.PreDestroy`)                         |


### 14.3 도메인 코드 (변경된 부분만)

```java
// Lion.java
package com.example.zoo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class Lion implements Animal {
    private final String name = "레오";

    @PostConstruct
    public void born() {
        System.out.println("[Lion] " + name + " 태어났다!");
    }
    // ... 나머지 동일

    @PreDestroy
    public void die() {
        System.out.println("[Lion] " + name + " 잠들었다...");
    }
}
```

```java
// Zoo.java
@Component
public class Zoo {
    @Autowired
    private List<Animal> animals;

    @PostConstruct
    public void open() { ... }

    @PreDestroy
    public void close() { ... }
    // ...
}
```

### 14.4 Main — Spring ApplicationContext 사용

**옵션 A: `AnnotationConfigApplicationContext` 직접 사용 (우리 강의와 가장 비슷)**

```java
package com.example.zoo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext("com.example.zoo");

        Zoo zoo = ctx.getBean(Zoo.class);
        zoo.showAll();

        ctx.close();   // ← @PreDestroy 자동 호출
    }
}
```

**옵션 B: Spring Boot 스타일 (실무 표준)**

```java
@SpringBootApplication
public class ZooApplication implements CommandLineRunner {

    private final Zoo zoo;

    public ZooApplication(Zoo zoo) {  // ← 생성자 주입
        this.zoo = zoo;
    }

    @Override
    public void run(String... args) {
        zoo.showAll();
    }

    public static void main(String[] args) {
        SpringApplication.run(ZooApplication.class, args);
    }
}
```

### 14.5 비교 — 우리 코드 ↔ Spring 코드


| 우리가 직접 만든 것                                  | Spring 이 자동으로 해주는 것                              |
| -------------------------------------------- | ------------------------------------------------ |
| `findClasses(...)` (파일 스캔)                   | `@ComponentScan` (JAR 안까지 자동)                    |
| `cls.getDeclaredConstructor().newInstance()` | `BeanDefinitionReader` + `InstantiationStrategy` |
| `@MyInject` 처리 (`injectDependencies`)        | `AutowiredAnnotationBeanPostProcessor`           |
| `@MyPostConstruct` 호출                        | `CommonAnnotationBeanPostProcessor`              |
| `shutdown()` 의 역순 PreDestroy                 | `DefaultListableBeanFactory#destroyBeans`        |
| `LinkedHashMap` Bean 저장                      | `DefaultListableBeanFactory#beanDefinitionMap`   |
| `getBean(Class)`                             | `BeanFactory#getBean(Class)`                     |


### 14.6 실행 결과 비교

**우리 미니 프레임워크**:

```
[Container] Bean 생성: Lion
[Container] 주입: Zoo.animals <- 2 개의 Animal Bean
[Lion] 레오 태어났다!
...
```

**Spring Boot**:

```
[Lion] 레오 태어났다!
[Elephant] 엘리 태어났다!
[Zoo] 개장!
... (Spring 의 자체 로그는 별도로 잔뜩 출력)
```

→ **개발자 관점에서는 거의 동일한 코드**. Spring 은 거기에 트랜잭션/AOP/보안 등을 더 얹은 것.

### 14.7 자기 진단 체크리스트

위 Spring 코드를 보고 다음을 즉시 답할 수 있다면 IoC 핵심을 이해한 것입니다:

- `@Component` 가 없으면 어떻게 되나? → Bean 등록 안 됨, `getBean` 호출 시 `NoSuchBeanDefinitionException`
- `@Autowired` 필드가 `List<Animal>` 이 아니라 `Animal` 하나면? → 2개라서 `NoUniqueBeanDefinitionException`
- `@PostConstruct` 가 호출되는 시점은? → 의존성 주입 완료 후, 사용자 코드 진입 전
- `ctx.close()` 가 호출되지 않으면? → `@PreDestroy` 안 불림, 리소스 누수 가능
- `Lion` 과 `Elephant` 가 모두 `Animal` 인데 `@Autowired Animal a` 라면? → `@Qualifier` 또는 `@Primary` 필요

답을 못한 게 있으면 해당 챕터(7장, 13장)로 돌아가세요.

---

<a id="sec-15"></a>
## 15. Spring 이 추가로 해주는 것들 + AOP 맛보기

### 15.1 AOP 의 본질 — 동적 프록시 30줄

13장에서 "왜 IoC 가 핵심이면 AOP 가 가능한가?" 라는 질문을 던졌습니다. 답을 코드로 보여드립니다.

**핵심 아이디어**: 컨테이너가 Bean 을 **직접 등록하지 않고**, **그 Bean 을 감싼 프록시**를 등록합니다. 사용자는 프록시를 통해서만 Bean 을 호출하므로, **호출 전후에 무엇이든 끼워넣을 수 있습니다**.

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 1. 원본 객체
Animal real = new Lion();

// 2. 프록시 생성 — Animal 인터페이스를 구현하는 동적 객체
Animal proxy = (Animal) Proxy.newProxyInstance(
    Animal.class.getClassLoader(),
    new Class<?>[] { Animal.class },
    new InvocationHandler() {
        @Override
        public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
            // [전] 메서드 호출 전
            long start = System.nanoTime();
            System.out.println("[AOP] " + method.getName() + " 호출 시작");

            // 진짜 메서드 실행
            Object result = method.invoke(real, args);

            // [후] 메서드 호출 후
            long elapsed = System.nanoTime() - start;
            System.out.println("[AOP] " + method.getName() + " 종료, " + elapsed + " ns");

            return result;
        }
    }
);

// 3. 사용자 코드 — 프록시인지 진짜인지 구분 못함
String sound = proxy.makeSound();
```

**출력**:

```
[AOP] makeSound 호출 시작
[AOP] makeSound 종료, 12345 ns
```

**핵심 통찰**:

- 사용자 코드는 `proxy.makeSound()` 만 호출. 자신이 프록시를 호출하는지 모름.
- 컨테이너가 **Bean 대신 프록시를 등록**하면 → 모든 메서드 호출이 자동으로 가로채짐.
- 이걸로 만들 수 있는 것: **자동 로깅, 트랜잭션 시작/커밋, 권한 검사, 캐싱, 측정** — 즉 `@Transactional`, `@Cacheable`, `@PreAuthorize` 의 정체.

**Spring 은 어떻게?**

- 인터페이스가 있는 Bean → **JDK Dynamic Proxy** (위 예시와 동일)
- 인터페이스가 없는 Bean (구체 클래스만) → **CGLIB** Bytecode 생성으로 서브클래스 생성
- 둘 다 컨테이너가 자동 선택

→ **"IoC 가 모든 것의 출발점" 이라는 13장의 주장이 이제 코드로 증명됩니다.** 컨테이너가 Bean 을 쥐고 있어야만 프록시로 갈아치울 수 있습니다.

### 15.2 우리가 의도적으로 안 만든 것들

이 미니 프레임워크는 **본질만** 보여줍니다. Spring 은 그 위에 거대한 레이어를 더 얹습니다:


| Spring 추가 기능                            | 왜 우리가 생략했나              |
| --------------------------------------- | ----------------------- |
| 생성자 주입 / setter 주입                      | 필드 주입 하나만으로 핵심 개념 전달 가능 |
| 스코프 (singleton / prototype / request 등) | 모든 Bean 이 사실상 singleton |
| 순환 참조 감지                                | 예제는 의도적으로 순환 없게 설계      |
| `@Configuration` + `@Bean` 메서드          | 컴포넌트 스캔 하나로 충분          |
| `@Qualifier` 로 동명 타입 구분                 | 동물 종류가 적어 불필요           |
| AOP / 프록시 (`@Transactional` 등)          | 15.1 에서 30줄로 맛만 보여드림    |
| JAR 내 스캔                                | 학습 범위 좁히기 (파일시스템만 지원)   |
| 이벤트 (`ApplicationEventPublisher`)       | 핵심 흐름과 무관               |
| `ApplicationContext` 계층 (parent/child)  | 단일 컨테이너로 충분             |


---

<a id="sec-16"></a>
## 16. 흔한 에러와 해결법

학생이 자주 만나는 7가지 에러를 정리합니다. 막혔을 때 이 표를 먼저 확인하세요.

### 16.1 `java.lang.NoSuchMethodException: ...Lion.<init>()`

```
java.lang.NoSuchMethodException: com.example.myframework.domain.Lion.<init>()
    at java.base/java.lang.Class.getConstructor0(...)
```

- **원인**: `@MyComponent` 클래스에 **public 기본 생성자가 없음**. 명시적인 다른 생성자를 만들면 컴파일러가 기본 생성자를 자동 생성하지 않음.
- **해결**: 인자 없는 public 생성자를 추가하거나, 다른 생성자 자체를 제거.
- **예시**:
  ```java
  @MyComponent
  public class Lion {
      public Lion() {}   // ← 명시적으로 기본 생성자 추가
      public Lion(String name) { ... }
  }
  ```

### 16.2 `java.lang.IllegalAccessException: Class ... cannot access ...`

- **원인**: private 필드/메서드에 `setAccessible(true)` 를 빼먹음.
- **해결**: 리플렉션 코드에서 `field.setAccessible(true)` / `method.setAccessible(true)` 호출 후 사용.

### 16.3 `@MyInject` 필드가 `null` (NullPointerException)

- **원인 후보 (가능성 높은 순)**:
  1. `@MyInject` 어노테이션의 `@Retention` 이 `RUNTIME` 이 아님 (예: `SOURCE`) → 컨테이너가 어노테이션을 못 봄
  2. 주입 대상 클래스에 `@MyComponent` 안 붙임 → Bean 등록 자체가 안 됨
  3. `findBeanByType` 의 `isAssignableFrom` 방향 헷갈림 → 잘못된 Bean 매칭
- **해결**: 위 3가지를 차례로 점검.

### 16.4 `URI is not hierarchical`

```
Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
    at java.base/java.io.File.<init>(File.java:420)
```

- **원인**: `java -jar target/myframework-1.0.0.jar` 로 실행. JAR 안의 `jar:file:` URI 를 `new File(URI)` 가 처리 못함.
- **해결**: `java -cp target/classes com.example.myframework.Main` 사용.

### 16.5 `ClassCastException: ...Type cannot be cast to ParameterizedType`

- **원인**: `@MyInject` 가 붙은 필드가 `List<Animal>` 이 아니라 raw `List` 로 선언됨.
- **해결**: 항상 제네릭 타입 명시:
  ```java
  @MyInject
  private List<Animal> animals;   // OK
  // private List animals;        // 에러
  ```

### 16.6 `IllegalStateException: Bean 을 찾을 수 없습니다`

- **원인 후보**:
  1. `MyContainer.scan("com.example.myframework")` 의 패키지 이름 오타
  2. 클래스가 다른 패키지에 있어서 스캔 범위 밖
  3. `@MyComponent` 안 붙임
- **해결**: 패키지 경로와 어노테이션 부착 확인.

### 16.7 한글 깨짐 (mojibake)

```
[Lion] ?? 태어났다!
```

- **원인**: 콘솔/터미널의 인코딩이 UTF-8 이 아님 (특히 Windows cmd).
- **해결 (Windows)**:
  ```bash
  chcp 65001
  java -Dfile.encoding=UTF-8 -cp target/classes com.example.myframework.Main
  ```
- 또는 IDE 의 콘솔에서 실행 (대부분 UTF-8 기본).

---

<a id="sec-17"></a>
## 17. IDE 사용 안내

CLI(`mvn`, `java`) 만으로 작업해도 되지만, 대부분의 학생은 IDE 를 씁니다. 추천 3가지의 빠른 사용법.

### 17.1 IntelliJ IDEA Community (가장 추천, 무료)

1. 다운로드: [https://www.jetbrains.com/idea/download](https://www.jetbrains.com/idea/download)
2. **File → Open** → `framework_example/` 폴더 선택
3. IntelliJ 가 자동으로 Maven 프로젝트로 인식 → 의존성 다운로드 (이 예제는 의존성 없으므로 즉시 끝남)
4. 좌측 트리에서 `Main.java` 우클릭 → **Run 'Main.main()'**
5. 콘솔에 출력 확인

**팁**:

- Project SDK 가 21 인지 확인: `File → Project Structure → Project SDK`
- 출력 한글 깨지면: `Help → Edit Custom VM Options` → `-Dfile.encoding=UTF-8` 추가

### 17.2 Cursor / VS Code

1. **Extension Pack for Java** 설치 (Microsoft 가 제공하는 확장팩)
2. **File → Open Folder** → `framework_example/` 선택
3. 좌측 Explorer 에서 `Main.java` 열기
4. `main` 메서드 위의 ▶ **Run** 버튼 클릭 (또는 F5)
5. 터미널에 출력

**팁**:

- JDK 21 인식 안 되면: `Cmd+Shift+P` → "Java: Configure Runtime" → JDK 21 선택
- 자바 확장팩이 `target/classes` 경로를 자동으로 설정해 줌

### 17.3 Eclipse

1. **File → Import → Existing Maven Projects** → `framework_example/` 선택
2. 좌측 Project Explorer 에서 `Main.java` 우클릭 → **Run As → Java Application**

### 17.4 IDE 사용 시 주의

- **IDE 가 `java -jar` 로 실행하지 않게 주의** — 일부 IDE 는 빌드 후 JAR 로 실행. 이 강의 컨테이너는 JAR 스캔 미지원이므로 깨짐.
- 항상 **Main 클래스를 직접 실행** (= `target/classes` 기준 실행) 하도록 설정.

---

<a id="sec-18"></a>
## 18. 직접 확장해 보기 (과제)

미니 프레임워크를 더 깊이 이해하려면 다음 과제를 추천:

### 과제 1: 새 동물 추가

- `Penguin implements Animal` 을 만들고 `@MyComponent` 부착
- `Zoo` 코드를 **한 줄도 안 고치고** 펭귄이 자동으로 등록되는지 확인
- → **OCP(개방-폐쇄 원칙)** 체험

### 과제 2: 생성자 주입 지원

- `@MyInject` 를 생성자에도 붙일 수 있게 `@Target({FIELD, CONSTRUCTOR})` 로 확장
- `getDeclaredConstructor()` 대신 `@MyInject` 가 붙은 생성자를 찾아 인자를 주입
- 힌트: 필드 주입은 인스턴스 생성 후, 생성자 주입은 인스턴스 생성 시점에 해결해야 한다

### 과제 3: 의존성 그래프 정렬 (topological sort)

- 현재 코드는 "생성 → 한꺼번에 주입" 순서지만, Spring 은 **의존 순서대로** 생성한다
- 그래프 만들어서 위상정렬 후 그 순서로 인스턴스 생성/PostConstruct 호출

### 과제 4: AOP 정식 구현

- 15.1 의 프록시 예제를 컨테이너에 통합
- `@MyLog` 어노테이션을 만들어 메서드 호출 전후 로그 출력
- 컨테이너가 Bean 등록 시점에 자동으로 프록시로 감싸도록 변경

### 과제 5: JAR 스캔 지원

- `ClassLoader.getResources()` 가 반환한 URL 이 `jar:` 인 경우 `JarFile` 로 열어서 내부 `.class` 찾기
- → 실제 Spring 의 `PathMatchingResourcePatternResolver` 와 비슷한 동작

### 과제 6: JUnit 테스트 보강 (11장 응용)

- 점진적 빌드(10장) 각 단계에 대응하는 테스트 추가
- 생명주기 검증을 위해 `System.out` 을 가로채는 패턴 적용

---

<a id="sec-19"></a>
## 19. 한 줄 요약

> 어노테이션 4개 + 리플렉션 + 약 150줄의 컨테이너 코드 = **Spring 의 본질**.  
> 나머지 Spring 의 거대함은 모두 이 본질 위에 쌓아 올린 **확장**입니다.  
> 이제 Spring 문서를 펼치면, 그 안의 모든 어노테이션이 **무엇을 자동화하고 있는지** 보이기 시작할 겁니다.

