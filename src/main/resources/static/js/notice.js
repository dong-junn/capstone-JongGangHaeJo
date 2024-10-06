document.addEventListener('DOMContentLoaded', function() {
    const notices = [
        {
            title: "공지사항 제목 1",
            content: "공지사항 내용 1입니다. 여기에 자세한 내용을 입력하세요.",
            date: "2024-06-04"
        },
        {
            title: "공지사항 제목 2",
            content: "공지사항 내용 2입니다. 여기에 자세한 내용을 입력하세요.",
            date: "2024-05-28"
        }
        // 더 많은 공지사항 추가 가능
    ];

    const noticeList = document.querySelector('.notice-list');

    function renderNotices() {
        noticeList.innerHTML = '';
        notices.forEach(notice => {
            const noticeItem = document.createElement('div');
            noticeItem.className = 'notice-item';

            noticeItem.innerHTML = `
                <h2>${notice.title}</h2>
                <p>${notice.content}</p>
                <span class="date">${notice.date}</span>
            `;

            noticeList.appendChild(noticeItem);
        });
    }

    renderNotices();

    // 관리자 여부 체크
    const isAdmin = false; // 로그인 상태 및 권한 확인 로직 필요
    const adminControls = document.querySelector('.admin-controls');

    if (isAdmin) {
        adminControls.style.display = 'block';
    }

    const noticeForm = document.getElementById('noticeForm');
    noticeForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;
        const date = new Date().toISOString().split('T')[0];

        notices.push({ title, content, date });

        renderNotices();

        noticeForm.reset();
    });
});
