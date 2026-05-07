import { memo } from "react";
import { useRenderCount } from "../hooks/useRenderCount";

/**
 * 하나의 Task를 표현하는 행 컴포넌트.
 *
 * React.memo가 붙어 있으므로,
 * 부모가 다시 렌더되어도 props가 같다면 이 행은 재렌더를 생략할 수 있다.
 *
 * 단, 주의:
 * - 함수 props(onToggle, onDelete, onEdit)가 매 렌더마다 새로 생성되면
 *   memo 효과가 줄어든다.
 * - 그래서 부모에서 useCallback으로 참조를 안정화한다.
 */
function TaskRow({ task, onEdit, onToggle, onDelete }) {
  const renderCount = useRenderCount();

  return (
    <article className="task-row">
      <div className="task-main">
        <div className="task-title-row">
          <h3>{task.title}</h3>
          <span className={`badge badge-${task.status.toLowerCase()}`}>
            {task.status}
          </span>
        </div>

        <p className="task-description">{task.description}</p>

        <div className="task-meta">
          <span>Priority: {task.priority}</span>
          <span>Render count: {renderCount}</span>
          <span>Updated: {new Date(task.updatedAt).toLocaleString()}</span>
        </div>
      </div>

      <div className="task-actions">
        <button onClick={() => onEdit(task)}>수정</button>
        <button onClick={() => onToggle(task.id)}>
          {task.status === "DONE" ? "미완료로" : "완료로"}
        </button>
        <button className="danger" onClick={() => onDelete(task.id)}>
          삭제
        </button>
      </div>
    </article>
  );
}

export default memo(TaskRow);
