import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Configuration Vite pour React
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      // Rediriger les requêtes API vers le backend
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})

