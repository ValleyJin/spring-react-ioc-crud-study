# Spring Boot IoC + React Hooks + TanStack Query CRUD Study Project

이 프로젝트는 **Spring Boot의 IoC / DI / Bean / 계층형 백엔드 구조**와  
**Vite + React의 Hooks / TanStack Query 기반 서버 연동 CRUD / 렌더링 최적화**를  
한 번에 학습하기 위한 풀스택 예제입니다.

## 이 프로젝트로 배우는 것

### 백엔드
- Spring Boot의 **IoC(Inversion of Control)** 와 **DI(Dependency Injection)**
- **IoC 컨테이너가 Bean을 생성하고 연결하는 방식**
- Controller → Service → Repository 계층 구조
- DTO 분리, 예외 처리, CORS 설정
- H2 인메모리 데이터베이스와 JPA 기반 CRUD

### 프론트엔드
- Vite 기반 React 프로젝트 구조
- React의 핵심 hook
  - `useState`
  - `useReducer`
  - `useEffect`
  - `useRef`
  - `useMemo`
  - `useCallback`
  - `useDeferredValue`
- TanStack Query를 이용한 서버 상태 관리
- Query / Mutation / invalidate / optimistic update
- `React.memo`와 참조 안정성에 기반한 렌더링 최적화

## 기술 스택

- Backend: Java 21, Spring Boot 3.5.14, Spring Web, Spring Data JPA, H2
- Frontend: React 19.2.5, Vite 8.0.10, TanStack Query 5.100.6

## 폴더 구조

```text
spring-react-ioc-crud-study/
├─ backend/
│  ├─ pom.xml
│  └─ src/
├─ frontend/
│  ├─ package.json
│  ├─ vite.config.js
│  └─ src/
├─ 강의.md
└─ README.md
```

## 실행 방법

### 1) 백엔드 실행
`backend` 폴더에서 Maven으로 실행합니다.

```bash
cd backend
mvn spring-boot:run
```

기본 포트:
- `http://localhost:8080`

H2 콘솔:
- `http://localhost:8080/h2-console`

JDBC URL:
- `jdbc:h2:mem:studydb`

### 2) 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

기본 포트:
- `http://localhost:5173`

## 주요 화면 기능

- 서버에서 할 일 목록 조회
- 새 할 일 생성
- 기존 할 일 수정
- 완료/미완료 토글
- 삭제
- 검색 / 상태 필터
- 통계 패널
- 렌더 횟수 표시로 최적화 효과 관찰

## 학습 순서 추천

1. `강의.md`를 먼저 읽어 전체 구조를 잡는다.
2. 백엔드에서 `StudyApplication` → `AppBeanConfig` → `TaskController` → `TaskServiceImpl` → `TaskRepository` 순서로 읽는다.
3. 프론트에서 `main.jsx` → `App.jsx` → `api/taskApi.js` → `hooks/*` → `components/*` 순서로 읽는다.
4. 그 다음 직접 기능을 하나씩 바꿔 본다.

## 실습 확장 아이디어

- 로그인 / JWT 추가
- Pagination 추가
- 정렬 기준 추가
- 카테고리 / 태그 추가
- 낙관적 업데이트 범위 확대
- 테스트 코드 작성
- 실제 DB(MySQL/PostgreSQL)로 교체
