import { memo } from "react";

/**
 * 통계 패널.
 * 계산 자체는 부모(App)에서 useMemo로 수행하고,
 * 이 컴포넌트는 받은 데이터를 표현만 한다.
 */
function TaskStats({ stats }) {
  return (
    <section className="card">
      <h2>통계</h2>

      <div className="stats-grid">
        <article className="stat-box">
          <strong>{stats.total}</strong>
          <span>Total</span>
        </article>
        <article className="stat-box">
          <strong>{stats.todo}</strong>
          <span>TODO</span>
        </article>
        <article className="stat-box">
          <strong>{stats.doing}</strong>
          <span>DOING</span>
        </article>
        <article className="stat-box">
          <strong>{stats.done}</strong>
          <span>DONE</span>
        </article>
        <article className="stat-box">
          <strong>{stats.avgPriority.toFixed(1)}</strong>
          <span>평균 우선순위</span>
        </article>
      </div>
    </section>
  );
}

export default memo(TaskStats);
