# SDKMAN Java 버전 관리 사용법 정리
## 1) SDKMAN 설치/초기화
- 설치:
  - `curl -s "https://get.sdkman.io" | bash`
- 현재 셸에서 즉시 로드:
  - `source "$HOME/.sdkman/bin/sdkman-init.sh"`
- 동작 확인:
  - `sdk version`

## 2) 설치 가능한 Java 버전 보기
- 전체 후보 확인:
  - `sdk list java`

## 3) Java 설치 (예: 21 LTS)
- 설치:
  - `sdk install java 21-tem`
- 특정 패치 버전 설치 예:
  - `sdk install java 21.0.8-tem`

## 4) 이미 설치된(Homebrew 등) Java를 SDKMAN에 등록
- JDK 경로 확인(macOS):
  - `/usr/libexec/java_home -V`
- 로컬 버전으로 등록:
  - `sdk install java 26-local /opt/homebrew/Cellar/openjdk/26.0.1/libexec/openjdk.jdk/Contents/Home`
- 핵심 형식:
  - `sdk install <candidate> <version-name> <absolute-path>`

## 5) 설치된(Java) 버전 목록 확인
- 설치/로컬 등록된 Java만 보기:
  - `sdk list java | grep -E "installed|local only"`
- 현재 활성 버전 확인:
  - `sdk current java`

## 6) 버전 적용 범위 (세션/글로벌/폴더)
### (a) 현재 셸(임시)
- `sdk use java 21-tem`
- 현재 터미널 세션에서만 유효

### (b) 글로벌 기본값
- `sdk default java 21-tem`
- 새 터미널 포함 전체 기본값으로 유효

### (c) 특정 폴더(프로젝트) 전용
- 해당 폴더에서:
  - `sdk env init`
- `.sdkmanrc`에 예시:
  - `java=21-tem`
- 적용:
  - `sdk env`

## 7) 자주 헷갈리는 점
- `java -version`:
  - PATH 상의 시스템 Java를 보여줌 (예: Homebrew OpenJDK)
- `sdk current java`:
  - SDKMAN이 현재 선택한 Java를 보여줌
- 따라서 시스템 Java가 있어도 SDKMAN에서 선택하지 않으면 `No current version`이 나올 수 있음

## 8) grep `-E` 옵션 의미
- `-E`는 확장 정규식(ERE) 사용 옵션
- 예: `grep -E "installed|local only"`에서 `|`(OR)를 바로 사용 가능

---

# SDKMAN vs Maven/Gradle Toolchains (혼동 방지)

## 9) 핵심 개념: "어디서 Java 버전을 고르는가"
- Java 버전을 선택하는 방법은 **레이어가 다르다.**
  - (A) **셸 레이어**: 터미널에서 `java`, `javac`, `mvn` 명령 자체가 어떤 JDK를 가리키는가
  - (B) **빌드 레이어**: Maven/Gradle 빌드 작업이 내부적으로 어떤 JDK를 사용해 컴파일/실행하는가
- 두 레이어는 **서로 독립**이고 각각 다른 도구가 담당한다.

| 레이어 | 담당 도구 | 영향 범위 |
|--------|-----------|-----------|
| (A) 셸 레이어 | SDKMAN, Homebrew, 수동 PATH 설정 | 터미널 전체 — IDE 실행, `java -jar`, 테스트, `mvn` 명령 자체 |
| (B) 빌드 레이어 | Maven Toolchains, Gradle Toolchains | 빌드 작업 내부에서만 — `mvn compile`이 쓰는 JDK |

## 10) Maven은 Java 버전을 어떻게 다루는가
### (a) `pom.xml`의 `<java.version>21</java.version>`은 무엇인가
- 이 프로젝트의 `backend/pom.xml`에 있는 설정
- 의미: **"컴파일된 바이트코드의 타겟 Java 버전"** 만 지정
- **빌드를 실행하는 JDK 자체를 강제하지 않는다.**
  - 예: 시스템에 Java 17만 깔려 있고 `JAVA_HOME=Java17`이면 → `mvn`은 Java 17로 동작하다가 "21 타겟 필요" 에러를 낸다.
- 즉, `pom.xml`의 `<java.version>`은 "원하는 타겟"을 적은 것일 뿐, "실제 사용 JDK"는 **`mvn` 실행 시점의 `JAVA_HOME`을 따른다.**

### (b) 진짜로 JDK를 고정하려면: maven-toolchains-plugin
- `~/.m2/toolchains.xml`에 사용할 JDK들을 등록:
  ```xml
  <toolchains>
    <toolchain>
      <type>jdk</type>
      <provides>
        <version>21</version>
        <vendor>temurin</vendor>
      </provides>
      <configuration>
        <jdkHome>/Users/eugene/.sdkman/candidates/java/21-tem</jdkHome>
      </configuration>
    </toolchain>
  </toolchains>
  ```
- `pom.xml`에 `maven-toolchains-plugin` 추가:
  ```xml
  <plugin>
    <artifactId>maven-toolchains-plugin</artifactId>
    <configuration>
      <toolchains>
        <jdk>
          <version>21</version>
          <vendor>temurin</vendor>
        </jdk>
      </toolchains>
    </configuration>
  </plugin>
  ```
- 효과: 셸의 `JAVA_HOME`이 17이어도, **Maven은 컴파일 시점에 등록된 21 JDK를 사용한다.**
- 단점: 설정이 번거롭고 `toolchains.xml`은 머신마다 따로 둬야 함 (절대 경로 때문)

## 11) Gradle은 Java 버전을 어떻게 다루는가
### (a) Gradle Toolchains — 한 줄로 끝남
- `build.gradle`에 추가:
  ```groovy
  java {
      toolchain {
          languageVersion = JavaLanguageVersion.of(21)
      }
  }
  ```
- 효과:
  - 시스템에 Java 21이 있으면 자동 탐지 후 사용
  - 없으면 **Gradle이 자동으로 다운로드** (Foojay API 사용)
- Maven보다 압도적으로 편리한 부분

### (b) 셸의 JDK와 독립
- 셸 `JAVA_HOME`이 17이어도 Gradle은 빌드에 21을 쓴다.
- 단, Gradle 데몬 자체는 셸 JDK로 실행됨 → 보통은 신경 안 써도 됨

## 12) 그럼 SDKMAN은 왜 따로 필요한가
- Toolchains는 **빌드만** 특정 JDK로 강제할 뿐, 다음 작업은 여전히 셸 JDK를 따른다:
  - 터미널에서 `java -jar app.jar` 직접 실행
  - 터미널에서 `javac Foo.java` 단발 컴파일
  - IDE(IntelliJ, VSCode)가 프로젝트를 인식할 때 기본 JDK 추측
  - `mvn` 명령 자체의 실행 (Maven 자체는 셸 JDK로 동작)
  - 테스트 러너, 디버거 등
- SDKMAN은 **셸 전체의 `java`/`javac`/`mvn`** 을 한 번에 바꾼다 → 위 모든 시나리오에 영향
- 즉:
  - **셸 작업 일관성** = SDKMAN의 영역
  - **빌드 재현성** = Toolchains의 영역

## 13) 그래서 무엇을 쓸 것인가 (의사결정표)

| 상황 | 권장 |
|------|------|
| 혼자 학습 프로젝트, 머신 1대 | SDKMAN만 (Toolchains 불필요) |
| 팀 프로젝트, 모두가 동일 환경 원함 | SDKMAN `.sdkmanrc` + (선택) Gradle Toolchains |
| CI/CD 빌드 재현성이 절대 중요 | Maven/Gradle Toolchains 필수 |
| 한 머신에서 여러 프로젝트가 서로 다른 JDK 사용 | SDKMAN `.sdkmanrc` (폴더별 자동 전환) |
| Gradle 프로젝트 + JDK 자동 다운로드 원함 | Gradle Toolchains 단독으로도 OK |

### 실무 권장 조합
- **SDKMAN으로 셸 JDK를 맞추고, Toolchains로 빌드 JDK를 한 번 더 못박는다.**
- 이유: 두 레이어를 모두 잠가야 "내 컴에선 되는데" 문제를 완전히 차단할 수 있음

## 14) 이 프로젝트(spring-react-ioc-crud-study)에 적용한다면
- 현재 상태: `pom.xml`에 `<java.version>21</java.version>`만 있음 → **실제 빌드 JDK는 셸 `JAVA_HOME`을 따름**
- 추천 단계:
  1. `sdk install java 21-tem`
  2. 프로젝트 루트에서 `sdk env init` → `.sdkmanrc` 생성 (`java=21-tem`)
  3. (선택) `maven-toolchains-plugin` 추가는 학습 프로젝트엔 오버킬 — 생략 가능
- 이렇게 하면: 프로젝트 폴더 진입 시 SDKMAN이 자동으로 Java 21로 전환 → `mvn` 명령이 Java 21로 동작 → `pom.xml`의 타겟과 일치

## 15) 한 줄 요약
- `pom.xml`의 `<java.version>` = **"내가 원하는 타겟"** (희망사항)
- `JAVA_HOME` / SDKMAN = **"실제 mvn이 쓰는 JDK"** (현실)
- Maven/Gradle Toolchains = **"빌드 시점에만 강제로 쓸 JDK"** (빌드 전용 잠금장치)
- 세 가지가 일치해야 빌드가 깔끔하게 돌아간다.
