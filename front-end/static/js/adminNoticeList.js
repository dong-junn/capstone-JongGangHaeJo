// 전역 변수 선언
let currentPage = 1;
const pageSize = 10;

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    loadNotices(currentPage);
});

// 공지사항 목록 불러오기
async function loadNotices(page) {
    try {
        const response = await fetchWithAuth(`/notices?page=${page-1}&size=${pageSize}`);
        if (!response.ok) {
            throw new Error('공지사항을 불러오는데 실패했습니다.');
        }

        const noticesData = await response.json();
        displayNotices(noticesData, page);
        createPagination(noticesData.totalPages, page);
    } catch (error) {
        console.error('Error:', error);
        alert('공지사항을 불러오는데 실패했습니다.');
    }
}

// 공지사항 화면에 표시
function displayNotices(noticesData, page) {
    const noticeList = document.querySelector('#notice-list tbody');
    noticeList.innerHTML = '';

    noticesData.content.forEach((notice, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${(page - 1) * pageSize + index + 1}</td>
            <td><a href="/front-end/templates/board/notice/noticeDetail.html?id=${notice.id}">${notice.title}</a></td>
            <td>${notice.hasFile ? '📎' : ''}</td>
            <td>${formatDate(notice.createdAt)}</td>
            <td>${notice.viewCount}</td>
            <td class="manage-buttons">
                <button class="edit-button" onclick="editNotice(${notice.id})">수정</button>
                <button class="delete-button" onclick="deleteNotice(${notice.id})">삭제</button>
            </td>
        `;
        noticeList.appendChild(row);
    });
}

// 페이지네이션 생성
function createPagination(totalPages, currentPage) {
    const pagination = document.querySelector('.pagination');
    pagination.innerHTML = '';

    // 이전 페이지 버튼
    if (currentPage > 1) {
        const prevButton = document.createElement('button');
        prevButton.textContent = '이전';
        prevButton.onclick = () => loadNotices(currentPage - 1);
        pagination.appendChild(prevButton);
    }

    // 페이지 번호 버튼
    for (let i = 1; i <= totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i;
        pageButton.classList.toggle('active', i === currentPage);
        pageButton.onclick = () => loadNotices(i);
        pagination.appendChild(pageButton);
    }

    // 다음 페이지 버튼
    if (currentPage < totalPages) {
        const nextButton = document.createElement('button');
        nextButton.textContent = '다음';
        nextButton.onclick = () => loadNotices(currentPage + 1);
        pagination.appendChild(nextButton);
    }
}

// 공지사항 수정
function editNotice(noticeId) {
    window.location.href = `/front-end/templates/board/notice/noticeEdit.html?id=${noticeId}`;
}

// 공지사항 삭제
async function deleteNotice(noticeId) {
    if (!confirm('이 공지사항을 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetchWithAuth(`/notices/${noticeId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('공지사항이 삭제되었습니다.');
            loadNotices(currentPage); // 현재 페이지 새로고침
        } else {
            throw new Error('삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('공지사항 삭제에 실패했습니다.');
    }
}

// 날짜 포맷 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
} 