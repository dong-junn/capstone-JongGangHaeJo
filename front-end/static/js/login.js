// REST API를 이용한 로그인 기능
async function loginUser() {
    const loginForm = document.getElementById('LoginForm');
    const formData = new FormData(loginForm);
    const json = {
        id: formData.get('username'),
        password: formData.get('password')
    };

    try {
        const response = await fetchWithoutAuth('http://3.147.12.27:8080/sign-in', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json),
            credentials: 'include'
        });

        if (response.ok) {
            alert('로그인 성공');
            window.location.href = '/';
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

