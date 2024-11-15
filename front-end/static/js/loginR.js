// REST API를 이용해 회원가입 기능을 처리하는 함수
async function registerUser() {
    const form = document.querySelector('form');
    const formData = new FormData(form);
    const json = {
        id: formData.get('id'),
        password: formData.get('password'),
        username: formData.get('name')
    };

    if (formData.get('password') !== formData.get('confirm-password')) {
        alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth('/sign-up', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });

        const result = await response.json();
        
        // '회원가입이 완료되었습니다.' 메시지를 확인하여 성공 여부 판단
        if (result.message === "회원가입이 완료되었습니다.") {
            alert('회원가입이 성공적으로 완료되었습니다.');
            form.reset();
        } else {
            alert(`회원가입 실패: ${result.message}`);
            // validation 에러가 있을 경우 상세 메시지 출력
            if (result.validation) {
                for (const [key, message] of Object.entries(result.validation)) {
                    alert(`${key}: ${message}`);
                }
            }
        }
    } catch (error) {
        console.error('회원가입 중 오류가 발생했습니다:', error);
        alert('회원가입 처리 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 이메일 인증 요청 함수
async function verifyEmail() {
    const email = document.getElementById('email').value;
    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth(`/api/verify-email?email=${email}`, {
            method: 'GET'
        });

        if (response.ok) {
            alert('이메일 인증 요청이 전송되었습니다. 이메일을 확인해주세요.');
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
        registerUser();
    });

    const emailCheckButton = document.querySelector('.email-check');
    emailCheckButton.addEventListener('click', verifyEmail);

    includeHTML();
});
