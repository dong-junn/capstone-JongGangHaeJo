function findId() {
    const email = document.getElementById('email').value;

    // 예시: 서버로 이메일을 전송하여 아이디 찾기 요청을 보내는 로직 (여기서는 간단히 경고창으로 대체)
    if (email) {
        alert(`아이디 찾기 요청이 전송되었습니다. 입력한 이메일: ${email}`);
        // 실제로는 서버와 통신하여 아이디 찾기 처리 후 사용자에게 알려주는 로직이 필요합니다.
    } else {
        alert('이메일을 입력해주세요.');
    }
}
