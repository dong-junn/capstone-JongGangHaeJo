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
            alert('프로젝트가 성공적으로 등록되었습니다.');
            document.getElementById('register-form').reset();
        } else {
            alert('프로젝트 등록에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('Error submitting project:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

 // 헤더와 푸터를 동적으로 로드하는 함수
 async function includeHTML() {
    const headerResponse = await fetch('http://127.0.0.1:5500/src/main/resources/templates/layout/header.html');
    const headerHtml = await headerResponse.text();
    document.getElementById('header').innerHTML = headerHtml;

    const footerResponse = await fetch('http://127.0.0.1:5500/src/main/resources/templates/layout/footer.html');
    const footerHtml = await footerResponse.text();
    document.getElementById('footer').innerHTML = footerHtml;
}

// 페이지 로드 시 헤더와 푸터를 로드
document.addEventListener('DOMContentLoaded', includeHTML);
