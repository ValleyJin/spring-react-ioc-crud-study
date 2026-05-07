/**
 * 이 파일은 "HTTP 통신 책임"을 한 군데에 모아둔 API 레이어다.
 *
 * 좋은 점:
 * 1. fetch 호출이 컴포넌트 곳곳에 흩어지지 않는다.
 * 2. URL, 헤더, 에러 처리 규칙을 모을 수 있다.
 * 3. 나중에 axios로 교체하더라도 이 파일 위주로 수정하면 된다.
 */

const BASE_URL = "/api/tasks";

async function handleResponse(response) {
  if (!response.ok) {
    // 서버가 JSON 에러 본문을 준다면 최대한 읽어온다.
    const errorBody = await response.json().catch(() => null);
    const message = errorBody?.message ?? "서버 요청에 실패했습니다.";
    throw new Error(message);
  }

  // 204 No Content 같은 경우에는 JSON이 없을 수 있다.
  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export async function fetchTasks(filters) {
  const params = new URLSearchParams();

  if (filters.search?.trim()) {
    params.set("search", filters.search.trim());
  }

  if (filters.status && filters.status !== "ALL") {
    params.set("status", filters.status);
  }

  const queryString = params.toString();
  const url = queryString ? `${BASE_URL}?${queryString}` : BASE_URL;

  const response = await fetch(url);
  return handleResponse(response);
}

export async function createTask(payload) {
  const response = await fetch(BASE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  return handleResponse(response);
}

export async function updateTask(taskId, payload) {
  const response = await fetch(`${BASE_URL}/${taskId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  return handleResponse(response);
}

export async function toggleTask(taskId) {
  const response = await fetch(`${BASE_URL}/${taskId}/toggle`, {
    method: "PATCH"
  });

  return handleResponse(response);
}

export async function deleteTask(taskId) {
  const response = await fetch(`${BASE_URL}/${taskId}`, {
    method: "DELETE"
  });

  return handleResponse(response);
}
