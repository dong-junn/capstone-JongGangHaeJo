const API_BASE_URL = 'http://3.147.12.27:8080'; // 환경별 API URL 설정

// 인증이 필요한 API 요청을 위한 공통 함수
async function fetchWithAuth(url, options = {}) {
    const defaultOptions = {
        ...options,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        }
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, defaultOptions);
        
        // 401 에러 시 로그인 페이지로 리다이렉트
        if (response.status === 401) {
            window.location.href = '/login.html';
            return;
        }
        
        return response;
    } catch (error) {
        console.error('API 요청 중 오류 발생:', error);
        throw error;
    }
}

// 인증이 필요없는 API 요청을 위한 공통 함수
async function fetchWithoutAuth(url, options = {}) {
    const defaultOptions = {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        }
    };

    return fetch(`${API_BASE_URL}${url}`, defaultOptions);
} 