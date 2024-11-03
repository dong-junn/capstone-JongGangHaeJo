// REST API를 이용해 프로젝트를 수정하는 함수
async function modifyProject() {
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
        const response = await fetch('http://18.118.128.174:8080/post/update', {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            window.location.href = '/projectList.html'; // 프로젝트 수정 성공 시 프로젝트 게시판으로 리다이렉트
        } else {
            const errorData = await response.json();
            alert(`프로젝트 수정에 실패했습니다: ${errorData.message}`);
        }
    } catch (error) {
        console.error('Error modifying project:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// 헤더와 푸터를 동적으로 로드하는 함수
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

// 페이지 로드 시 헤더와 푸터를 포함시킴
document.addEventListener('DOMContentLoaded', includeHTML);
