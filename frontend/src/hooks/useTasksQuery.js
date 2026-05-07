import { useQuery } from "@tanstack/react-query";
import { fetchTasks } from "../api/taskApi";
import { taskKeys } from "../constants/queryKeys";

/**
 * 서버에서 Task 목록을 읽어오는 custom hook.
 *
 * custom hook을 두는 이유:
 * - useQuery 세부설정이 컴포넌트에서 분리된다.
 * - 여러 컴포넌트가 같은 서버 상태 접근 규칙을 재사용할 수 있다.
 * - 관심사 분리가 좋아진다.
 */
export function useTasksQuery(filters) {
  return useQuery({
    queryKey: taskKeys.list(filters),
    queryFn: () => fetchTasks(filters),
    placeholderData: (previousData) => previousData
  });
}
