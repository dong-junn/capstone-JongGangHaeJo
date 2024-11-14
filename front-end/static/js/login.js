// REST API를 이용한 로그인 기능
async function loginUser() {
    const loginForm = document.getElementById('LoginForm');
    const formData = new FormData(loginForm);
    const json = {
        id: formData.get('username'),
        password: formData.get('password')
    };

    try {
        const response = await fetch('http://3.147.12.27:8080/sign-in', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });

        if (response.ok) {
            const data = await response.json(); // 응답을 JSON으로 파싱
            const token = data.token; // 토큰 추출
            if (token) {
                localStorage.setItem('authToken', token);
                alert('로그인 성공');
                // window.location.href = '/'; // 로그인 성공 후 메인 페이지로 리다이렉트
            } else {
                alert('로그인에 성공했으나 토큰을 받지 못했습니다. 다시 시도해주세요.');
            }
        } else {
            const errorData = await response.json();
            alert(`로그인 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('로그인 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// API 요청 시 토큰을 헤더에 추가하는 함수
async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('authToken');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    return fetch(url, {
        ...options,
        headers,
    });
}

// 로그인 폼 제출 시 REST API 호출
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('LoginForm');
    loginForm.addEventListener('submit', (event) => {
        event.preventDefault(); // 폼 기본 제출 방지
        loginUser(); // 로그인 함수 호출
    });
});

// 헤더와 푸터를 동적으로 로드하는 함수
async function includeHTML() {
    try {
        const headerResponse = await fetch('/front-end/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.querySelector('.header-container').innerHTML = headerHtml;

        const footerResponse = await fetch('/front-end/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.querySelector('.footer-container').innerHTML = footerHtml;
    } catch (error) {
        console.error('Error loading modules:', error);
    }
}

// 페이지 로드 시 헤더와 푸터를 포함시킴
document.addEventListener('DOMContentLoaded', includeHTML);
