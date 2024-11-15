// REST API를 이용해 프로젝트를 등록하는 함수
async function submitProject() {
    const formData = new FormData(document.getElementById('register-form'));
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
        Array.from(fileInput.files).forEach((file, index) => {
            formData.append(`files`, file); // 다중 파일 처리
        });
    }

    // // 썸네일 이미지 추가
    // const thumbnailInput = document.getElementById('poster');
    // if (thumbnailInput.files.length > 0) {
    //     formData.append('files', thumbnailInput.files[0]);
    // }

    try {
        const response = await fetchWithAuth('/post', {
            method: 'POST',
            body: formData,
            headers: {
                // Content-Type을 제거하여 브라우저가 자동으로 설정하도록 함
                // multipart/form-data로 자동 설정됨
            }
        });

        if (response.ok) {
            window.location.href = '/front-end/templates/board/project/projectList.html'; // 프로젝트 등록 성공 시 프로젝트 게시판으로 리다이렉트
        } else {
            const errorData = await response.json();
            // validation 객체의 모든 에러 메시지를 추출
            const validationMessages = errorData.validation 
                ? Object.values(errorData.validation).join('\n')
                : errorData.message;
                
            alert(`프로젝트 등록에 실패했습니다:\n${validationMessages}`);
        }
    } catch (error) {
        console.error('Error submitting project:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
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