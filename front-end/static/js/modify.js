// 프로젝트 목록을 불러오는 함수
async function loadProjects(currentPage = 1) {
    skeletonUI.show('.project-list', 'adminProjectCard', 9);
    
    try {
        const response = await fetchWithAuth(`/admin/post?page=${currentPage}&size=9&sort=createdAt,desc`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const projectsData = await response.json();
            const projects = projectsData.content;
            const projectList = document.querySelector('.project-list');
            
            skeletonUI.hide('.project-list');
            projectList.innerHTML = '';

            projects.forEach((project) => {
                const projectElement = document.createElement('div');
                projectElement.className = 'project-item';
                projectElement.innerHTML = `
                    <h3>${project.title}</h3>
                    <p>작성자: ${project.username}</p>
                    <p>팀명: ${project.team}</p>
                    <p>등록일: ${new Date(project.createdAt).toLocaleDateString('ko-KR')}</p>
                    <div class="project-actions">
                        <button class="view-button" onclick="window.open('/front-end/templates/board/project/detail.html?id=${project.id}', '_blank')">보기</button>
                        <button class="delete-button" onclick="deleteProject(${project.id})">삭제</button>
                    </div>
                `;
                projectList.appendChild(projectElement);
            });

            updatePagination(projectsData.totalPages, currentPage);
        }
    } catch (error) {
        console.error('Error loading projects:', error);
        alert('프로젝트 목록을 불러오는 중 오류가 발생했습니다.');
    } finally {
        skeletonUI.hide('.project-list');
    }
}

// 페이지네이션 업데이트 함수
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

// 페이지네이션 버튼 생성 함수
function createPageButton(text, pageNum, isActive = false) {
    const button = document.createElement('button');
    button.textContent = text;
    if (isActive) button.className = 'active';
    button.onclick = () => loadProjects(pageNum);
    return button;
}

// 프로젝트 삭제 함수
async function deleteProject(projectId) {
    if (!confirm('이 프로젝트를 삭제하시겠습니까?')) return;

    try {
        const response = await fetchWithAuth(`/admin/post/${projectId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('프로젝트가 삭제되었습니다.');
            loadProjects(1);
        } else {
            const errorData = await response.json();
            alert(`삭제 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error deleting project:', error);
        alert('프로젝트 삭제 중 오류가 발생했습니다.');
    }
}

// 초기 로드
document.addEventListener('DOMContentLoaded', () => {
    if (!checkAuth()) return;
    loadProjects(1);
});

