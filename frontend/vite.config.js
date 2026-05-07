import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";

/**
 * Vite 설정 파일.
 *
 * proxy를 설정하면 프론트 개발 서버(5173)에서 /api 요청을 백엔드(8080)로 전달할 수 있다.
 * 이렇게 하면 프론트 코드에서 fetch("/api/tasks")처럼 상대 경로를 유지할 수 있다.
 *
 * 브라우저 입장에서는 프론트 서버에 요청했지만,
 * 개발 서버가 뒤에서 백엔드로 프록시해주는 구조다.
 */
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});
