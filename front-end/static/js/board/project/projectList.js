// REST API를 이용해 프로젝트 리스트를 불러오는 함수
async function loadProjects(currentPage = 1) {
    skeletonUI.show('.projects-container', 'projectCard', 6);
    try {
        const response = await fetchWithoutAuth(`/post?page=${currentPage}&size=12&sort=createdAt,desc`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            skeletonUI.hide('.projects-container');
            const projectsData = await response.json();
            const projects = projectsData.content; // 'content' 배열로 접근
            const projectsContainer = document.querySelector('.projects-container');
            projectsContainer.innerHTML = ''; // 기존 내용을 초기화

            projects.forEach((project) => {
                const thumbnailImage = project.files?.find(file => file.thumbnailUrl)?.thumbnailUrl;
                
                const projectElement = document.createElement('div');
                projectElement.className = 'project-info';
                projectElement.innerHTML = `
                    <a href="/front-end/templates/board/project/projectDetail.html?id=${project.id}">
                        ${thumbnailImage ? 
                            `<img src="${thumbnailImage}" alt="프로젝트 이미지">` :
                            `<div class="no-image">
                                <span>이미지가 없는 게시물입니다.</span>
                            </div>`
                        }
                        <div class="project-details">
                            <h2>${project.title}</h2>
                            <p>팀 명: <span>${project.team}</span></p>
                            <p>업로드 날짜: <span>${new Date(project.createdAt).toLocaleDateString('ko-KR')}</span></p>
                            <p>조회수: <span>${project.viewCount}</span></p>
                        </div>
                    </a>
                `;
                projectsContainer.appendChild(projectElement);
            });

            // 페이지네이션 로드
            loadPagination(projectsData.totalPages, currentPage);
            
            // project-detail 섹션으로 스크롤
            scrollToTop();
        } else {
            const errorData = await response.json();
            alert(`프로젝트 목록을 불러오지 못했습니다: ${errorData.message}`);
        }
    } catch (error) {
        if (error.name === 'AbortError') {
            return;
        }
        console.error('Error loading projects:', error);
        alert(`오류가 발생했습니다: ${error.message}`);
    } finally {
        skeletonUI.hide('.projects-container');
    }
}
// 최상단으로 스크롤하는 함수
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth' // 부드러운 스크롤 효과
    });
}

// 페이지네이션을 동적으로 로드하는 함수
function loadPagination(totalPages, currentPage) {
    const paginationContainer = document.querySelector('.pagination');
    paginationContainer.innerHTML = '';

    // 페이지 그룹 계산 (한 번에 5개의 페이지 번호만 표시)
    const pageGroupSize = 5;
    const currentGroup = Math.floor((currentPage - 1) / pageGroupSize);
    const startPage = currentGroup * pageGroupSize + 1;
    const endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    // 처음 페이지로 이동
    if (currentPage > pageGroupSize) {
        const firstPage = createPageLink('처음', 1);
        paginationContainer.appendChild(firstPage);
    }

    // 이전 페이지 그룹으로 이동
    if (currentGroup > 0) {
        const prevGroup = createPageLink('이전', (currentGroup - 1) * pageGroupSize + 1);
        paginationContainer.appendChild(prevGroup);
    }

    // 페이지 번호 링크
    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement('span');
        pageItem.className = 'page-item';
        
        if (i === currentPage) {
            // 현재 페이지는 강조 표시
            pageItem.innerHTML = `<strong class="current-page">${i}</strong>`;
        } else {
            // 다른 페이지는 클릭 가능한 링크
            const pageLink = createPageLink(i, i);
            pageItem.appendChild(pageLink);
        }
        
        paginationContainer.appendChild(pageItem);
    }

    // 다음 페이지 그룹으로 이동
    if (endPage < totalPages) {
        const nextGroup = createPageLink('다음', Math.min(endPage + 1, totalPages));
        paginationContainer.appendChild(nextGroup);
    }

    // 마지막 페이지로 이동
    if (endPage < totalPages) {
        const lastPage = createPageLink('끝', totalPages);
        paginationContainer.appendChild(lastPage);
    }
}

// 페이지 링크 생성 함수
function createPageLink(text, pageNumber) {
    const link = document.createElement('a');
    link.href = '#';
    link.textContent = text;
    link.className = 'page-link';
    link.addEventListener('click', async (e) => {
        e.preventDefault();
        await loadProjects(pageNumber);
    });
    return link;
}

// 페이지 로드 시 프로젝트 리스트 로드
document.addEventListener('DOMContentLoaded', () => {
    loadProjects(1);
});