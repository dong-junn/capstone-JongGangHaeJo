document.addEventListener('DOMContentLoaded', () => {
    const authLink = document.getElementById('authLink');
    const token = localStorage.getItem('accessToken'); // LocalStorage에서 토큰 확인

    if (token) {
        // 로그인 상태일 경우 LOGOUT 링크로 변경
        authLink.innerHTML = `<a href="#">LOGOUT</a>`;
        authLink.addEventListener('click', (event) => {
            event.preventDefault();
            logout();
        });
    } else {
        // 비로그인 상태일 경우 LOGIN 링크 유지
        authLink.innerHTML = `<a href="/front-end/templates/user/auth/login/login.html">LOGIN</a>`;
    }
});

// 로그아웃 처리 함수
function logout() {
    localStorage.removeItem('accessToken'); // 토큰 삭제
    alert('로그아웃되었습니다.');
    window.location.href = '/index.html'; // 로그아웃 후 메인 페이지로 이동
}
