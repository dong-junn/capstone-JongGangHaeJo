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
