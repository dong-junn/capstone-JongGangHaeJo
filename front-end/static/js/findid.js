// 이메일 인증 여부 변수
let isEmailVerified = false; // 이메일 인증 여부
let timerInterval;

// 이메일 인증코드 요청
async function requestVerificationCode() {
    const email = document.getElementById('email').value;
    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth("/email/verify", {
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

        // 인증코드 입력란 표시
        document.querySelector('.verification-group').classList.remove('hidden');
        showMessage('인증코드가 전송되었습니다.', true);
        startTimer(300); // 타이머 5분 설정
    } catch (error) {
        showMessage(error.message, false);
    }
}

// 인증코드 확인
async function verifyCode() {
    const email = document.getElementById('email').value;
    const code = document.getElementById('verification-code').value;

    if (!email || !code) {
        alert('이메일과 인증코드를 모두 입력해주세요.');
        return;
    }

    try {
        const response = await fetchWithoutAuth("/email/verify/code", {
            method: 'POST',
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
        document.querySelector('.submit-button').disabled = false;
        showMessage('인증이 완료되었습니다.', true);
    } catch (error) {
        showMessage(error.message, false);
    }
}

// 아이디 찾기 요청
async function findUserId() {
    const email = document.getElementById('email').value;

    if (!isEmailVerified) {
        alert('이메일 인증이 필요합니다.');
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
        findUserId();
    });

    includeHTML();
});
