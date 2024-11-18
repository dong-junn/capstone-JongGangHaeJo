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

            // 기존 파일 목록 표시
            if (project.files && project.files.length > 0) {
                const fileListContainer = document.createElement('div');
                fileListContainer.className = 'existing-files';
                fileListContainer.innerHTML = `
                    <h3>기존 첨부파일</h3>
                    <p class="warning-text">추가 업로드 시 기존의 첨부파일은 제거되니 모든 파일을 선택하세요.</p>
                `;
                
                // 썸네일과 일반 파일 분리
                const thumbnails = project.files.filter(file => file.thumbnailUrl);
                const normalFiles = project.files.filter(file => !file.thumbnailUrl);

                // 썸네일 섹션 추가
                if (thumbnails.length > 0) {
                    const thumbnailSection = document.createElement('div');
                    thumbnailSection.className = 'thumbnail-section';
                    thumbnailSection.innerHTML = '<h4>현재 썸네일</h4>';
                    thumbnails.forEach(file => {
                        thumbnailSection.innerHTML += `
                            <img src="${file.thumbnailUrl}" alt="${file.fileName}">
                        `;
                    });
                    fileListContainer.appendChild(thumbnailSection);
                }

                // 일반 파일 목록 표시
                if (normalFiles.length > 0) {
                    const fileList = document.createElement('div');
                    fileList.className = 'file-list';
                    fileList.innerHTML = normalFiles.map(file => `
                        <div class="file-item">
                            <i class="fas fa-file"></i>
                            <span>${file.fileName}</span>
                        </div>
                    `).join('');
                    fileListContainer.appendChild(fileList);
                }

                // 파일 목록을 projectFiles 입력 필드 아래에 추가
                const projectFilesInput = document.getElementById('projectFiles');
                projectFilesInput.parentNode.insertBefore(fileListContainer, projectFilesInput.nextSibling);
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
    const formData = new FormData();  // 빈 FormData 생성
    
    // JSON 데이터를 생성하여 폼데이터에 추가
    const json = {
        title: document.getElementById('projectName').value,
        content: document.getElementById('projectDescription').value,
        team: document.getElementById('teamMem').value,
        youtubelink: document.getElementById('yt_url').value
    };

    // JSON 데이터를 폼데이터에 추가
    formData.append('post', new Blob([JSON.stringify(json)], { type: 'application/json' }));

    // 파일 데이터 추가
    const fileInput = document.getElementById('projectFiles');
    if (fileInput.files.length > 0) {
        Array.from(fileInput.files).forEach(file => {
            formData.append('files', file);
        });
    }

    // 썸네일 이미지 추가
    const thumbnailInput = document.getElementById('poster');
    if (thumbnailInput.files.length > 0) {
        formData.append('thumbnail', thumbnailInput.files[0]);
    }

    try {
        const response = await fetchWithAuth(`/post/${projectId}`, {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            alert('프로젝트가 성공적으로 수정되었습니다.');
            window.location.href = `/front-end/templates/board/project/projectDetail.html?id=${projectId}`;
        } else {
            const errorData = await response.json();
            const validationMessages = errorData.validation 
                ? Object.values(errorData.validation).join('\n')
                : errorData.message;
                
            alert(`프로젝트 수정에 실패했습니다:\n${validationMessages}`);
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
