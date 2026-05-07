import { QueryClient } from "@tanstack/react-query";

/**
 * QueryClient는 TanStack Query의 중앙 캐시 관리자다.
 *
 * 캐시 관점:
 * - queryKey 별로 서버 응답이 메모리에 저장된다.
 * - staleTime 동안은 "신선한 데이터"로 간주해 재요청을 줄일 수 있다.
 * - gcTime이 지나면 사용하지 않는 캐시를 수거하여 메모리 사용량을 줄인다.
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 10,
      gcTime: 1000 * 60 * 5,
      refetchOnWindowFocus: false,
      retry: 1
    },
    mutations: {
      retry: 0
    }
  }
});
