// noticeRegister.js

// REST API를 이용해 공지사항을 등록하는 함수
async function submitNotice() {
    const formData = new FormData();
    
    // JSON 데이터 생성
    const json = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    };

    // JSON 데이터를 폼데이터에 추가
    formData.append('notice', new Blob([JSON.stringify(json)], { type: 'application/json' }));

    // 첨부파일 추가
    const fileInput = document.getElementById('noti_file');
    if (fileInput.files.length > 0) {
        formData.append('file', fileInput.files[0]);
    }

    try {
        const response = await fetchWithAuth('/admin/notice', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('공지사항이 성공적으로 등록되었습니다.');
            window.location.href = '/front-end/templates/board/notice/adminNoticeList.html';
        } else {
            const errorData = await response.json();
            const validationMessages = errorData.validation 
                ? Object.values(errorData.validation).join('\n')
                : errorData.message;
                
            alert(`공지사항 등록에 실패했습니다:\n${validationMessages}`);
        }
    } catch (error) {
        console.error('공지사항 등록 중 오류 발생:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 페이지 로드 시 권한 체크
document.addEventListener('DOMContentLoaded', () => {
    if (!checkAuth()) return;
});
