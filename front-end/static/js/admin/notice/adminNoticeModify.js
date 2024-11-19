// URL에서 공지사항 ID 추출
function getNoticeIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// 공지사항 상세 정보 로드
async function loadNoticeDetails() {
    const noticeId = getNoticeIdFromUrl();
    if (!noticeId) {
        alert('유효하지 않은 공지사항입니다.');
        window.location.href = '/front-end/templates/admin/notice/adminNoticeList.html';
        return;
    }

    try {
        const response = await fetchWithAuth(`/notice/${noticeId}`, {
            method: 'GET'
        });

        if (response.ok) {
            const notice = await response.json();
            
            // 폼에 데이터 채우기
            document.getElementById('title').value = notice.title;
            document.getElementById('content').value = notice.content;
        } else {
            const errorData = await response.json();
            alert(errorData.message || '공지사항을 불러오는데 실패했습니다.');
            window.location.href = '/front-end/templates/admin/notice/adminNoticeList.html';
        }
    } catch (error) {
        console.error('공지사항 정보를 불러오는 중 오류 발생:', error);
        alert('공지사항 정보를 불러오는데 실패했습니다.');
    }
}

// 공지사항 수정
async function updateNotice() {
    const noticeId = getNoticeIdFromUrl();
    const json = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    };

    try {
        const response = await fetchWithAuth(`/notice/admin/${noticeId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });

        if (response.ok) {
            alert('공지사항이 성공적으로 수정되었습니다.');
            window.location.href = '/front-end/templates/admin/notice/adminNoticeList.html';
        } else {
            const errorData = await response.json();
            const validationMessages = errorData.validation 
                ? Object.values(errorData.validation).join('\n')
                : errorData.message;
                
            alert(`공지사항 수정에 실패했습니다:\n${validationMessages}`);
        }
    } catch (error) {
        console.error('공지사항 수정 중 오류 발생:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    if (!checkAuth()) return;
    loadNoticeDetails();
}); 