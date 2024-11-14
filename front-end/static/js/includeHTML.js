// 헤더와 푸터를 동적으로 로드하는 함수
async function includeHTML() {
    try {
        const headerResponse = await fetch('/front-end/templates/layout/header.html');
        const headerHtml = await headerResponse.text();
        document.querySelector('.header-container').innerHTML = headerHtml;

        const footerResponse = await fetch('/front-end/templates/layout/footer.html');
        const footerHtml = await footerResponse.text();
        document.querySelector('.footer-container').innerHTML = footerHtml;
    } catch (error) {
        console.error('모듈 로드 중 오류 발생:', error);
    }
}

// 페이지 로드 시 헤더와 푸터를 포함시킴
document.addEventListener('DOMContentLoaded', includeHTML);
