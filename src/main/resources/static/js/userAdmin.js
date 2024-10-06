function editUser(userId) {
    const editForm = document.getElementById(`edit-form-${userId}`);
    if (editForm.style.display === "block") {
        editForm.style.display = "none";
    } else {
        editForm.style.display = "block";
    }
}

function deleteUser(userId) {
    if (confirm('사용자를 삭제하시겠습니까?')) {
        alert(`사용자 ${userId}가 삭제되었습니다.`);
        // 사용자 삭제 로직 추가
    }
}

function saveChanges(userId) {
    const email = document.getElementById(`email-${userId}`).value;
    const phone = document.getElementById(`phone-${userId}`).value;
    alert(`사용자 ${userId}의 이메일: ${email}, 전화번호: ${phone}`);
    //  저장 로직 추가
}
