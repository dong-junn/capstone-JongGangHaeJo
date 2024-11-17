// 비밀번호 재설정 요청
async function resetPassword(event) {
    event.preventDefault();
    
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    
    // 비밀번호 유효성 검사
    if (newPassword !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }
    
    // 임시 토큰 가져오기
    const resetToken = sessionStorage.getItem('resetToken');
    if (!resetToken) {
        alert('인증이 만료되었습니다. 다시 시도해주세요.');
        window.location.href = 'findPw.html';
        return;
    }

    try {
        const response = await fetchWithoutAuth("/auth/find-password/reset", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Password-Reset-Token': `${resetToken}`
            },
            body: JSON.stringify({
                newPassword
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        alert('비밀번호가 성공적으로 변경되었습니다.');
        // 토큰 삭제
        sessionStorage.removeItem('resetToken');
        // 로그인 페이지로 이동
        window.location.href = 'login.html';

    } catch (error) {
        if (error.message === '유효하지 않은 토큰입니다.') {
            sessionStorage.removeItem('resetToken');
            alert('인증이 만료되었습니다. 다시 시도해주세요.');
            window.location.href = 'findPw.html';
            return;
        }
        alert(error.message || '비밀번호 변경에 실패했습니다.');
    }
}

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('resetForm');
    form.addEventListener('submit', resetPassword);
}); 