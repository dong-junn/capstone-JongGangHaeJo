function enviewLogin(mode) {
    // 로그인 처리 함수 (예제)
    const userId = document.getElementById('id').value;
    const password = document.getElementById('pw').value;
    
    if (userId === "" || password === "") {
        alert("아이디와 비밀번호를 입력하세요.");
        return;
    }

    // 실제 로그인 처리를 위한 코드 추가
    // 예시: 로그인 성공 시 메인 페이지로 이동
    alert("로그인 성공");
    window.location.href = 'main.html';
}
