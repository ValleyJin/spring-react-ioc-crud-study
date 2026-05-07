import { useCallback, useDeferredValue, useMemo, useState } from "react";

import Layout from "./components/Layout";
import TaskFilters from "./components/TaskFilters";
import TaskForm from "./components/TaskForm";
import TaskList from "./components/TaskList";
import TaskStats from "./components/TaskStats";
import OptimizationPanel from "./components/OptimizationPanel";

import { useTasksQuery } from "./hooks/useTasksQuery";
import { useTaskMutations } from "./hooks/useTaskMutations";

/**
 * 최상위 App 컴포넌트.
 *
 * 이 컴포넌트에서 학습해야 할 핵심:
 * 1. 로컬 UI 상태(useState)
 * 2. 서버 상태(useTasksQuery / useTaskMutations)
 * 3. 파생 데이터(useMemo)
 * 4. 참조 안정화(useCallback)
 * 5. 지연 반영(useDeferredValue)
 */
export default function App() {
  // 로컬 상태: 입력창 값, 필터 상태, 현재 수정 중인 Task
  const [searchInput, setSearchInput] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [editingTask, setEditingTask] = useState(null);
  const [formError, setFormError] = useState("");

  /**
   * searchInput은 사용자가 타이핑하는 즉시 바뀐다.
   * 하지만 이 값을 그대로 queryKey에 넣으면 매 입력마다 서버 요청/무거운 계산이 붙을 수 있다.
   *
   * useDeferredValue는 "우선순위가 덜 급한 값"을 조금 늦춰 반영해
   * UI 응답성을 높이는 데 도움을 줄 수 있다.
   */
  const deferredSearchText = useDeferredValue(searchInput);

  /**
   * useMemo를 쓰는 이유:
   * - 객체 리터럴 { search, status } 를 매 렌더마다 새로 만들면
   *   queryKey와 memo 비교에서 불리해질 수 있다.
   * - 의존성이 변할 때만 새 객체를 만들도록 해서 참조를 안정화한다.
   */
  const filters = useMemo(
    () => ({
      search: deferredSearchText,
      status: statusFilter
    }),
    [deferredSearchText, statusFilter]
  );

  const tasksQuery = useTasksQuery(filters);
  const { createMutation, updateMutation, toggleMutation, deleteMutation } = useTaskMutations();

  const tasks = tasksQuery.data ?? [];

  /**
   * 통계는 tasks 배열로부터 계산되는 "파생 데이터"다.
   * 원본 서버 데이터가 바뀌지 않으면 굳이 매 렌더마다 다시 계산할 필요가 없다.
   */
  const stats = useMemo(() => {
    const total = tasks.length;
    const todo = tasks.filter((task) => task.status === "TODO").length;
    const doing = tasks.filter((task) => task.status === "DOING").length;
    const done = tasks.filter((task) => task.status === "DONE").length;
    const avgPriority =
      total === 0
        ? 0
        : tasks.reduce((sum, task) => sum + task.priority, 0) / total;

    return { total, todo, doing, done, avgPriority };
  }, [tasks]);

  /**
   * useCallback:
   * - 함수를 메모이제이션한다.
   * - React.memo가 붙은 자식에게 함수 props를 넘길 때 유용하다.
   * - "항상 필요한 것은 아니지만", 최적화가 실제로 필요한 지점에서는 중요하다.
   */
  const handleSearchInputChange = useCallback((value) => {
    setSearchInput(value);
  }, []);

  const handleStatusFilterChange = useCallback((value) => {
    setStatusFilter(value);
  }, []);

  const handleEdit = useCallback((task) => {
    setFormError("");
    setEditingTask(task);
  }, []);

  const handleCreate = useCallback(
    async (payload) => {
      try {
        setFormError("");
        await createMutation.mutateAsync(payload);
      } catch (error) {
        setFormError(error.message);
      }
    },
    [createMutation]
  );

  const handleUpdate = useCallback(
    async (taskId, payload) => {
      try {
        setFormError("");
        await updateMutation.mutateAsync({ taskId, payload });
        setEditingTask(null);
      } catch (error) {
        setFormError(error.message);
      }
    },
    [updateMutation]
  );

  const handleCancelEdit = useCallback(() => {
    setEditingTask(null);
    setFormError("");
  }, []);

  const handleToggle = useCallback(
    async (taskId) => {
      try {
        await toggleMutation.mutateAsync(taskId);
      } catch (error) {
        window.alert(error.message);
      }
    },
    [toggleMutation]
  );

  const handleDelete = useCallback(
    async (taskId) => {
      const shouldDelete = window.confirm("정말 삭제하시겠습니까?");
      if (!shouldDelete) return;

      try {
        if (editingTask?.id === taskId) {
          setEditingTask(null);
        }
        await deleteMutation.mutateAsync(taskId);
      } catch (error) {
        window.alert(error.message);
      }
    },
    [deleteMutation, editingTask]
  );

  return (
    <Layout>
      <div className="left-column stack-lg">
        <TaskForm
          editingTask={editingTask}
          onCreate={handleCreate}
          onUpdate={handleUpdate}
          onCancelEdit={handleCancelEdit}
          isCreating={createMutation.isPending}
          isUpdating={updateMutation.isPending}
          serverError={formError}
        />

        <TaskFilters
          searchInput={searchInput}
          onSearchInputChange={handleSearchInputChange}
          statusFilter={statusFilter}
          onStatusFilterChange={handleStatusFilterChange}
        />

        <TaskStats stats={stats} />

        <OptimizationPanel
          deferredSearchText={deferredSearchText}
          searchInput={searchInput}
        />
      </div>

      <div className="right-column">
        <TaskList
          tasks={tasks}
          isLoading={tasksQuery.isLoading}
          isFetching={tasksQuery.isFetching}
          error={tasksQuery.error}
          onEdit={handleEdit}
          onToggle={handleToggle}
          onDelete={handleDelete}
        />
      </div>
    </Layout>
  );
}
