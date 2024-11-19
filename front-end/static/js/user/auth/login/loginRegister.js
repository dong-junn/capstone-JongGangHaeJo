// loginRegister.js

// 이메일 인증 위한 변수
let isEmailVerified = false; // 이메일 인증 여부
let timerInterval;
let lastRequestTime = 0;
const COOLDOWN_TIME = 10000; // 10초

//이메일 인증코드 요청
async function requestVerificationCode() {
    const currentTime = Date.now();
    const timeSinceLastRequest = currentTime - lastRequestTime;
    const emailCheckButton = document.querySelector('.email-check');
    
    // 대기 시간 중일 때
    if (timeSinceLastRequest < COOLDOWN_TIME) {
        const remainingTime = Math.ceil((COOLDOWN_TIME - timeSinceLastRequest) / 1000);
        showMessage(`${remainingTime}초 후에 다시 시도해주세요.`, false);
        
        // 남은 시간동안 1초마다 메시지 업데이트
        const countdownInterval = setInterval(() => {
            const newRemainingTime = Math.ceil((COOLDOWN_TIME - (Date.now() - lastRequestTime)) / 1000);
            if (newRemainingTime <= 0) {
                clearInterval(countdownInterval);
                showMessage('인증코드를 요청할 수 있습니다.', true);
            } else {
                showMessage(`${newRemainingTime}초 후에 다시 시도해주세요.`, false);
            }
        }, 1000);
        
        return;
    }

    const email = document.getElementById('email').value;
    
    if (!email) {
        showMessage('이메일을 입력해주세요.', false);
        emailCheckButton.disabled = false;
        return;
    }

    try {
        const response = await fetchWithoutAuth("/auth/sign-up/email", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(`코드 전송 실패: ${error.message}`);
        }

        document.querySelector('.verification-group').classList.remove('hidden');
        showMessage('인증코드가 전송되었습니다.', true);
        startTimer(300);
        
        lastRequestTime = currentTime;

        setTimeout(() => {
            emailCheckButton.disabled = false;
        }, COOLDOWN_TIME);

    } catch (error) {
        showMessage(error.message, false);
        emailCheckButton.disabled = false;
    }
}

async function verifyCode() {
    const email = document.getElementById('email').value;
    const code = document.getElementById('verification-code').value;

    try {
        const response = await fetchWithoutAuth("/auth/sign-up/email/verify", {
            method: `POST`,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, code })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(`인증 실패: ${error.message}`);
        }

        isEmailVerified = true;
        document.querySelector(`.submit-button`).disabled = false;
        showMessage('인증이 완료되었습니다.', true);
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

// REST API를 이용해 회원가입 기능을 처리하는 함수
async function registerUser() {
    const form = document.querySelector('form');
    const formData = new FormData(form);
    const json = {
        email: formData.get('email'),
        id: formData.get('id'),
        password: formData.get('password'),
        username: formData.get('name')
    };

    if (formData.get('password') !== formData.get('confirm-password')) {
        alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth('/auth/sign-up', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });

        if (response.ok) {
            alert('회원가입이 성공적으로 완료되었습니다.');
            form.reset();
            window.location.href = '/front-end/templates/user/auth/login/login.html';
        } else {
            const errorData = await response.json();
            // validation 객체의 모든 에러 메시지를 추출
            const validationMessages = errorData.validation
                ? Object.values(errorData.validation).join('\n')
                : errorData.message;

            alert(`${validationMessages}`);
        }
    } catch (error) {
        console.error('회원가입 중 오류가 발생했습니다:', error);
        alert('회원가입 처리 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
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
        registerUser();
    });

    includeHTML();
});
