async function loadProjects() {
    const thumbnailsContainer = document.getElementById('thumbnails');
    
    // 기존 내용 제거
    thumbnailsContainer.innerHTML = '';
    
    // 스켈레톤 UI 표시 (Top3이므로 3개)
    const skeletonUI = new SkeletonUI();
    skeletonUI.show('#thumbnails', 'topProject', 3);

    try {
        const response = await fetchWithoutAuth('/', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const projects = await response.json();
            
            // 스켈레톤 UI 제거
            skeletonUI.hide('#thumbnails');
            thumbnailsContainer.innerHTML = ''; // 컨테이너 초기화

            // Top 3 프로젝트만 표시하도록 명시적으로 제한
            const top3Projects = projects.slice(0, 3);
            
            top3Projects.forEach(project => {
                const thumbnail = document.createElement('div');
                thumbnail.className = 'thumbnail';
                
                // 썸네일 URL 결정 로직
                const thumbnailImage = project.files?.find(file => file.thumbnailUrl)?.thumbnailUrl || '/front-end/static/img/default-thumbnail.jpg';
                
                thumbnail.innerHTML = `
                    <a href="/front-end/templates/board/project/projectDetail.html?id=${project.id}">
                        <img src="${thumbnailImage}" alt="${project.title}">
                        <div class="thumbnail-info">
                            <h3>${project.title}</h3>
                            <p>${project.username || '무명'}</p>
                        </div>
                    </a>
                `;
                thumbnailsContainer.appendChild(thumbnail);
            });
        }
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

