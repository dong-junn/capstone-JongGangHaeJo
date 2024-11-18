// 검색 상태를 저장할 변수
let currentSearchId = '';

async function loadUsers(currentPage = 1) {
    skeletonUI.show('.user-list', 'userCard', 10);
    
    try {
        // 검색어가 있는 경우 검색 파라미터 추가
        const searchParam = currentSearchId ? `?id=${currentSearchId}&page=${currentPage}` : `?page=${currentPage}`;
        const response = await fetchWithAuth(`/admin/user${searchParam}&size=10`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const userData = await response.json();
            const users = userData.content;
            const userList = document.querySelector('.user-list');
            
            skeletonUI.hide('.user-list');
            userList.innerHTML = '';

            if (users.length === 0) {
                userList.innerHTML = '<div class="no-results">검색 결과가 없습니다.</div>';
                return;
            }

            users.forEach((user) => {
                const userElement = document.createElement('div');
                userElement.className = 'user';
                userElement.innerHTML = `
                    <p>ID: ${user.id}</p>
                    <p>사용자명: ${user.name}</p>
                    <p>이메일: ${user.email || '미등록'}</p>
                    <p>권한: ${user.roles.includes('ROLE_ADMIN') ? '관리자 회원' : '일반 회원'}</p>
                    <button class="delete-button" onclick="deleteUser('${user.id}')">삭제</button>
                `;
                userList.appendChild(userElement);
            });

            updatePagination(userData.totalPages, currentPage);
        }
    } catch (error) {
        console.error('Error loading users:', error);
        alert('사용자 목록을 불러오는 중 오류가 발생했습니다.');
    } finally {
        skeletonUI.hide('.user-list');
    }
}

async function deleteUser(userId) {
    if (!confirm('이 사용자를 삭제하시겠습니까?')) return;

    try {
        const response = await fetchWithAuth(`/admin/user/${userId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('사용자가 삭제되었습니다.');
            loadUsers(1);
        } else {
            const errorData = await response.json();
            alert(`삭제 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        alert('사용자 삭제 중 오류가 발생했습니다.');
    }
}

function updatePagination(totalPages, currentPage) {
    const paginationContainer = document.querySelector('.pagination');
    paginationContainer.innerHTML = '';

    if (currentPage > 1) {
        paginationContainer.appendChild(createPageButton('이전', currentPage - 1));
    }

    for (let i = 1; i <= totalPages; i++) {
        paginationContainer.appendChild(createPageButton(i, i, i === currentPage));
    }

    if (currentPage < totalPages) {
        paginationContainer.appendChild(createPageButton('다음', currentPage + 1));
    }
}

function createPageButton(text, pageNum, isActive = false) {
    const button = document.createElement('button');
    button.textContent = text;
    if (isActive) button.className = 'active';
    button.onclick = () => loadUsers(pageNum);
    return button;
}

// 검색 함수
async function searchUser() {
    const searchInput = document.getElementById('searchInput');
    currentSearchId = searchInput.value.trim();
    
    if (currentSearchId) {
        await loadUsers(1);
    }
}

// 검색 초기화 함수
async function resetSearch() {
    const searchInput = document.getElementById('searchInput');
    searchInput.value = '';
    currentSearchId = '';
    await loadUsers(1);
}

// Enter 키로 검색 실행
document.getElementById('searchInput')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        searchUser();
    }
});

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    if (!checkAuth()) return;
    loadUsers(1);
});
