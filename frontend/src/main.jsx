import React from "react";
import ReactDOM from "react-dom/client";
import { QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

import App from "./App";
import { queryClient } from "./lib/queryClient";
import "./index.css";

/**
 * React 애플리케이션의 브라우저 진입점.
 *
 * ReactDOM.createRoot(...):
 * - 브라우저 DOM 노드(#root)에 React의 루트 객체를 연결한다.
 * - 이후 React는 virtual DOM 계산 결과를 실제 DOM에 반영한다.
 *
 * QueryClientProvider:
 * - TanStack Query의 캐시 저장소(queryClient)를 하위 컴포넌트 트리에 공급한다.
 * - 일종의 "React 쪽 컨테이너"라고 느껴도 좋다.
 * - 하위 훅(useQuery/useMutation)은 컨텍스트를 통해 queryClient를 찾는다.
 */
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </React.StrictMode>
);
