// REST API를 이용한 로그인 기능
async function loginUser() {
    const loginForm = document.getElementById('LoginForm');
    const formData = new FormData(loginForm);
    const json = {
        id: formData.get('username'),
        password: formData.get('password')
    };

    try {
        const response = await fetchWithoutAuth('/auth/sign-in', {
            method: 'POST',
            body: JSON.stringify(json)
        });

        if (response.ok) {
            const data = await response.json();
            setAuthToken(data.token); // 토큰 저장
            alert('로그인 성공');
            window.location.href = '/';
        } else {
            const errorData = await response.json();
            alert(`로그인 실패: ${errorData.message}`);
        }
    } catch (error) {
        if (error.name === 'AbortError') {
            return;
        }
        console.error('로그인 중 오류 발생:', error);
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
