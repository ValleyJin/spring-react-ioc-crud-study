export default function Layout({ children }) {
  return (
    <div className="page-shell">
      <header className="hero">
        <div>
          <p className="eyebrow">Spring Boot + React Fullstack Study</p>
          <h1>IoC · TanStack Query · Hooks · Optimization</h1>
          <p className="hero-copy">
            백엔드는 Spring Boot의 IoC/DI를, 프론트는 React hooks와 서버 상태 관리를 한 번에 체험하는 학습용 프로젝트입니다.
          </p>
        </div>
      </header>

      <main className="grid-layout">{children}</main>
    </div>
  );
}
