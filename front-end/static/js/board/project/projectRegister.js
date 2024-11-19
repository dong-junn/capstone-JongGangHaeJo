// REST API를 이용해 프로젝트를 등록하는 함수
async function submitProject() {
    const submitButton = document.querySelector('.submit-button');
    
    // 버튼 비활성화 및 로딩 상태 표시
    submitButton.disabled = true;
    const originalText = submitButton.textContent;
    submitButton.textContent = '등록 중...';
    
    const formData = new FormData(document.getElementById('register-form'));
    const json = {
        title: document.getElementById('projectName').value,
        content: document.getElementById('projectDescription').value,
        team: document.getElementById('teamMem').value,
        youtubelink: document.getElementById('yt_url').value
    };

    formData.append('post', new Blob([JSON.stringify(json)], { type: 'application/json' }));

    const fileInput = document.getElementById('projectFiles');
    if (fileInput.files.length > 0) {
        Array.from(fileInput.files).forEach((file, index) => {
            formData.append(`files`, file);
        });
    }

    const thumbnailInput = document.getElementById('poster');
    if (thumbnailInput.files.length > 0) {
        formData.append('thumbnail', thumbnailInput.files[0]);
    }

    try {
        const response = await fetchWithAuth('/post', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            window.location.href = '/front-end/templates/board/project/projectList.html';
        } else {
            const errorData = await response.json();
            const validationMessages = errorData.validation 
                ? Object.values(errorData.validation).join('\n')
                : errorData.message;
            alert(`프로젝트 등록에 실패했습니다:\n${validationMessages}`);
            
            // 에러 발생 시 버튼 상태 복구
            submitButton.disabled = false;
            submitButton.textContent = originalText;
        }
    } catch (error) {
        if (error.name === 'AbortError') {
            return;
        }
        console.error('Error submitting project:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
        
        // 에러 발생 시 버튼 상태 복구
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    }
}
// 작성자 로그인 여부를 확인하고 수정 버튼을 동적으로 생성
function displayEditButton() {
    const editButtonContainer = document.getElementById("editButtonContainer");    
    const editButton = document.createElement("button");
    editButton.className = "edit-button";
    editButton.innerText = "수정하기";
    editButton.onclick = () => {
        window.location.href = "/front-end/templates/board/project/projectRegister.html"; // 수정 페이지로 이동
    };
    editButtonContainer.appendChild(editButton);
    
}

// 페이지 로드 시 수정 버튼 확인
document.addEventListener("DOMContentLoaded", () => {
    if (!checkAuth()) return;

    includeHTML();
    displayEditButton();
});
function showThumbnails(input) {
    const container = document.getElementById("thumbnail-container");
    container.innerHTML = ""; // 초기화

    if (input.files) {
        Array.from(input.files).forEach(file => {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.createElement("img");
                img.src = e.target.result;
                img.className = "thumbnail";
                container.appendChild(img);
            };
            reader.readAsDataURL(file);
        });
    }
}