import { useRef } from "react";

/**
 * 현재 컴포넌트가 몇 번 렌더되었는지를 세는 custom hook.
 *
 * useRef의 핵심:
 * - ref 객체는 렌더 사이에서도 유지된다.
 * - ref.current를 바꿔도 렌더를 다시 일으키지 않는다.
 *
 * 즉, "UI에 직접 반영하지 않아도 되는, 렌더 간 유지값"을 저장하기에 좋다.
 */
export function useRenderCount() {
  const renderCountRef = useRef(0);
  renderCountRef.current += 1;
  return renderCountRef.current;
}
