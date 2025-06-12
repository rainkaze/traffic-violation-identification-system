/** @type {import('tailwindcss').Config} */
export default {
  // 告诉 Tailwind 要扫描哪些文件来查找样式类
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  theme: {
    extend: {
      // 从 index2.html 移植过来的自定义颜色
      colors: {
        primary: '#1e40af', // 深蓝色主色调
        secondary: '#0ea5e9', // 浅蓝色辅助色
        accent: '#f59e0b', // 橙色强调色
        success: '#10b981', // 成功色
        warning: '#f59e0b', // 警告色
        danger: '#ef4444', // 危险色
        dark: '#1f2937', // 深色背景
        light: '#f3f4f6', // 浅色背景
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
