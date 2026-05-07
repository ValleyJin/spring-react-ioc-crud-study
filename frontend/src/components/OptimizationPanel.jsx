import { memo } from "react";

/**
 * 지금 어떤 최적화 포인트를 체험 중인지 설명해주는 보조 패널.
 * 학습용 프로젝트이므로 "기능"뿐 아니라 "관찰 포인트"를 UI에 같이 둔다.
 */
function OptimizationPanel({ deferredSearchText, searchInput }) {
  const isDeferred = deferredSearchText !== searchInput;

  return (
    <section className="card">
      <h2>최적화 관찰 포인트</h2>

      <ul className="bullet-list">
        <li>
          <code>useDeferredValue</code>로 검색 입력과 실제 서버 질의를 분리했습니다.
        </li>
        <li>
          <code>useMemo</code>로 통계 계산과 form mode 계산을 메모이제이션했습니다.
        </li>
        <li>
          <code>useCallback</code> + <code>React.memo</code> 조합으로 하위 목록의 불필요한 재렌더를 줄였습니다.
        </li>
        <li>
          각 행(Row)에는 렌더 횟수가 표시됩니다.
        </li>
      </ul>

      <div className="deferred-box">
        <p>현재 입력값: <strong>{searchInput || "(비어 있음)"}</strong></p>
        <p>지연 적용값: <strong>{deferredSearchText || "(비어 있음)"}</strong></p>
        <p className={isDeferred ? "warn-text" : "ok-text"}>
          {isDeferred
            ? "입력은 바뀌었지만, 무거운 후속 연산/요청은 약간 늦춰 반영 중입니다."
            : "입력값과 지연값이 동일합니다."}
        </p>
      </div>
    </section>
  );
}

export default memo(OptimizationPanel);
