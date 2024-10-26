function enviewLogin() {
    const userId = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (userId === "" || password === "") {
        alert("아이디와 비밀번호를 입력하세요.");
        return;
    }

    // 로그인 성공 시 메인 페이지로 이동
    alert("로그인 성공");
    window.location.href = '/src/main/resources/templates/index.html';
}
