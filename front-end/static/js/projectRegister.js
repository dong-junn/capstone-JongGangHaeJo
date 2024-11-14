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
    formData.append('post', new Blob([JSON.stringify(json)], { type: 'application/json' }));            
    try {
        const response = await fetch('http://18.118.128.174:8080/post', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            window.location.href = '/projectList.html'; // 프로젝트 등록 성공 시 프로젝트 게시판으로 리다이렉트
        } else {
            const errorData = await response.json();
            alert(`프로젝트 등록에 실패했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error submitting project:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}
// 작성자 로그인 여부를 확인하고 수정 버튼을 동적으로 생성
function displayEditButton() {
    const isAuthorLoggedIn = true; // 실제 로그인 확인 로직 추가 필요
    const editButtonContainer = document.getElementById("editButtonContainer");

    if (isAuthorLoggedIn) {
        const editButton = document.createElement("button");
        editButton.className = "edit-button";
        editButton.innerText = "수정하기";
        editButton.onclick = () => {
            window.location.href = "/front-end/templates/board/project/projectRegister.html"; // 수정 페이지로 이동
        };
        editButtonContainer.appendChild(editButton);
    }
}

// 페이지 로드 시 수정 버튼 확인
document.addEventListener("DOMContentLoaded", () => {
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


 // 헤더와 푸터를 동적으로 로드하는 함수
async function includeHTML() {
    const headerResponse = await fetch('http://127.0.0.1:5500/front-end/templates/layout/header.html');
    const headerHtml = await headerResponse.text();
    document.getElementById('header').innerHTML = headerHtml;

    const footerResponse = await fetch('http://127.0.0.1:5500/front-end/templates/layout/footer.html');
    const footerHtml = await footerResponse.text();
    document.getElementById('footer').innerHTML = footerHtml;
}

// 페이지 로드 시 헤더와 푸터를 로드
document.addEventListener('DOMContentLoaded', includeHTML);
