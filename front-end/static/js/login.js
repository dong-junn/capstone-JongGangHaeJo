// REST API를 이용한 로그인 기능
async function loginUser() {
    const loginForm = document.getElementById('LoginForm');
    const formData = new FormData(loginForm);
    const json = {
        username: formData.get('username'),
        password: formData.get('password')
    };

    try {
        const response = await fetch('http://127.0.0.1:8080/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });

        if (response.ok) {
            alert('로그인 성공');
            window.location.href = '/'; // 로그인 성공 후 메인 페이지로 리다이렉트
        } else {
            const errorData = await response.json();
            alert(`로그인 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('로그인 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
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
        const headerResponse = await fetch('http://127.0.0.1:5500/front-end/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.getElementById('header').innerHTML = headerHtml;

        const footerResponse = await fetch('http://127.0.0.1:5500/front-end/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.getElementById('footer').innerHTML = footerHtml;
    } catch (error) {
        console.error('Error loading modules:', error);
    }
}

// 페이지 로드 시 헤더와 푸터를 포함시킴
document.addEventListener('DOMContentLoaded', includeHTML);
