// loginR.js

// REST API를 이용해 회원가입 기능을 처리하는 함수
async function registerUser() {
    const form = document.querySelector('form');
    const formData = new FormData(form);
    const json = {
        email: formData.get('email'),
        name: formData.get('name'),
        password: formData.get('password'),
        phone: `${formData.get('phone-code')}-${formData.get('phone')}`,
        emailConsent: formData.get('email-consent') ? true : false,
        smsConsent: formData.get('sms-consent') ? true : false
    };

    if (formData.get('password') !== formData.get('confirm-password')) {
        alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
        return;
    }

    try {
        const response = await fetch('http://127.0.0.1:8080/api/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });

        if (response.ok) {
            alert('회원가입이 성공적으로 완료되었습니다.');
            form.reset();
        } else {
            const errorData = await response.json();
            alert(`회원가입 실패: ${errorData.message}`);
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
        const response = await fetch(`http://127.0.0.1:8080/api/verify-email?email=${email}`, {
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

// 헤더와 푸터를 동적으로 로드하는 함수
async function includeHTML() {
    try {
        const headerResponse = await fetch('/front-end/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.querySelector('.header-container').innerHTML = headerHtml;

        const footerResponse = await fetch('/front-end/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.querySelector('.footer-container').innerHTML = footerHtml;
    } catch (error) {
        console.error('헤더와 푸터를 로드하는 중 오류 발생:', error);
    }
}
