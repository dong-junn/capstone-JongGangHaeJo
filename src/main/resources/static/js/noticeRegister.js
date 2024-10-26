// REST API를 이용해 공지사항을 등록하는 함수
async function submitNotice() {
    const formData = new FormData(document.getElementById('register-form'));
    try {
        const response = await fetch('https://your-api-endpoint.com/notices', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('공지사항이 성공적으로 등록되었습니다.');
            document.getElementById('register-form').reset();
        } else {
            alert('공지사항 등록에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('Error submitting notice:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}


