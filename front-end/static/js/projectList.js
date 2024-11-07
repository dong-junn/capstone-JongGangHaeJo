// REST API를 이용해 프로젝트 리스트를 불러오는 함수
async function loadProjects() {
    try {
        const response = await fetch('http://18.118.128.174:8080/projects');
        if (response.ok) {
            const projects = await response.json();
            const projectsContainer = document.querySelector('.projects-container');
            projectsContainer.innerHTML = '';

            projects.forEach(project => {
                const projectElement = document.createElement('div');
                projectElement.className = 'project-info';
                projectElement.innerHTML = `
                    <a href="../../board/project/detail.html?id=${project.id}">
                        <img src="${project.imageUrl}" alt="프로젝트 이미지">
                        <div class="project-details">
                            <h2>${project.title}</h2>
                            <p>작성자: <span>${project.writer}</span></p>
                            <p>프로젝트 설명: <span>${project.content}</span></p>
                            <p>완성 날짜: <span>${project.createdDate}</span></p>
                            <p>조회수: <span>${project.view}</span></p>
                        </div>
                        <div class="detail-button">상세보기</div>
                    </a>
                `;
                projectsContainer.appendChild(projectElement);
            });
        } else {
            const errorData = await response.json();
            alert(`프로젝트 목록을 불러오지 못했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error loading projects:', error);
        alert(`오류가 발생했습니다: ${error.message}`);
    }
}

// 페이지네이션을 불러오는 함수
async function loadPagination(startPage, endPage, nowPage) {
    try {
        const paginationContainer = document.querySelector('main section.project-detail');
        const paginationBlock = document.createElement('div');
        paginationBlock.className = 'pagination';

        for (let page = startPage; page <= endPage; page++) {
            const pageElement = document.createElement(page === nowPage ? 'strong' : 'a');
            pageElement.textContent = page;
            pageElement.style.color = page === nowPage ? 'red' : '';
            if (page !== nowPage) {
                pageElement.href = `/posts/list?page=${page - 1}`;
            }
            paginationBlock.appendChild(pageElement);
        }

        paginationContainer.appendChild(paginationBlock);
    } catch (error) {
        console.error('Error loading pagination:', error);
    }
}

// 헤더와 푸터를 동적으로 로드하는 함수
async function includeHTML() {
    try {
        const headerResponse = await fetch('http://127.0.0.1:5500/front-end/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.getElementById('header').innerHTML = headerHtml;

        const footerResponse = await fetch('http://127.0.0.1:5500/front-end/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.getElementById('footer').innerHTML = footerHtml;
    } catch (error) {
        console.error('Error loading modules:', error);
    }
}

// 페이지 로드 시 헤더, 푸터, 프로젝트 리스트 및 페이지네이션 로드
document.addEventListener('DOMContentLoaded', () => {
    includeHTML();
    loadProjects();
    loadPagination(1, 5, 1); // 예시로 1~5페이지와 현재 페이지를 설정
});
