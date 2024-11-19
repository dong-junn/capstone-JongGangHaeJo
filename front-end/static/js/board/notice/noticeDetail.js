// 공지사항 ID를 URL에서 추출하는 함수
function getNoticeIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// 공지사항 상세 정보를 불러오는 함수
async function loadNoticeDetails() {
    const noticeId = getNoticeIdFromUrl();
    
    if (!noticeId) {
        alert('유효하지 않은 공지사항입니다.');
        window.location.href = '/front-end/templates/board/notice/noticeList.html';
        return;
    }

    try {
        const response = await fetchWithoutAuth(`/notice/${noticeId}`);
        
        if (response.ok) {
            const notice = await response.json();
            
            // 공지사항 정보 업데이트
            document.querySelector('.title.highlight').textContent = notice.title || '제목 없음';
            document.querySelector('.notice-content').innerHTML = notice.content?.replace(/\n/g, '<br>') || '';
            
            // 작성자 정보 업데이트 - td:nth-child(2)를 정확한 위치로 수정
            const authorCell = document.querySelector('tr:nth-child(2) td:nth-child(2)');
            if (authorCell) {
                authorCell.textContent = notice.username || '작성자 정보 없음';
            }
            
            // 작성일 처리 - td:nth-child(4)를 정확한 위치로 수정
            const createdAt = notice.createdAt 
                ? new Date(notice.createdAt).toLocaleDateString('ko-KR') 
                : '날짜 정보 없음';
            const dateCell = document.querySelector('tr:nth-child(2) td:nth-child(4)');
            if (dateCell) {
                dateCell.textContent = createdAt;
            }
            
            // 첨부파일 행 제거
            const fileRow = document.querySelector('tr:nth-child(3)');
            if (fileRow) {
                fileRow.remove();
            }

        } else {
            const errorData = await response.json();
            alert(`공지사항을 불러오지 못했습니다: ${errorData.message}`);
            window.location.href = '/front-end/templates/board/notice/noticeList.html';
        }
    } catch (error) {
        if (error.name === 'AbortError') {
            return;
        }
        console.error('공지사항을 불러오는 중 오류 발생:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
        window.location.href = '/front-end/templates/board/notice/noticeList.html';
    }
}

// 페이지 로드 시 공지사항 상세 정보 로드
document.addEventListener('DOMContentLoaded', () => {
    loadNoticeDetails();
});