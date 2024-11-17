// URL에서 공지사항 ID 추출하는 함수
function getNoticeIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    return id ? parseInt(id) : 1; // id가 없을 경우 기본값 1 반환
}

// 공지사항 상세 정보를 불러오는 함수
async function loadNoticeDetail() {
    try {
        const noticeId = getNoticeIdFromUrl();
        const response = await fetchWithoutAuth(`/notice/${noticeId}`);
        
        if (response.ok) {
            const notice = await response.json();
            updateNoticeContent(notice);
            await loadNavigationLinks(noticeId);
        } else {
            const errorData = await response.json();
            alert(`공지사항을 불러오지 못했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('공지사항을 불러오는 중 오류 발생:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 공지사항 내용을 화면에 표시하는 함수
function updateNoticeContent(notice) {
    document.querySelector('.title').textContent = notice.title;
    document.querySelector('td:nth-child(2)').textContent = notice.author;
    document.querySelector('td:nth-child(4)').textContent = formatDate(notice.createdAt);
    
    // 첨부 파일 처리
    const fileCell = document.querySelector('.file-link').parentElement;
    if (notice.attachments && notice.attachments.length > 0) {
        const fileLinks = notice.attachments.map(file => 
            `<a href="/api/notice/download/${file.id}" class="file-link">${file.originalFileName}</a>`
        ).join('<br>');
        fileCell.innerHTML = fileLinks;
    } else {
        fileCell.textContent = '첨부파일이 없습니다.';
    }
    
    document.querySelector('.notice-content').innerHTML = notice.content;
}

// 이전글/다음글 네비게이션 로드 함수
async function loadNavigationLinks(currentNoticeId) {
    try {
        const response = await fetchWithoutAuth(`/notice/${currentNoticeId}/navigation`);
        if (response.ok) {
            const navData = await response.json();
            
            // 이전글 업데이트
            const prevRow = document.querySelector('.navigation-links .previous');
            if (navData.previous) {
                prevRow.innerHTML = `<a href="/notice/${navData.previous.id}">이전글 ↑</a> ${navData.previous.title}`;
            } else {
                prevRow.innerHTML = '이전글이 없습니다.';
            }
            
            // 다음글 업데이트
            const nextRow = document.querySelector('.navigation-links .next');
            if (navData.next) {
                nextRow.innerHTML = `<a href="/notice/${navData.next.id}">다음글 ↓</a> ${navData.next.title}`;
            } else {
                nextRow.innerHTML = '다음글이 없습니다.';
            }
        }
    } catch (error) {
        console.error('네비게이션 링크를 불러오는 중 오류 발생:', error);
    }
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 목록 버튼 클릭 이벤트 핸들러
document.querySelector('.back-button').addEventListener('click', () => {
    window.location.href = '/notice';
});

// 페이지 로드 시 공지사항 상세 정보 로드
document.addEventListener('DOMContentLoaded', loadNoticeDetail);
