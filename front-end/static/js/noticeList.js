// REST API를 이용해 공지사항 리스트를 불러오는 함수
async function loadNotices(currentPage = 1) {
    try {
        const response = await fetchWithoutAuth(`/notice?page=${currentPage}`);
        if (response.ok) {
            const noticesData = await response.json();
            const notices = noticesData.content; // 공지사항 데이터 배열
            const noticeList = document.querySelector('#notice-list tbody');
            noticeList.innerHTML = ''; // 기존 항목 초기화

            // 공지사항 리스트 동적 생성
            notices.forEach((notice, index) => {
                const noticeRow = document.createElement('tr');
                noticeRow.className = 'notice-item'; // CSS 클래스 적용
                noticeRow.style.cursor = 'pointer'; // 커서 스타일을 포인터로 변경
                noticeRow.innerHTML = `
                    <td>${index + 1 + (currentPage - 1) * noticesData.size}</td>
                    <td>${notice.title}</td>
                    <td>${notice.hasAttachment ? '<i class="file-icon"></i>' : ''}</td>
                    <td>${notice.createdAt}</td>
                    <td>${notice.viewCount}</td>
                `;
                
                // 행 클릭 이벤트 수정
                noticeRow.addEventListener('click', () => {
                    // 절대 경로로 변경
                    window.location.href = `/front-end/templates/board/notice/noticeDetail.html?id=${notice.id}`;
                });
                
                noticeList.appendChild(noticeRow);
            });

            // 페이지네이션 로드
            loadPagination(noticesData.totalPages, currentPage);
        } else {
            const errorData = await response.json();
            alert(`공지사항을 불러오지 못했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('공지사항을 불러오는 중 오류 발생:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 페이지네이션을 동적으로 로드하는 함수
async function loadPagination(totalPages, currentPage) {
    const paginationContainer = document.querySelector('.pagination');
    paginationContainer.innerHTML = ''; // 기존 페이지네이션 초기화

    // 이전 페이지 링크
    const prevLink = document.createElement('a');
    prevLink.href = '#';
    prevLink.textContent = '«';
    prevLink.classList.toggle('disabled', currentPage === 1);
    prevLink.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage > 1) {
            loadNotices(currentPage - 1); // 이전 페이지 로드
        }
    });
    paginationContainer.appendChild(prevLink);

    // 페이지 번호 링크
    for (let i = 1; i <= totalPages; i++) {
        const pageLink = document.createElement(i === currentPage ? 'strong' : 'a');
        pageLink.textContent = i;
        if (i !== currentPage) {
            pageLink.href = '#';
            pageLink.addEventListener('click', (e) => {
                e.preventDefault();
                loadNotices(i);
            });
        }
        pageLink.classList.add('pagination-link'); // CSS 클래스 적용
        paginationContainer.appendChild(pageLink);
    }

    // 다음 페이지 링크
    const nextLink = document.createElement('a');
    nextLink.href = '#';
    nextLink.textContent = '»';
    nextLink.classList.toggle('disabled', currentPage === totalPages);
    nextLink.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage < totalPages) {
            loadNotices(currentPage + 1); // 다음 페이지 로드
        }
    });
    paginationContainer.appendChild(nextLink);
}

// 페이지 로드 시 공지사항과 페이지네이션을 초기화할 피룡가 있을때 이 코드 활성화
// 페이지 로드 시 공지사항 리스트와 페이지네이션 로드
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = parseInt(urlParams.get('page')) || 1;
    loadNotices(currentPage);
    loadPagination(currentPage);
});



