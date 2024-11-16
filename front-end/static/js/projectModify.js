// URL에서 프로젝트 ID를 가져오는 함수
function getProjectIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// 프로젝트 데이터를 불러와 폼에 채워넣는 함수
async function loadProjectDetails() {
    projectId = getProjectIdFromUrl();
    
    if (!projectId) {
        alert('유효하지 않은 프로젝트 ID입니다.');
        window.location.href = '/front-end/templates/board/project/projectList.html';
        return;
    }

    try {
        const response = await fetchWithAuth(`/post/${projectId}`, {
            method: 'GET'
        });

        if (response.ok) {
            const project = await response.json();

            // 폼에 데이터 채우기
            document.getElementById('projectName').value = project.title || '';
            document.getElementById('teamMem').value = project.team || '';
            document.getElementById('projectDescription').value = project.content || '';
            document.getElementById('yt_url').value = project.youtubelink || '';

            // 썸네일 이미지 미리보기
            if (project.posterUrl) {
                const thumbnailContainer = document.getElementById('thumbnail-container');
                const imgElement = document.createElement('img');
                imgElement.src = project.posterUrl;
                imgElement.alt = '현재 썸네일';
                thumbnailContainer.appendChild(imgElement);
            }
        } else {
            const errorData = await response.json();
            alert(errorData.message || '프로젝트 정보를 불러오는데 실패했습니다.');
            window.location.href = '/front-end/templates/board/project/projectList.html';
        }
    } catch (error) {
        console.error('프로젝트 정보를 불러오는 중 오류 발생:', error);
        alert('프로젝트 정보를 불러오는데 실패했습니다.');
    }
}


// 프로젝트 수정 요청
async function submitProject() {
    const projectId = getProjectIdFromUrl();
    const form = document.getElementById('register-form');
    const formData = new FormData(form);

    if (!projectId) {
        alert('유효하지 않은 프로젝트 ID입니다.');
        return;
    }

    try {
        const response = await fetch(`/post/${projectId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        });

        if (response.ok) {
            alert('프로젝트가 성공적으로 수정되었습니다.');
            window.location.href = `/front-end/templates/board/project/detail.html?id=${projectId}`;
        } else {
            const errorData = await response.json();
            alert(errorData.message || '프로젝트 수정에 실패했습니다.');
        }
    } catch (error) {
        console.error('프로젝트 수정 중 오류 발생:', error);
        alert('프로젝트 수정 중 오류가 발생했습니다.');
    }
}

// 썸네일 미리보기를 표시하는 함수
function showThumbnails(input) {
    const container = document.getElementById('thumbnail-container');
    container.innerHTML = ''; // 기존 썸네일 초기화

    if (input.files) {
        Array.from(input.files).forEach(file => {
            // 파일 타입 확인
            if (!file.type.startsWith('image/')) {
                alert('이미지 파일만 업로드 가능합니다.');
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                const img = document.createElement('img');
                img.src = e.target.result; // 업로드한 이미지의 데이터 URL
                img.alt = '업로드된 썸네일';
                container.appendChild(img);
            };
            reader.readAsDataURL(file); // 파일을 읽어 데이터 URL 생성
        });
    }
}


// 페이지 로드 시 프로젝트 데이터를 불러옴
document.addEventListener('DOMContentLoaded', () => {
    loadProjectDetails();
    includeHTML();
});
