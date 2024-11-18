// // 헤더와 푸터를 동적으로 로드하는 함수
// async function includeHTML() {
//     try {
//         const headerResponse = await fetch('/front-end/templates/layout/header.html');
//         const headerHtml = await headerResponse.text();
//         document.querySelector('.header-container').innerHTML = headerHtml;

//         const footerResponse = await fetch('/front-end/templates/layout/footer.html');
//         const footerHtml = await footerResponse.text();
//         document.querySelector('.footer-container').innerHTML = footerHtml;
//     } catch (error) {
//         console.error('모듈 로드 중 오류 발생:', error);
//     }
// }

// // 페이지 로드 시 헤더와 푸터를 포함시킴
// document.addEventListener('DOMContentLoaded', includeHTML);

// 헤더와 푸터를 동적으로 로드하는 함수
async function includeHTML() {
    try {
        // 헤더 동적 로드
        const headerResponse = await fetch('/front-end/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        const headerContainer = document.querySelector('.header-container');
        headerContainer.innerHTML = headerHtml;

        // header.js의 로직 실행
        loadHeaderJS();

        // 푸터 동적 로드
        const footerResponse = await fetch('/front-end/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        const footerContainer = document.querySelector('.footer-container');
        footerContainer.innerHTML = footerHtml;
    } catch (error) {
        console.error('모듈 로드 중 오류 발생:', error);
    }
}

// 페이지 로드 시 헤더와 푸터를 포함시킴
document.addEventListener('DOMContentLoaded', includeHTML);

// header.js의 핵심 로직을 함수화
function loadHeaderJS() {
    const authLink = document.getElementById('authLink');
    const token = localStorage.getItem('accessToken'); // LocalStorage에서 토큰 확인

    if (token) {
        // 로그인 상태일 경우 LOGOUT 링크로 변경
        authLink.innerHTML = `<a href="#">LOGOUT</a>`;
        authLink.addEventListener('click', (event) => {
            event.preventDefault();
            logout();
        });
    } else {
        // 비로그인 상태일 경우 LOGIN 링크 유지
        authLink.innerHTML = `<a href="/front-end/templates/user/auth/login/login.html">LOGIN</a>`;
    }
}

// 로그아웃 처리 함수
function logout() {
    localStorage.removeItem('accessToken'); // 토큰 삭제
    alert('로그아웃되었습니다.');
    window.location.href = '/index.html'; // 로그아웃 후 메인 페이지로 이동
}

