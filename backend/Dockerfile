# =========================================================
# Stage 1: 빌드 (JDK 21 + Maven Wrapper로 컴파일/패키징)
#   - Maven은 mvnw가 자동으로 다운로드하므로 베이스 이미지는 JDK만 있으면 됨
#   - pom.xml만 먼저 복사해 의존성 캐시 레이어를 분리
# =========================================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# 1) Maven Wrapper와 pom.xml 먼저 복사 (의존성 캐시 활용)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -B -q dependency:go-offline

# 2) 소스 복사 후 패키징 (테스트는 CI에서 따로 돌린다고 가정)
COPY src ./src
RUN ./mvnw -B -q package -DskipTests

# =========================================================
# Stage 2: 실행 (JRE만 들고 작은 이미지로)
#   - JDK 대신 JRE 사용 → 이미지 크기 절감
#   - non-root 사용자로 실행 (보안 모범사례)
# =========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# non-root 사용자 생성 후 전환
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 빌드 산출물 JAR만 복사
COPY --from=build /app/target/study-backend-0.0.1-SNAPSHOT.jar app.jar

# Spring Boot 기본 포트
EXPOSE 8080

# 컨테이너 시작 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
