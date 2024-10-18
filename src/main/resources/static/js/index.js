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

// 더보기 버튼을 눌렀을 때 추가 프로젝트 로드
async function loadMoreProjects() {
    try {
        const response = await fetch('http://18.118.128.174:8080/post?more=true&sortBy=views');
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
        const headerResponse = await fetch('http://18.118.128.174:8080/docs/index.html');
        const headerHtml = await headerResponse.text();
        document.getElementById('header').innerHTML = headerHtml;

        const footerResponse = await fetch('http://18.118.128.174:8080/docs/index.html');
        const footerHtml = await footerResponse.text();
        document.getElementById('footer').innerHTML = footerHtml;
    } catch (error) {
        console.error('Error loading modules:', error);
    }
}

// 페이지 로드 시 header와 footer를 포함시킴
includeHTML();
