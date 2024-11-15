// REST API를 이용해 프로젝트 리스트를 불러오는 함수
async function loadProjects(currentPage = 1) {
    try {
        const response = await fetchWithoutAuth(`/api/post?page=${currentPage}`);
        if (response.ok) {
            const projectsData = await response.json();
            const projects = projectsData.content; // 'content' 배열로 접근
            const projectsContainer = document.querySelector('.projects-container');
            projectsContainer.innerHTML = ''; // 기존 내용을 초기화

            // 프로젝트 리스트 동적 생성
            projects.forEach((project, index) => {
                const projectElement = document.createElement('div');
                projectElement.className = 'project-info';
                projectElement.innerHTML = `
                    <a href="../../board/project/detail.html?id=${project.id}">
                        <img src="${project.imageUrl || 'default-image-url'}" alt="프로젝트 이미지">
                        <div class="project-details">
                            <h2>${project.title}</h2>
                            <p>작성자: <span>${project.team}</span></p>
                            <p>완성 날짜: <span>${project.createdAt}</span></p>
                            <p>조회수: <span>${project.viewCount}</span></p>
                        </div>
                    </a>
                `;
                projectsContainer.appendChild(projectElement);
            });

            // 페이지네이션 로드
            loadPagination(projectsData.totalPages, currentPage);
        } else {
            const errorData = await response.json();
            alert(`프로젝트 목록을 불러오지 못했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error loading projects:', error);
        alert(`오류가 발생했습니다: ${error.message}`);
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
            loadProjects(currentPage - 1); // 이전 페이지 로드
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
                loadProjects(i);
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
            loadProjects(currentPage + 1); // 다음 페이지 로드
        }
    });
    paginationContainer.appendChild(nextLink);
}

// 페이지 로드 시 프로젝트 리스트 및 페이지네이션 로드
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = parseInt(urlParams.get('page')) || 1;
    loadProjects(currentPage);
    loadPagination(currentPage);
});
