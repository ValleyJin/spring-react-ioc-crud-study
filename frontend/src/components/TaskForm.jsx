import { useEffect, useMemo, useReducer, useRef } from "react";

/**
 * formReducer는 useReducer 학습용 핵심 포인트다.
 *
 * 왜 useState 여러 개 대신 reducer를 쓰는가?
 * - 하나의 "폼 상태 객체"를 다룰 때 상태 전이 규칙을 한 군데에 모으기 좋다.
 * - action 기반으로 업데이트 의도가 명확해진다.
 *
 * 상태 전이(state transition)라는 말은 어렵게 들리지만,
 * 결국 "현재 상태 + 액션 -> 다음 상태" 함수를 정의하는 것이다.
 */
const initialState = {
  title: "",
  description: "",
  status: "TODO",
  priority: 3
};

function formReducer(state, action) {
  switch (action.type) {
    case "FIELD_CHANGE":
      return {
        ...state,
        [action.field]: action.value
      };
    case "RESET":
      return initialState;
    case "LOAD_TASK":
      return {
        title: action.task.title,
        description: action.task.description,
        status: action.task.status,
        priority: action.task.priority
      };
    default:
      return state;
  }
}

export default function TaskForm({
  editingTask,
  onCreate,
  onUpdate,
  onCancelEdit,
  isCreating,
  isUpdating,
  serverError
}) {
  const [formState, dispatch] = useReducer(formReducer, initialState);
  const titleInputRef = useRef(null);

  /**
   * editingTask가 바뀌면 formState를 동기화한다.
   * - 수정 모드: task 데이터 로드
   * - 생성 모드: reset
   *
   * useEffect는 "렌더 이후의 부수효과"를 담당한다.
   * 여기서는 props 변화에 맞춰 내부 상태를 재동기화하는 역할이다.
   */
  useEffect(() => {
    if (editingTask) {
      dispatch({ type: "LOAD_TASK", task: editingTask });
    } else {
      dispatch({ type: "RESET" });
    }
  }, [editingTask]);

  /**
   * 입력창 focus도 DOM에 대한 부수효과다.
   * render 단계는 "무엇을 그릴지 계산"하는 곳이고,
   * focus 같은 실제 DOM 조작은 effect 단계에서 수행하는 것이 맞다.
   */
  useEffect(() => {
    titleInputRef.current?.focus();
  }, [editingTask]);

  const formModeLabel = useMemo(() => {
    return editingTask ? "수정 모드" : "생성 모드";
  }, [editingTask]);

  function handleChange(event) {
    const { name, value } = event.target;
    dispatch({
      type: "FIELD_CHANGE",
      field: name,
      value: name === "priority" ? Number(value) : value
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const payload = {
      title: formState.title.trim(),
      description: formState.description.trim(),
      status: formState.status,
      priority: Number(formState.priority)
    };

    if (!payload.title || !payload.description) {
      return;
    }

    if (editingTask) {
      await onUpdate(editingTask.id, payload);
    } else {
      await onCreate(payload);
      dispatch({ type: "RESET" });
      titleInputRef.current?.focus();
    }
  }

  return (
    <section className="card">
      <div className="section-title-row">
        <h2>Task Form</h2>
        <span className="mini-badge">{formModeLabel}</span>
      </div>

      {serverError ? <div className="error-box">{serverError}</div> : null}

      <form className="stack-md" onSubmit={handleSubmit}>
        <label className="field">
          <span>제목</span>
          <input
            ref={titleInputRef}
            name="title"
            value={formState.title}
            onChange={handleChange}
            placeholder="예: React Query invalidate 이해하기"
          />
        </label>

        <label className="field">
          <span>설명</span>
          <textarea
            name="description"
            value={formState.description}
            onChange={handleChange}
            rows={4}
            placeholder="상세 설명을 입력하세요."
          />
        </label>

        <div className="two-column">
          <label className="field">
            <span>상태</span>
            <select name="status" value={formState.status} onChange={handleChange}>
              <option value="TODO">TODO</option>
              <option value="DOING">DOING</option>
              <option value="DONE">DONE</option>
            </select>
          </label>

          <label className="field">
            <span>우선순위</span>
            <input
              name="priority"
              type="number"
              min="1"
              max="5"
              value={formState.priority}
              onChange={handleChange}
            />
          </label>
        </div>

        <div className="task-actions-inline">
          <button type="submit" disabled={isCreating || isUpdating}>
            {editingTask
              ? isUpdating
                ? "수정 중..."
                : "수정 저장"
              : isCreating
              ? "생성 중..."
              : "새 Task 생성"}
          </button>

          {editingTask ? (
            <button type="button" className="secondary" onClick={onCancelEdit}>
              수정 취소
            </button>
          ) : null}
        </div>
      </form>
    </section>
  );
}
