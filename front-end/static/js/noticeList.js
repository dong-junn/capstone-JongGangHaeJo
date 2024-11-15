// REST API를 이용해 공지사항 리스트를 불러오는 함수
async function loadNotices() {
    try {
        const response = await fetchWithAuth('/api/notices');
        if (response.ok) {
            const notices = await response.json();
            const noticeList = document.querySelector('#notice-list tbody');
            noticeList.innerHTML = ''; // 기존 항목 초기화

            notices.forEach((notice, index) => {
                const noticeRow = document.createElement('tr');
                noticeRow.innerHTML = `
                    <td>${index + 1}</td>
                    <td><a href="/notice/${notice.id}">${notice.title}</a></td>
                    <td>${notice.hasAttachment ? '<i class="file-icon"></i>' : ''}</td>
                    <td>${notice.createdDate}</td>
                    <td>${notice.viewCount}</td>
                `;
                noticeList.appendChild(noticeRow);
            });
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
async function loadPagination(currentPage) {
    try {
        const response = await fetchWithAuth(`/api/notices/pages?page=${currentPage}`);
        if (response.ok) {
            const paginationData = await response.json();
            const totalPages = paginationData.totalPages;
            const paginationContainer = document.querySelector('.pagination');
            paginationContainer.innerHTML = ''; // 기존 페이지네이션 초기화

            // 이전 페이지 링크
            const prevLink = document.createElement('a');
            prevLink.href = `?page=${currentPage - 1}`;
            prevLink.textContent = '«';
            prevLink.classList.toggle('disabled', currentPage === 1);
            paginationContainer.appendChild(prevLink);

            // 페이지 번호 링크
            for (let i = 1; i <= totalPages; i++) {
                const pageLink = document.createElement(i === currentPage ? 'strong' : 'a');
                pageLink.textContent = i;
                if (i !== currentPage) {
                    pageLink.href = `?page=${i}`;
                }
                paginationContainer.appendChild(pageLink);
            }

            // 다음 페이지 링크
            const nextLink = document.createElement('a');
            nextLink.href = `?page=${currentPage + 1}`;
            nextLink.textContent = '»';
            nextLink.classList.toggle('disabled', currentPage === totalPages);
            paginationContainer.appendChild(nextLink);
        } else {
            const errorData = await response.json();
            alert(`페이지네이션 정보를 불러오지 못했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('페이지네이션 정보를 불러오는 중 오류 발생:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 페이지 로드 시 공지사항 리스트와 페이지네이션 로드
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = parseInt(urlParams.get('page')) || 1;
    loadNotices();
    loadPagination(currentPage);
});
