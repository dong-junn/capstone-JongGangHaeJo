// 관리자 페이지 접근 검증 함수
async function verifyAdminAccess() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('접근 권한이 없습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/front-end/templates/user/login/login.html';
        return;
    }

    try {
        // 토큰 디코딩
        const base64Payload = token.split('.')[1];
        const payload = JSON.parse(atob(base64Payload));

        // ROLE_ADMIN 확인
        if (!payload.auth.includes('ROLE_ADMIN')) {
            alert('관리자 권한이 없습니다. 로그인 페이지로 이동합니다.');
            window.location.href = '/front-end/templates/user/login/login.html';
        }
    } catch (error) {
        console.error('토큰 디코딩 오류:', error);
        alert('유효하지 않은 토큰입니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/front-end/templates/user/login/login.html';
    }
}

// 페이지 로드 시 관리자 접근 검증 실행
document.addEventListener('DOMContentLoaded', verifyAdminAccess);
