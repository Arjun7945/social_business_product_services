import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../target/classes/static', // Output to Spring Boot's static folder
    emptyOutDir: true, // Clear the directory before building (Safety: this wipes Angular's static files if they were there)
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081', // Proxy API requests to backend
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
