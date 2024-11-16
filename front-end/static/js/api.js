// const API_BASE_URL = 'http://3.147.12.27:8080'; // 환경별 API URL 설정
const API_BASE_URL = 'http://3.147.12.27:8080'; // 환경별 API URL 설정

// 로그인 체크 함수
function checkAuth() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요한 서비스입니다.');
        window.location.href = '/front-end/templates/user/login/login.html';
        return false;
    }
    return true;
}

// 인증이 필요한 API 요청을 위한 공통 함수
async function fetchWithAuth(url, options = {}) {
    if (!checkAuth()) return; // 로그인 체크
    const token = localStorage.getItem('accessToken'); // 토큰 가져오기

    // FormData인 경우 Content-Type 헤더를 제거
    const headers = options.body instanceof FormData 
        ? {
            'Authorization': `Bearer ${token}`,
            ...options.headers
        }
        : {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...options.headers
        };

    const defaultOptions = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, defaultOptions);
        
        // 401 에러 시 로그인 페이지로 리다이렉트
        if (response.status === 401) {
            removeAuthToken(); // 토큰 삭제
            window.location.href = '/front-end/templates/user/login/login.html';
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

// 로그인 성공 시 토큰 저장 함수
function setAuthToken(token) {
    localStorage.setItem('accessToken', token);
}

// 로그아웃 함수
function removeAuthToken() {
    localStorage.removeItem('accessToken');
}