// REST API를 이용해 프로젝트 세부 정보를 로드하는 함수
async function loadProjectDetails() {
    const projectId = "YOUR_PROJECT_ID"; // 프로젝트 ID를 동적으로 설정해야 함
    try {
        const response = await fetch(`http://18.118.128.174:8080/post`);
        const project = await response.json();

        // 프로젝트 세부 정보를 DOM에 업데이트
        document.getElementById('projectOverview').innerText = project.content;
        document.getElementById('projectPoster').src = project.posterUrl;
        document.getElementById('projectVideo').children[0].src = project.videoUrl;
        document.getElementById('youtubeVideo').src = `https://www.youtube.com/embed/${project.youtubeId}`;
        document.getElementById('likeNum').innerText = project.likes;
        document.getElementById('commentNum').innerText = project.comments.length;
        
        // 댓글 목록을 로드
        loadComments(project.comments);
    } catch (error) {
        console.error('Error loading project details:', error);
    }
}

// 댓글 목록을 로드하는 함수
function loadComments(comments) {
    const commentsList = document.getElementById('commentsList');
    commentsList.innerHTML = ''; // 기존 댓글 초기화

    // 각 댓글을 화면에 추가
    comments.forEach(comment => {
        const commentElement = document.createElement('div');
        commentElement.className = 'comment-item';
        commentElement.innerText = comment.content;
        
        // 댓글 삭제 버튼 추가
        const deleteButton = document.createElement('button');
        deleteButton.className = 'delete-comment-button';
        deleteButton.innerText = '댓글 삭제';
        deleteButton.onclick = () => deleteComment(comment.id);
        
        commentElement.appendChild(deleteButton);
        commentsList.appendChild(commentElement);
    });
}

// 댓글을 추가하는 함수
async function submitComment() {
    const projectId = "YOUR_PROJECT_ID"; // 프로젝트 ID를 동적으로 설정해야 함
    const commentContent = document.getElementById('commentInput').value;
    try {
        // 댓글을 서버에 POST 요청으로 추가
        const response = await fetch(`https://your-api-endpoint.com/projects/${projectId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: commentContent })
        });

        if (response.ok) {
            alert('댓글이 성공적으로 등록되었습니다.');
            document.getElementById('commentInput').value = ''; // 댓글 입력창 초기화
            loadProjectDetails(); // 댓글을 다시 로드하여 갱신
        } else {
            alert('댓글 등록에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('Error submitting comment:', error);
    }
}

// 프로젝트 삭제 함수
async function deleteProject() {
    const projectId = "YOUR_PROJECT_ID"; // 프로젝트 ID를 동적으로 설정해야 함
    try {
        // 서버에 DELETE 요청을 보내서 프로젝트 삭제
        const response = await fetch(`https://your-api-endpoint.com/projects/${projectId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('프로젝트가 성공적으로 삭제되었습니다.');
            window.location.href = '/'; // 메인 페이지로 리다이렉트
        } else {
            alert('프로젝트 삭제에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('프로젝트 삭제 중 오류 발생:', error);
    }
}

// 댓글 삭제 함수
async function deleteComment(commentId) {
    const projectId = "YOUR_PROJECT_ID"; // 프로젝트 ID를 동적으로 설정해야 함
    try {
        // 서버에 DELETE 요청을 보내서 댓글 삭제
        const response = await fetch(`https://your-api-endpoint.com/projects/${projectId}/comments/${commentId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('댓글이 성공적으로 삭제되었습니다.');
            loadProjectDetails(); // 댓글 목록 갱신
        } else {
            alert('댓글 삭제에 실패했습니다. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('댓글 삭제 중 오류 발생:', error);
    }
}
// HTML 페이지가 로드될 때 프로젝트를 불러옴
document.addEventListener('DOMContentLoaded', loadProjects);

// 모듈을 포함시키는 함수 (header와 footer 포함)
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

// HTML 페이지가 로드될 때 프로젝트를 불러옴
document.addEventListener('DOMContentLoaded', async () => {

    loadProjectDetails(); // 헤더와 푸터가 로드된 후에 프로젝트 세부 정보를 로드
});
