/**
 * queryKey를 상수 함수로 분리해두면 오타를 줄일 수 있다.
 * 또한 invalidateQueries 시 같은 key 규칙을 재사용할 수 있다.
 */
export const taskKeys = {
  all: ["tasks"],
  list: (filters) => ["tasks", filters]
};
