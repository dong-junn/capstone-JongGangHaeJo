// const API_BASE_URL = 'http://3.147.12.27:8080'; // 환경별 API URL 설정
const API_BASE_URL = 'http://127.0.0.1:8080'; // 환경별 API URL 설정
// const API_BASE_URL = window.location.hostname === 'localhost' 
//     ? 'http://localhost:8080'
//     : `http://${window.location.hostname}:8080`;

// 로그인 체크 함수
function checkAuth() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요한 서비스입니다.');
        window.location.href = '/front-end/templates/user/auth/login/login.html';
        return false;
    }
    return true;
}

// 인증이 필요한 API 요청을 위한 공통 함수
async function fetchWithAuth(url, options = {}) {
    if (!checkAuth()) return;
    const token = localStorage.getItem('accessToken');

    // 새로운 headers 객체 생성
    const newHeaders = options.body instanceof FormData 
        ? {
            'Authorization': `Bearer ${token}`,
            ...options.headers
        }
        : {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...options.headers
        };

    return fetchWithAbort(url, {
        ...options,
        headers: newHeaders // 새로운 headers 사용
    });
}

// 인증이 필요없는 API 요청을 위한 공통 함수
async function fetchWithoutAuth(url, options = {}) {
    // 새로운 headers 객체 생성
    const newHeaders = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    return fetchWithAbort(url, {
        ...options,
        headers: newHeaders // 새로운 headers 사용
    });
}

// AbortController를 사용하는 fetch 함수
async function fetchWithAbort(url, options = {}) {
    const controller = new AbortController();
    const { signal } = controller;

    // cleanup 함수 등록
    const cleanup = () => controller.abort();
    window.addEventListener('beforeunload', cleanup);

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            ...options,
            signal,
            headers: options.headers // headers 추가
        });
        return response;
    } finally {
        window.removeEventListener('beforeunload', cleanup);
    }
}

// 로그인 성공 시 토큰 저장 함수
function setAuthToken(token) {
    localStorage.setItem('accessToken', token);
}

// 로그아웃 함수
function removeAuthToken() {
    localStorage.removeItem('accessToken');
}