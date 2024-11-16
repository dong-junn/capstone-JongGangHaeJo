// REST API를 이용해 프로젝트 세부 정보를 로드하는 함수
let projectId;
// URL에서 프로젝트 ID를 가져오는 함수
function getProjectIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

async function loadProjectDetails() {
    projectId = getProjectIdFromUrl();
    
    if (!projectId) {
        alert('유효하지 않은 게시물 번호입니다.');
        window.location.href = '/front-end/templates/board/project/projectList.html';
        return;
    }

    try {
        const response = await fetchWithoutAuth(`/post/${projectId}`, {
            method: 'GET'
        });

        if (response.ok) {
            const project = await response.json();

            // 프로젝트 세부 정보를 DOM에 업데이트
            document.querySelector('.project-header h1').innerText = project.title;
            
            // 메타 정보 업데이트
            const metaInfo = document.querySelector('.project-meta');
            metaInfo.innerHTML = `
                <span><i class="fas fa-users"></i> 팀명: ${project.team || '팀 이름'}</span>
                <span><i class="fas fa-user"></i> 작성자: ${project.username}</span>
                <span><i class="fas fa-calendar"></i> 등록일: ${new Date(project.createdAt).toLocaleDateString('ko-KR')}</span>
                <span><i class="fas fa-eye"></i> 조회수: ${project.viewcount}</span>
            `;

            // 작품 개요 업데이트
            document.querySelector('.project-extra-info p').innerText = project.content;

            // 포스터 이미지 업데이트
            if (project.posterUrl) {
                document.getElementById('projectPoster').src = project.posterUrl;
            }

            // 유튜브 동영상 업데이트
            if (project.youtubelink) {
                document.getElementById('youtubeVideo').src = project.youtubelink;
            }

            // 좋아요 수와 댓글 수 업데이트
            document.getElementById('likeNum').innerText = project.likes;
            document.getElementById('commentNum').innerText = project.comments.length;

            // 댓글 목록 로드
            loadComments(project.comments);
        } else {
            const errorData = await response.json();
            alert(errorData.message || '게시물을 불러오는데 실패했습니다.');
            window.location.href = '/front-end/templates/board/project/projectList.html';
        }
    } catch (error) {
        console.error('프로젝트 정보를 불러오는 중 오류 발생:', error);
        alert('프로젝트 정보를 불러오는데 실패했습니다.');
    }
}
// 댓글 목록을 로드하는 함수
function loadComments(comments) {
    const commentsList = document.getElementById('commentsList');
    commentsList.innerHTML = ''; // 기존 댓글 초기화

    // 각 댓글을 화면에 추가
    comments.forEach(comment => {
        const commentElement = document.createElement('div');
        commentElement.className = 'comment';
        
        // 현재 날짜 포맷팅
        const commentDate = new Date(comment.createdAt).toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        }).replace(/\./g, '.');

        commentElement.innerHTML = `
            <div class="comment-header">
                <strong class="comment-author">${comment.author || '작성자'}</strong>
                <span class="comment-date">${commentDate}</span>
            </div>
            <p class="comment-content">${comment.content}</p>
            <div class="comment-actions">
                <button class="reply-button" onclick="showReplyForm(this)">답글</button>
                <button class="delete-comment" onclick="deleteComment('${comment.id}')">삭제</button>
            </div>
            
            <!-- 답글 입력 폼 -->
            <div class="reply-form hidden">
                <textarea class="reply-input" placeholder="답글을 입력하세요"></textarea>
                <button class="submit-reply" onclick="submitReply(this)">답글 달기</button>
            </div>
            
            <!-- 답글 목록 -->
            <div class="replies">
                ${loadReplies(comment.replies || [])}
            </div>
        `;

        commentsList.appendChild(commentElement);
    });
}

// 답글 목록을 생성하는 함수
function loadReplies(replies) {
    return replies.map(reply => `
        <div class="reply">
            <div class="comment-header">
                <strong class="comment-author">${reply.author || '작성자'}</strong>
                <span class="comment-date">${new Date(reply.createdAt).toLocaleDateString('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit'
                }).replace(/\./g, '.')}</span>
            </div>
            <p class="comment-content">${reply.content}</p>
            <div class="comment-actions">
                <button class="reply-button" onclick="showReplyForm(this)">답글</button>
                <button class="delete-comment" onclick="deleteReply('${reply.id}')">삭제</button>
            </div>
            
            <!-- 답글의 답글 입력 폼 -->
            <div class="reply-form hidden">
                <textarea class="reply-input" placeholder="답글을 입력하세요"></textarea>
                <button class="submit-reply" onclick="submitReply(this)">답글 달기</button>
            </div>
            
            <!-- 중첩된 답글 목록 -->
            <div class="replies nested-replies">
                ${loadReplies(reply.replies || [])}
            </div>
        </div>
    `).join('');
}

// 답글 폼 표시/숨김 토글 함수
function showReplyForm(button) {
    const replyForm = button.closest('.comment, .reply').querySelector('.reply-form');
    replyForm.classList.toggle('hidden');
}

// 답글 제출 함수
async function submitReply(button) {
    const replyForm = button.closest('.reply-form');
    const replyInput = replyForm.querySelector('.reply-input');
    const replyText = replyInput.value.trim();
    const parentComment = button.closest('.comment, .reply');
    const parentId = parentComment.dataset.id;

    if (replyText) {
        try {
            const response = await fetchWithoutAuth(`/post/${projectId}/comments/${parentId}/replies`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ content: replyText })
            });

            if (response.ok) {
                replyInput.value = '';
                replyForm.classList.add('hidden');
                loadProjectDetails(); // 전체 댓글 목록 새로고침
            } else {
                const errorData = await response.json();
                alert(errorData.message);
            }
        } catch (error) {
            console.error('답글 등록 중 오류 발생:', error);
        }
    } else {
        alert('답글을 입력하세요!');
    }
}

// 댓글을 추가하는 함수
async function submitComment() {
    const commentInput = document.getElementById('commentInput');
    const commentContent = commentInput.value.trim();

    if (!commentContent) {
        alert('댓글을 입력하세요!');
        return;
    }

    try {
        const response = await fetchWithoutAuth(`/post/${projectId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: commentContent })
        });

        if (response.ok) {
            // 입력창 초기화
            commentInput.value = '';
            
            // 댓글 목록 새로고침
            loadProjectDetails();
            
            // 댓글 수 업데이트
            const commentNum = document.getElementById('commentNum');
            commentNum.textContent = parseInt(commentNum.textContent) + 1;
        } else {
            const errorData = await response.json();
            alert(errorData.message || '댓글 등록에 실패했습니다.');
        }
    } catch (error) {
        console.error('댓글 등록 중 오류 발생:', error);
        alert('댓글 등록 중 오류가 발생했습니다.');
    }
}

// 프로젝트 삭제 함수
async function deleteProject() {
    const projectId = "YOUR_PROJECT_ID"; // 실제 프로젝트 ID가 동적으로 설정되어야 함
    try {
        const response = await fetch(`http://18.118.128.174:8080/post/${projectId}`, {
            method: 'DELETE',
            headers: {
                'mode': 'cors'
            }
        });

        if (response.ok) {
            alert('프로젝트가 성공적으로 삭제되었습니다.');
            window.location.href = '/projectList.html'; // 메인 페이지로 리다이렉트
        } else {
            const errorData = await response.json();
            alert(errorData.message);
        }
    } catch (error) {
        console.error('프로젝트 삭제 중 오류 발생:', error);
    }
}

// 댓글 삭제 함수
async function deleteComment(commentId) {
    const projectId = "YOUR_PROJECT_ID"; // 실제 프로젝트 ID가 동적으로 설정되어야 함
    try {
        const response = await fetch(`http://18.118.128.174:8080/post/${projectId}/comments/${commentId}`, {
            method: 'DELETE',
            headers: {
                'mode': 'cors'
            }
        });

        if (response.ok) {
            alert('댓글이 성공적으로 삭제되었습니다.');
            loadProjectDetails(); // 댓글 목록 갱신
        } else {
            const errorData = await response.json();
            alert(errorData.message);
        }
    } catch (error) {
        console.error('댓글 삭제 중 오류 발생:', error);
    }
}
// 좋아요 증가 기능
function increaseLike() {
    const likeCount = document.getElementById('likeNum');
    likeCount.textContent = parseInt(likeCount.textContent) + 1;
}

// 댓글 추가 및 갯수 증가 기능
// function submitComment() {
//     const commentInput = document.getElementById('commentInput');
//     const commentText = commentInput.value.trim();

//     if (commentText) {
//         const commentList = document.getElementById('commentsList');
//         const newComment = document.createElement('div');
//         newComment.className = 'comment';
//         newComment.innerHTML = `<p><strong>작성자:</strong> ${commentText}</p>`;
//         commentList.appendChild(newComment);

//         // 댓글 갯수 증가
//         const commentNum = document.getElementById('commentNum');
//         commentNum.textContent = parseInt(commentNum.textContent) + 1;

//         // 입력창 초기화
//         commentInput.value = '';
//     } else {
//         alert('댓글을 입력하세요!');
//     }
// }
// HTML 페이지가 로드될 때 프로젝트를 불러옴
document.addEventListener('DOMContentLoaded', loadProjectDetails);

