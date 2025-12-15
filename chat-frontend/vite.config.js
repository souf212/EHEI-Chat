import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    // Certains packages (comme sockjs-client) attendent "global" (Node),
    // on le mappe vers "window" pour le navigateur
    global: 'window',
  },
})
