// REST API를 이용해 아이디 찾기 기능을 처리하는 함수
async function findUserId() {
    const email = document.getElementById('email').value;
    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth(`/api/find-id?email=${encodeURIComponent(email)}`, {
            method: 'GET'
        });

        const result = await response.json();
        
        if (response.ok && result.id) {
            alert(`아이디 찾기 성공: 아이디는 "${result.id}"입니다.`);
        } else if (result.message) {
            alert(`아이디 찾기 실패: ${result.message}`);
        } else {
            alert('아이디를 찾을 수 없습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('아이디 찾기 중 오류 발생:', error);
        alert('아이디 찾기 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 이메일 인증 요청 함수 (선택사항)
async function verifyEmail() {
    const email = document.getElementById('email').value;
    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth(`/api/verify-email?email=${encodeURIComponent(email)}`, {
            method: 'GET'
        });

        if (response.ok) {
            alert('이메일 인증 요청이 전송되었습니다. 이메일을 확인해주세요.');
            document.querySelector('.email-check').disabled = true; // 이메일 확인 버튼 비활성화
        } else {
            alert('이메일 인증 요청에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('이메일 인증 중 오류 발생:', error);
        alert('이메일 인증 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    form.addEventListener('submit', (event) => {
        event.preventDefault();
        findUserId();
    });

    const emailCheckButton = document.querySelector('.email-check');
    emailCheckButton.addEventListener('click', verifyEmail);

    includeHTML();
});
