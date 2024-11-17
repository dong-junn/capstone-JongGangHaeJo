// REST API를 이용해 데이터를 불러오는 함수
async function loadProjects() {
    try {
        const response = await fetchWithoutAuth('/', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const projects = await response.json();

        const thumbnailsContainer = document.getElementById('thumbnails');
        thumbnailsContainer.innerHTML = ''; // 기존 썸네일 초기화

        projects.forEach(project => {
            const thumbnail = document.createElement('div');
            thumbnail.className = 'thumbnail';
            
            // 썸네일 URL 결정 로직
            const thumbnailImage = project.files?.find(file => file.thumbnailUrl)?.thumbnailUrl || '/front-end/static/img/default-thumbnail.jpg';
            
            thumbnail.innerHTML = `
                <a href="/front-end/templates/board/project/detail.html?id=${project.id}">
                    <img src="${thumbnailImage}" alt="${project.title}">
                    <div class="thumbnail-info">
                        <h3>${project.title}</h3>
                        <p>${project.team || '팀 이름'}</p>
                    </div>
                </a>
            `;
            thumbnailsContainer.appendChild(thumbnail);
        });
    } catch (error) {
        console.error('Error loading projects:', error);
    }
}

// 더보기 버튼을 눌렀을 때 추가 프로젝트 로드
// async function loadMoreProjects() {
//     try {
//         const response = await fetch('http://127.0.0.1:5500/front-end/templates/board/project/detail.html');
//         const moreProjects = await response.json();

//         const thumbnailsContainer = document.getElementById('thumbnails');

//         moreProjects.forEach(project => {
//             const thumbnail = document.createElement('div');
//             thumbnail.className = 'thumbnail';
//             thumbnail.innerHTML = `<img src="${project.imageUrl}" alt="${project.title}">`;
//             thumbnailsContainer.appendChild(thumbnail);
//         });
//     } catch (error) {
//         console.error('Error loading more projects:', error);
//     }
// }

// HTML 페이지가 로드될 때 프로젝트를 불러옴
document.addEventListener('DOMContentLoaded', loadProjects);

