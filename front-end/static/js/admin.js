document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('adminLoginForm');
    const loginMessage = document.querySelector('.login-message');
    const adminNav = document.querySelector('.admin-nav');
    const welcomeMessage = document.querySelector('.welcome-message');

    // 관리자 로그인 함수
    async function loginAdmin(event) {
        event.preventDefault();

        const formData = new FormData(loginForm);
        const json = {
            username: formData.get('username'),
            password: formData.get('password')
        };

        try {
            const response = await fetchWithoutAuth('/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(json)
            });

            if (response.ok) {
                const data = await response.json();
                loginMessage.textContent = '로그인 성공!';
                loginMessage.style.color = 'green';

                // 토큰 저장 (예: 쿠키 또는 로컬 스토리지)
                document.cookie = `adminToken=${data.token}; path=/`;

                // 로그인 섹션 숨기기
                loginForm.parentElement.classList.add('hidden');

                // 네비게이션 바와 환영 메시지 표시
                adminNav.classList.remove('hidden');
                welcomeMessage.classList.remove('hidden');
            } else {
                const error = await response.json();
                loginMessage.textContent = error.message || '로그인 실패';
                loginMessage.style.color = 'red';
            }
        } catch (error) {
            console.error('로그인 중 오류 발생:', error);
            loginMessage.textContent = '로그인 중 오류가 발생했습니다.';
            loginMessage.style.color = 'red';
        }
    }

    // 로그인 폼 제출 이벤트
    loginForm.addEventListener('submit', loginAdmin);
});
