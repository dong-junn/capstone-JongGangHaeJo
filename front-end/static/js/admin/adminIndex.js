async function verifyAdminAccess() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('접근 권한이 없습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/front-end/templates/user/login/login.html';
        return;
    }

    try {
        const base64Payload = token.split('.')[1];
        if (!base64Payload) {
            throw new Error('JWT 형식이 잘못되었습니다.');
        }

        // Base64URL -> Base64 변환
        const base64 = base64Payload.replace(/-/g, '+').replace(/_/g, '/');

        // Base64 디코딩 후 UTF-8로 변환
        const payloadJson = decodeURIComponent(escape(atob(base64)));
        const payload = JSON.parse(payloadJson);
        console.log('디코딩된 페이로드:', payload);

        // 만료 시간 확인
        const currentTime = Math.floor(Date.now() / 1000);
        if (payload.exp < currentTime) {
            alert('토큰이 만료되었습니다. 다시 로그인해주세요.');
            localStorage.removeItem('accessToken');
            window.location.href = '/front-end/templates/user/auth/login/login.html';
            return;
        }

        // ROLE_ADMIN 확인
        if (!payload.auth.includes('ROLE_ADMIN')) {
            alert('관리자 권한이 없습니다. 메인 화면으로 이동합니다.');
            window.location.href = '/index.html';
        }
    } catch (error) {
        console.error('토큰 디코딩 오류:', error.message);
        alert('유효하지 않은 토큰입니다. 로그인 페이지로 이동합니다.');
        localStorage.removeItem('accessToken');
        window.location.href = '/front-end/templates/user/auth/login/login.html';
    }
}


// 페이지 로드 시 verifyAdminAccess 함수 실행
document.addEventListener('DOMContentLoaded', () => {
    verifyAdminAccess();
});
