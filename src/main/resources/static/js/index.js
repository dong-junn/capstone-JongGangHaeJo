// REST API를 이용해 데이터를 불러오는 함수
async function loadProjects() {
    try {
        const response = await fetch('http://18.118.128.174:8080/post');
        const projects = await response.json();

        const thumbnailsContainer = document.getElementById('thumbnails');
        thumbnailsContainer.innerHTML = ''; // 기존 썸네일 초기화

        projects.forEach(project => {
            const thumbnail = document.createElement('div');
            thumbnail.className = 'thumbnail';
            thumbnail.innerHTML = `<img src="${project.imageUrl}" alt="${project.title}">`;
            thumbnailsContainer.appendChild(thumbnail);
        });
    } catch (error) {
        console.error('Error loading projects:', error);
    }
}

    document.addEventListener("DOMContentLoaded", function() {
        const imageSlide = document.querySelector('.image-slide');
        const videoContainer = document.querySelector('.video-container');

        // 이미지가 먼저 나타나고, 1초 후에 영상을 보여줌
        setTimeout(function() {
            // 이미지를 숨기고 영상을 표시
            imageSlide.style.opacity = '0'; // 이미지가 사라지도록
            setTimeout(function() {
                videoContainer.style.display = 'block'; // 영상이 나타나도록
                imageSlide.style.display = 'none'; // 이미지를 완전히 숨김
            }, 1000); // 이미지 사라진 후 1초 후 영상이 표시되도록
        }, 1000); // 1초 후 영상이 나타나도록
    });


// 더보기 버튼을 눌렀을 때 추가 프로젝트 로드
async function loadMoreProjects() {
    try {
        const response = await fetch('src/main/resources/templates/board/project/detail.html');
        const moreProjects = await response.json();

        const thumbnailsContainer = document.getElementById('thumbnails');

        moreProjects.forEach(project => {
            const thumbnail = document.createElement('div');
            thumbnail.className = 'thumbnail';
            thumbnail.innerHTML = `<img src="${project.imageUrl}" alt="${project.title}">`;
            thumbnailsContainer.appendChild(thumbnail);
        });
    } catch (error) {
        console.error('Error loading more projects:', error);
    }
}

// HTML 페이지가 로드될 때 프로젝트를 불러옴
document.addEventListener('DOMContentLoaded', loadProjects);

// 모듈을 포함시키는 함수 (header와 footer 포함)
async function includeHTML() {
    try {
        const headerResponse = await fetch('http://127.0.0.1:5500/src/main/resources/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.getElementById('header').innerHTML = headerHtml;

        const footerResponse = await fetch('http://127.0.0.1:5500/src/main/resources/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.getElementById('footer').innerHTML = footerHtml;
    } catch (error) {
        console.error('Error loading modules:', error);
    }
}

// 페이지 로드 시 header와 footer를 포함시킴
includeHTML();

