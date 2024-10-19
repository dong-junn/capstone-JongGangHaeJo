// REST API를 이용해 공지사항을 등록하는 함수
async function submitNotice() {
    const formData = new FormData(document.getElementById('noticeForm'));
    try {
        const response = await fetch('https://your-api-endpoint.com/notices', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('공지사항이 성공적으로 등록되었습니다.');
            document.getElementById('noticeForm').reset();
            loadNotices(); // 공지사항 리스트를 다시 로드하여 갱신
        } else {
            alert('공지사항 등록에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('Error submitting notice:', error);
        alert('오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
}

// REST API를 이용해 공지사항 리스트를 로드하는 함수
async function loadNotices() {
    try {
        const response = await fetch('https://your-api-endpoint.com/notices');
        const notices = await response.json();

        const noticeList = document.getElementById('notice-list');
        noticeList.innerHTML = '';

        notices.forEach(notice => {
            const noticeElement = document.createElement('div');
            noticeElement.className = 'notice-item';
            noticeElement.innerHTML = `<h3>${notice.title}</h3><p>${notice.content}</p>`;
            noticeList.appendChild(noticeElement);
        });
    } catch (error) {
        console.error('Error loading notices:', error);
    }
}

// 페이지 로드 시 공지사항을 로드하고 header와 footer를 포함시킴
document.addEventListener('DOMContentLoaded', () => {
    includeHTML();
    loadNotices();
});

// 모듈을 포함시키는 함수 (header와 footer 포함)
async function includeHTML() {
    try {
        const headerResponse = await fetch('module/header.html');
        const headerHtml = await headerResponse.text();
        document.getElementById('header').innerHTML = headerHtml;

        const footerResponse = await fetch('module/footer.html');
        const footerHtml = await footerResponse.text();
        document.getElementById('footer').innerHTML = footerHtml;
    } catch (error) {
        console.error('Error loading modules:', error);
    }
}
