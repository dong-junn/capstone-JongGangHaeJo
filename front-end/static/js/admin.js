// REST API를 이용해 관리자 정보를 가져오는 함수
async function loadAdminInfo() {
    try {
        const response = await fetchWithAuth('/api/admin/info', {
            method: 'GET'
        });

        if (response.ok) {
            const adminInfo = await response.json();
            document.querySelector('.welcome-message').textContent = `관리자님, ${adminInfo.name}님 환영합니다!`;
        } else {
            alert('관리자 정보를 불러오지 못했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('관리자 정보 로드 중 오류 발생:', error);
        alert('관리자 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

// REST API를 이용해 대시보드 데이터를 가져오는 함수
async function loadDashboardData() {
    try {
        const response = await fetchWithAuth('/api/admin/dashboard', {
            method: 'GET'
        });

        if (response.ok) {
            const dashboardData = await response.json();

            // 대시보드 데이터를 HTML로 추가
            const dashboardContainer = document.createElement('div');
            dashboardContainer.className = 'dashboard-container';
            dashboardContainer.innerHTML = `
                <h2>대시보드</h2>
                <p>등록된 프로젝트 수: ${dashboardData.projectCount}</p>
                <p>등록된 공지사항 수: ${dashboardData.noticeCount}</p>
                <p>등록된 회원 수: ${dashboardData.userCount}</p>
            `;
            document.body.insertBefore(dashboardContainer, document.querySelector('.footer-container'));
        } else {
            alert('대시보드 데이터를 불러오지 못했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('대시보드 데이터 로드 중 오류 발생:', error);
        alert('대시보드 데이터를 불러오는 중 오류가 발생했습니다.');
    }
}

// 로그아웃 기능
async function logoutAdmin() {
    try {
        const response = await fetchWithAuth('/api/admin/logout', {
            method: 'POST'
        });

        if (response.ok) {
            alert('로그아웃 되었습니다.');
            window.location.href = '/front-end/templates/user/login/login.html';
        } else {
            alert('로그아웃에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('로그아웃 중 오류 발생:', error);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
}

// 페이지 로드 시 필요한 데이터 로드
document.addEventListener('DOMContentLoaded', () => {
    loadAdminInfo();
    loadDashboardData();

    // 로그아웃 버튼 동적 추가
    const headerContainer = document.querySelector('.header-container');
    const logoutButton = document.createElement('button');
    logoutButton.textContent = '로그아웃';
    logoutButton.className = 'logout-button';
    logoutButton.addEventListener('click', logoutAdmin);
    headerContainer.appendChild(logoutButton);
});
