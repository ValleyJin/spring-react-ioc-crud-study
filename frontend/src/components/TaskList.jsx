import { memo } from "react";
import TaskRow from "./TaskRow";

/**
 * 목록 전체를 담당한다.
 * props가 바뀌지 않으면 TaskList도 memo 덕분에 재렌더를 생략할 수 있다.
 */
function TaskList({
  tasks,
  isLoading,
  isFetching,
  error,
  onEdit,
  onToggle,
  onDelete
}) {
  return (
    <section className="card">
      <div className="section-title-row">
        <h2>Task 목록</h2>
        <div className="fetch-badges">
          {isLoading && <span className="mini-badge">초기 로딩 중</span>}
          {!isLoading && isFetching && <span className="mini-badge">백그라운드 갱신 중</span>}
        </div>
      </div>

      {error ? (
        <div className="error-box">{error.message}</div>
      ) : null}

      {!isLoading && tasks.length === 0 ? (
        <div className="empty-box">조건에 맞는 Task가 없습니다.</div>
      ) : null}

      <div className="stack-md">
        {tasks.map((task) => (
          <TaskRow
            key={task.id}
            task={task}
            onEdit={onEdit}
            onToggle={onToggle}
            onDelete={onDelete}
          />
        ))}
      </div>
    </section>
  );
}

export default memo(TaskList);
