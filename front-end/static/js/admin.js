// 관리자 페이지 접근 권한 확인
async function verifyAdminAccess() {
    try {
        const response = await fetchWithAuth('/admin', {
            method: 'GET'
        });

        if (!response.ok) {
            // 요청 실패 시 로그인 페이지로 리다이렉트
            alert('접근 권한이 없습니다. 로그인해주세요.');
            redirectToLogin();
        }
    } catch (error) {
        console.error('권한 확인 중 오류 발생:', error);
        alert('권한 확인 중 문제가 발생했습니다. 다시 시도해주세요.');
        redirectToLogin();
    }
}

// 로그인 페이지로 리다이렉트
function redirectToLogin() {
    window.location.href = '/front-end/templates/user/login/login.html';
}

// 페이지 로드 시 관리자 접근 확인
document.addEventListener('DOMContentLoaded', () => {
    verifyAdminAccess();
});
