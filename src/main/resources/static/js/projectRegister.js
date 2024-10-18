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
