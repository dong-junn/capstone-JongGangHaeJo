// 이메일 인증 여부 변수
let isEmailVerified = false; // 이메일 인증 여부
let timerInterval;

// 이메일 인증코드 요청
async function requestVerificationCode() {
    const email = document.getElementById('email').value;
    const id = document.getElementById('id').value;

    if (!email || !id) {
        showMessage('아이디와 이메일을 모두 입력해주세요.', false);
        return;
    }

    try {
        const response = await fetchWithoutAuth("/auth/find-password/request", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id, email })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(`코드 전송 실패: ${error.message}`);
        }

        // 인증코드 입력란 표시
        document.querySelector('.verification-group').classList.remove('hidden');
        showMessage('인증코드가 전송되었습니다.', true);
        startTimer(300);
    } catch (error) {
        showMessage(error.message, false);
    }
}

// 인증코드 확인
async function verifyCode() {
    const email = document.getElementById('email').value;
    const code = document.getElementById('verification-code').value;
    const id = document.getElementById('id').value;

    if (!email || !code || !id) {
        showMessage('모든 정보를 입력해주세요.', false);
        return;
    }

    try {
        const response = await fetchWithoutAuth("/auth/find-password/verify", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id, email, code })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(`인증 실패: ${error.message}`);
        }

        // JSON 응답에서 토큰 추출
        const { token } = await response.json();
        sessionStorage.setItem('resetToken', token);
        showMessage('인증이 완료되었습니다.', true);

        // 3초 후 비밀번호 재설정 페이지로 이동
        setTimeout(() => {
            window.location.href = '/front-end/templates/user/auth/reset/resetPw.html';
        }, 3000);

    } catch (error) {
        showMessage(error.message, false);
    }
}

// 인증코드 타이머 시작
function startTimer(duration) {
    clearInterval(timerInterval);
    const timerDisplay = document.querySelector('.timer');
    let timer = duration;

    timerInterval = setInterval(() => {
        const minutes = parseInt(timer / 60, 10);
        const seconds = parseInt(timer % 60, 10);

        timerDisplay.textContent =
            minutes + ":" + (seconds < 10 ? "0" : "") + seconds;

        if (--timer < 0) {
            clearInterval(timerInterval);
            timerDisplay.textContent = "시간 만료";
        }
    }, 1000);
}

// 인증코드 메시지 표시
function showMessage(message, isSuccess) {
    const messageElement = document.querySelector('.verification-message');
    messageElement.textContent = message;
    messageElement.className = 'verification-message ' + (isSuccess ? 'success' : 'error');
}

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
    // 인증코드 입력란 초기 숨김
    document.querySelector('.verification-group').classList.add('hidden');

    // 이메일 인증 버튼
    const emailCheckButton = document.querySelector('.email-check');
    emailCheckButton.addEventListener('click', requestVerificationCode);

    // 인증코드 확인 버튼
    const verifyButton = document.querySelector('.verify-btn');
    verifyButton.addEventListener('click', verifyCode);

    // 폼 제출
    const form = document.querySelector('form');
    form.addEventListener('submit', (event) => {
        event.preventDefault();
        resetPassword();
    });

    includeHTML();
});
