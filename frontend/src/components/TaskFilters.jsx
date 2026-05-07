import { memo } from "react";

/**
 * 검색어/상태 필터 UI.
 *
 * memo를 붙이면 부모가 렌더되더라도
 * props가 바뀌지 않는 한 이 컴포넌트는 재렌더를 건너뛸 수 있다.
 */
function TaskFilters({
  searchInput,
  onSearchInputChange,
  statusFilter,
  onStatusFilterChange
}) {
  return (
    <section className="card">
      <h2>필터</h2>

      <div className="stack-md">
        <label className="field">
          <span>검색</span>
          <input
            value={searchInput}
            onChange={(event) => onSearchInputChange(event.target.value)}
            placeholder="제목/설명 검색"
          />
        </label>

        <label className="field">
          <span>상태</span>
          <select
            value={statusFilter}
            onChange={(event) => onStatusFilterChange(event.target.value)}
          >
            <option value="ALL">ALL</option>
            <option value="TODO">TODO</option>
            <option value="DOING">DOING</option>
            <option value="DONE">DONE</option>
          </select>
        </label>
      </div>
    </section>
  );
}

export default memo(TaskFilters);
