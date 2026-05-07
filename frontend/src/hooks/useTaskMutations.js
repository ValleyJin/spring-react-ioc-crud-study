import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createTask, deleteTask, toggleTask, updateTask } from "../api/taskApi";
import { taskKeys } from "../constants/queryKeys";

/**
 * 생성/수정/토글/삭제 Mutation을 모아둔 custom hook.
 *
 * 여기서 핵심 학습 포인트:
 * 1. useMutation은 "서버에 쓰기" 작업을 관리한다.
 * 2. 성공 후 invalidateQueries를 하면 관련 query가 다시 최신화된다.
 * 3. 일부 작업은 optimistic update로 먼저 화면을 갱신할 수 있다.
 */
export function useTaskMutations() {
  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: createTask,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.all });
    }
  });

  const updateMutation = useMutation({
    mutationFn: ({ taskId, payload }) => updateTask(taskId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.all });
    }
  });

  const toggleMutation = useMutation({
    mutationFn: toggleTask,
    // 낙관적 업데이트 예시:
    // 서버 응답을 기다리기 전에 캐시를 먼저 수정한다.
    onMutate: async (taskId) => {
      await queryClient.cancelQueries({ queryKey: taskKeys.all });

      const previousEntries = queryClient.getQueriesData({ queryKey: taskKeys.all });

      previousEntries.forEach(([queryKey, tasks]) => {
        if (!Array.isArray(tasks)) return;

        queryClient.setQueryData(
          queryKey,
          tasks.map((task) => {
            if (task.id !== taskId) return task;

            return {
              ...task,
              status: task.status === "DONE" ? "TODO" : "DONE"
            };
          })
        );
      });

      return { previousEntries };
    },
    onError: (_error, _taskId, context) => {
      context?.previousEntries?.forEach(([queryKey, tasks]) => {
        queryClient.setQueryData(queryKey, tasks);
      });
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.all });
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteTask,
    onMutate: async (taskId) => {
      await queryClient.cancelQueries({ queryKey: taskKeys.all });

      const previousEntries = queryClient.getQueriesData({ queryKey: taskKeys.all });

      previousEntries.forEach(([queryKey, tasks]) => {
        if (!Array.isArray(tasks)) return;

        queryClient.setQueryData(
          queryKey,
          tasks.filter((task) => task.id !== taskId)
        );
      });

      return { previousEntries };
    },
    onError: (_error, _taskId, context) => {
      context?.previousEntries?.forEach(([queryKey, tasks]) => {
        queryClient.setQueryData(queryKey, tasks);
      });
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.all });
    }
  });

  return {
    createMutation,
    updateMutation,
    toggleMutation,
    deleteMutation
  };
}
