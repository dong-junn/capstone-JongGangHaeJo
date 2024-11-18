function enviewLogin() {
    const userId = document.getElementById('id').value;
    const password = document.getElementById('pw').value;

    // 관리자 계정 정보
    const adminId = 'admin';
    const adminPassword = 'admin123';

    if (userId === adminId && password === adminPassword) {
        // 관리자 로그인
        sessionStorage.setItem('loggedIn', 'true');
        sessionStorage.setItem('isAdmin', 'true');
        window.location.href = 'admin.html';
    } else {
        // 일반 사용자 로그인 로직 (여기서는 간단히 사용자 로그인 예제)
        // 실제로는 서버와 통신하여 로그인 처리를 해야 합니다.
        sessionStorage.setItem('loggedIn', 'true');
        sessionStorage.setItem('isAdmin', 'false');
        window.location.href = 'index.html';
    }
}
  // 헤더와 푸터를 동적으로 로드
  async function includeHTML() {
    try {
        const headerResponse = await fetch('/src/main/resources/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.getElementById('header').innerHTML = headerHtml;

        const footerResponse = await fetch('/src/main/resources/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.getElementById('footer').innerHTML = footerHtml;
    } catch (error) {
        console.error('헤더와 푸터를 로드하는 중 오류 발생:', error);
    }
}

// 페이지 로드 시 헤더와 푸터 포함
document.addEventListener('DOMContentLoaded', async () => {
    await includeHTML();
    loadNotices(); // 공지사항 로드 함수 호출
});
