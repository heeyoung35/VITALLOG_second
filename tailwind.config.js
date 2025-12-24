/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    50: '#F0F8FF',   // 라이트 블루
                    100: '#E0F1FF',
                    200: '#B8E0FF',
                    300: '#87CEEB',  // 스카이 블루
                    400: '#6B9DC2',  // 미디엄 블루
                    500: '#4A90C2',  // 메인 블루
                    600: '#1D5D9B',  // 헤더 블루 (딥 블루)
                    700: '#1A5086',
                    800: '#164371',
                    900: '#12365C',
                },
                accent: {
                    50: '#FFFBEB',   // 라이트 옐로우
                    100: '#FEF3C7',
                    200: '#FDE68A',
                    300: '#FCD34D',
                    400: '#FBBF24',
                    500: '#F4D160',  // 포인트 컬러 (골든 옐로우)
                    600: '#D97706',
                    700: '#B45309',
                    800: '#92400E',
                    900: '#78350F',
                },
                neutral: {
                    cream: '#F5F1ED',  // 크림 베이지
                    light: '#FAFAFA',
                    DEFAULT: '#F8F9FA',
                    dark: '#E9ECEF',
                },
                blue: {
                    light: '#87CEEB',    // 스카이 블루
                    medium: '#6B9DC2',   // 미디엄 블루
                    dark: '#1D5D9B',     // 헤더 블루
                }
            },
            fontFamily: {
                sans: ['Inter', 'system-ui', 'sans-serif'],
            },
            boxShadow: {
                'soft': '0 2px 15px -3px rgba(70, 130, 180, 0.1), 0 10px 20px -2px rgba(70, 130, 180, 0.04)',
                'medium': '0 4px 25px -5px rgba(70, 130, 180, 0.15), 0 10px 30px -5px rgba(70, 130, 180, 0.08)',
                'strong': '0 10px 40px -10px rgba(70, 130, 180, 0.25), 0 20px 50px -10px rgba(70, 130, 180, 0.15)',
            }
        },
    },
    plugins: [],
}