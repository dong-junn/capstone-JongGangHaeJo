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

    const projectDetailSection = document.querySelector('.project-detail');
    
    // 원본 HTML 구조 저장
    const originalHTML = projectDetailSection.innerHTML;

    skeletonUI.show('.project-detail', 'projectDetail');

    try {
        const response = await fetchWithoutAuth(`/post/${projectId}`, {
            method: 'GET'
        });

        if (response.ok) {
            const project = await response.json();
            console.log('Project data:', project); // 디버깅용

            // 스켈레톤 UI 제거하고 원본 HTML 구조 복원
            projectDetailSection.innerHTML = originalHTML;

            // 프로젝트 세부 정보를 DOM에 업데이트
            const titleElement = document.querySelector('.project-detail h1');
            if (titleElement) {
                titleElement.innerText = project.title;
            }

            // 작성자 권한 확인 및 버튼 표시/숨김 처리
            const actionButtons = document.querySelector('.project-actions-buttons');
            if (actionButtons) {
                const currentUsername = getCurrentUsername();
                console.log('Current username from token:', currentUsername); // 디버깅용
                console.log('Project username:', project.username); // 디버깅용
                
                if (currentUsername === project.username) {
                    console.log('User is author, showing buttons'); // 디버깅용
                    actionButtons.innerHTML = `
                        <button class="edit-button" onclick="location.href='/front-end/templates/board/project/projectModify.html?id=${projectId}'">수정</button>
                        <button class="delete-button" onclick="deleteProject()">삭제</button>
                    `;
                    actionButtons.style.display = 'flex';
                } else {
                    console.log('User is not author, hiding buttons'); // 디버깅용
                    actionButtons.style.display = 'none';
                }
            }

            // 메타 정보 업데이트
            const metaInfo = document.querySelector('.project-meta');
            if (metaInfo) {
                metaInfo.innerHTML = `
                    <span><i class="fas fa-users"></i> 팀명: ${project.team || '팀 이름'}</span>
                    <span><i class="fas fa-user"></i> 작성자: ${project.username}</span>
                    <span><i class="fas fa-calendar"></i> 등록일: ${new Date(project.createdAt).toLocaleDateString('ko-KR')}</span>
                    <span><i class="fas fa-eye"></i> 조회수: ${project.viewCount}</span>
                `;
            }

            // 포스터 이미지 업데이트
            const posterContainer = document.querySelector('.project-poster');
            if (posterContainer) {
                // files 배열에서 thumbnailUrl이 있는 파일 찾기
                const thumbnailFile = project.files?.find(file => file.thumbnailUrl);
                
                if (thumbnailFile?.thumbnailUrl) {
                    posterContainer.innerHTML = `
                        <h2>발표 자료</h2>
                        <img src="${thumbnailFile.thumbnailUrl}" 
                             alt="${project.title || 'Project Thumbnail'}" 
                             id="projectPoster">
                    `;
                } else {
                    posterContainer.innerHTML = `
                        <h2>발표 자료</h2>
                        <div class="no-poster">
                            <span>등록한 이미지가 없습니다.</span>
                        </div>
                    `;
                }
            }

            // 작품 개요 업데이트
            const contentElement = document.querySelector('.project-extra-info p');
            if (contentElement) {
                contentElement.innerText = project.content;
            }

            // 유튜브 동영상 업데이트
            const videoContainer = document.querySelector('.project-youtube-video');
            const videoElement = document.getElementById('youtubeVideo');
            if (videoContainer && videoElement) {
                if (project.youtubelink) {
                    videoElement.src = project.youtubelink;
                    videoContainer.style.display = 'block'; // 또는 'flex'
                } else {
                    videoContainer.style.display = 'none';
                }
            }

            // 좋아요 수와 댓글 수 업데이트
            const likeElement = document.getElementById('likeNum');
            const commentElement = document.getElementById('commentNum');
            
            if (likeElement) likeElement.innerText = project.likeCount || 0;
            if (commentElement) commentElement.innerText = project.comments?.length || 0;

            // 좋아요 상태 초기화
            const likeButton = document.querySelector('.like-button');
            if (likeButton && project.liked) {
                likeButton.classList.add('liked');
            }

            // 댓글 목록 로드
            await loadComments();

            // 첨부파일 목록 업데이트
            const attachmentsList = document.getElementById('attachmentsList');
            if (project.files && project.files.length > 0) {
                attachmentsList.innerHTML = project.files.map(file => `
                    <div class="attachment-item">
                        <i class="fas fa-file attachment-icon"></i>
                        <div class="attachment-info">
                            <a href="${file.downloadUrl}" 
                               class="attachment-name" 
                               download="${file.fileName}"
                               rel="noopener noreferrer">
                                ${file.fileName}
                            </a>
                        </div>
                    </div>
                `).join('');
            } else {
                attachmentsList.innerHTML = '<div class="no-attachments">첨부된 파일이 없습니다.</div>';
            }
        } else {
            skeletonUI.hide('.project-detail');
            const errorData = await response.json();
            alert(errorData.message || '게시물을 불러오는데 실패했습니다.');
        }
    } catch (error) {
        if (error.name === 'AbortError') {
            return;
        }
        skeletonUI.hide('.project-detail');
        console.error('프로젝트 정보를 불러오는 중 오류 발생:', error);
        alert('프로젝트 정보를 불러오는데 실패했습니다.');
    }
}
// 댓글 목록을 로드하는 함수
async function loadComments() {
    const commentsList = document.getElementById('commentsList');
    
    // 댓글 스켈레톤 UI 표시
    skeletonUI.show('#commentsList', 'commentList', 3);
    try {
        const response = await fetchWithoutAuth(`/post/${projectId}/comments`, {
            method: 'GET'
        });

        if (response.ok) {
            const comments = await response.json();

            skeletonUI.hide('#commentsList');
            const commentsList = document.getElementById('commentsList');
            commentsList.innerHTML = ''; // 기존 댓글 초기화

            // 댓글 트리 구조로 변환
            const commentTree = buildCommentTree(comments);

            // 댓글을 작성 시간순으로 정렬 (오래된 순)
            const sortedComments = commentTree.sort((a, b) => 
                new Date(a.createdAt) - new Date(b.createdAt)
            );

            // 각 댓글을 화면에 추가
            sortedComments.forEach(comment => {
                const commentElement = createCommentElement(comment);
                commentsList.appendChild(commentElement);
            });

            // 댓글 수 업데이트
            const commentNum = document.getElementById('commentNum');
            if (commentNum) {
                commentNum.textContent = comments.length;
            }
        }
    } catch (error) {
        skeletonUI.hide('#commentsList');
        console.error('댓글 목록을 불러오는 중 오류 발생:', error);
    }
}

// 댓글을 트리 구조로 변환하는 함수
function buildCommentTree(comments) {
    const commentMap = new Map();
    const rootComments = [];

    // 모든 댓글을 Map에 저장
    comments.forEach(comment => {
        comment.replies = [];
        commentMap.set(comment.id, comment);
    });

    // 댓글을 트리 구조로 구성
    comments.forEach(comment => {
        if (comment.parentCommentId) {
            const parentComment = commentMap.get(comment.parentCommentId);
            if (parentComment) {
                parentComment.replies.push(comment);
            }
        } else {
            rootComments.push(comment);
        }
    });

    // 루트 댓글과 각 답글들을 시간순으로 정렬
    rootComments.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    
    // 각 댓글의 답글들도 시간순 정렬
    rootComments.forEach(comment => {
        if (comment.replies.length > 0) {
            comment.replies.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
        }
    });

    return rootComments;
}

// 댓글 요소를 생성하는 함수
function createCommentElement(comment, depth = 0) {
    const commentElement = document.createElement('div');
    commentElement.className = depth === 0 ? 'comment' : 'reply';
    commentElement.dataset.commentId = comment.id;
    commentElement.dataset.depth = depth;
    commentElement.style.position = 'relative';

    // 현재 로그인한 사용자의 username 가져오기
    const currentUsername = getCurrentUsername();
    
    // 디버깅을 위한 로그
    console.log('Creating comment element:');
    console.log('Comment:', comment);
    console.log('Current username from token:', currentUsername);
    console.log('Comment username:', comment.username);

    // 삭제 권한 확인 (작성자 본인인 경우)
    const canDelete = currentUsername === comment.username;
    
    console.log('Can delete:', canDelete);

    const commentDate = new Date(comment.createdAt).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });

    if (comment.deleted) {
        commentElement.innerHTML = `
            <div class="comment-header deleted">
                <span class="comment-author">삭제된 댓글입니다</span>
                <span class="comment-date">${commentDate}</span>
            </div>
            <p class="comment-content deleted">삭제된 댓글입니다.</p>
        `;
    } else {
        const showReplyButton = depth < 2;

        commentElement.innerHTML = `
            <div class="comment-header">
                <strong class="comment-author">${comment.username || '작성자'}</strong>
                <span class="comment-date">${commentDate}</span>
            </div>
            <p class="comment-content">${comment.content}</p>
            <div class="comment-actions">
                ${showReplyButton ? `<button class="reply-button" onclick="showReplyForm(this)">답글</button>` : ''}
                ${canDelete ? `<button class="delete-comment" onclick="deleteComment('${comment.id}')">삭제</button>` : ''}
            </div>
            ${showReplyButton ? `
            <div class="reply-form hidden" data-parent-id="${comment.id}">
                <textarea class="reply-input" placeholder="답글을 입력하세요"></textarea>
                <button class="submit-reply" onclick="submitReply(this)">답글 달기</button>
            </div>
            ` : ''}
        `;
    }

    // 답글이 있는 경우 처리
    if (comment.replies && comment.replies.length > 0) {
        const repliesContainer = document.createElement('div');
        repliesContainer.className = 'replies';
        
        comment.replies.forEach(reply => {
            repliesContainer.appendChild(createCommentElement(reply, depth + 1));
        });
        
        commentElement.appendChild(repliesContainer);
    }

    return commentElement;
}

// 답글 제출 함수
async function submitReply(button) {
    const replyForm = button.closest('.reply-form');
    const replyInput = replyForm.querySelector('.reply-input');
    const replyContent = replyInput.value.trim();
    const parentCommentId = replyForm.dataset.parentId;

    if (!replyContent) {
        alert('답글을 입력하세요!');
        return;
    }

    try {
        const response = await fetchWithAuth(`/post/${projectId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content: replyContent,
                parentCommentId: Number(parentCommentId)
            })
        });

        if (response.ok) {
            replyInput.value = '';
            replyForm.classList.add('hidden');
            await loadComments();
        } else {
            const errorData = await response.json();
            alert(errorData.message || '답글 등록에 실패했습니다.');
        }
    } catch (error) {
        console.error('답글 등록 중 오류 발생:', error);
        alert('답글 등록 중 오류가 발생했습니다.');
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
        const response = await fetchWithAuth(`/post/${projectId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: commentContent })
        });

        if (response.ok) {
            const newComment = await response.json();
            
            // 입력창 초기화
            commentInput.value = '';
            
            // 새 댓글을 목록의 맨 아래에 추가
            const commentsList = document.getElementById('commentsList');
            const commentElement = document.createElement('div');
            commentElement.className = 'comment';
            commentElement.dataset.commentId = newComment.id;
            
            const commentDate = new Date(newComment.createdAt).toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            }).replace(/\./g, '.');

            commentElement.innerHTML = `
                <div class="comment-header">
                    <strong class="comment-author">${newComment.username || '작성자'}</strong>
                    <span class="comment-date">${commentDate}</span>
                </div>
                <p class="comment-content">${newComment.content}</p>
                <div class="comment-actions">
                    <button class="reply-button" onclick="showReplyForm(this)">답글</button>
                    <button class="delete-comment" onclick="deleteComment('${newComment.id}')">삭제</button>
                </div>
                
                <!-- 답글 입력 폼 -->
                <div class="reply-form hidden" data-parent-id="${newComment.id}">
                    <textarea class="reply-input" placeholder="답글을 입력하세요"></textarea>
                    <button class="submit-reply" onclick="submitReply(this)">답글 달기</button>
                </div>
                
                <!-- 답글 목록 -->
                <div class="replies"></div>
            `;

            // 새 댓글을 목록의 맨 아래에 추가
            commentsList.appendChild(commentElement);
            
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
    // 삭제 확인
    if (!confirm('프로젝트를 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetchWithAuth(`/post/${projectId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('프로젝트가 성공적으로 삭제되었습니다.');
            window.location.href = '/front-end/templates/board/project/projectList.html';
        } else {
            const errorData = await response.json();
            alert(errorData.message || '프로젝트 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('프로젝트 삭제 중 오류 발생:', error);
        alert('프로젝트 삭제 중 오류가 발생했습니다.');
    }
}

// 댓글 삭제 함수
async function deleteComment(commentId) {
    // 삭제 확인
    if (!confirm('댓글을 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetchWithAuth(`/post/${projectId}/comments/${commentId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('댓글이 삭제되었습니다.');
            // 댓글 목록 새로고침
            await loadComments();
        } else {
            const errorData = await response.json();
            alert(errorData.message || '댓글 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('댓글 삭제 중 오류 발생:', error);
        alert('댓글 삭제 중 오류가 발생했습니다.');
    }
}
// 좋아요 토글 함수
async function toggleLike() {
    const likeButton = document.querySelector('.like-button');
    const likeElement = document.getElementById('likeNum');
    
    if (!likeButton || !likeElement) {
        console.error('좋아요 버튼 또는 카운터를 찾을 수 없습니다.');
        return;
    }

    try {
        const response = await fetchWithAuth(`/posts/${projectId}/like`, {
            method: 'PUT'
        });

        if (response.ok) {
            const currentLikes = parseInt(likeElement.textContent);
            
            if (likeButton.classList.contains('liked')) {
                likeElement.textContent = currentLikes - 1;
                likeButton.classList.remove('liked');
            } else {
                likeElement.textContent = currentLikes + 1;
                likeButton.classList.add('liked');
            }
        } else {
            const errorData = await response.json();
            alert(errorData.message || '좋아요 처리에 실패했습니다.');
        }
    } catch (error) {
        console.error('좋아요 처리 중 오류 발생:', error);
        alert('좋아요 처리 중 오류가 발생했습니다.');
    }
}

// HTML 페이지가 로드될 때 프로젝트를 불러옴
document.addEventListener('DOMContentLoaded', loadProjectDetails);

// 답글 입력 폼을 보여주는 함수
function showReplyForm(button) {
    // 현재 댓글 답글 입력 폼
    const currentElement = button.closest('[data-comment-id]');
    const replyForm = currentElement.querySelector('.reply-form');
    
    // 다 든 답글 폼을 숨김
    document.querySelectorAll('.reply-form').forEach(form => {
        if (form !== replyForm) {
            form.classList.add('hidden');
        }
    });

    // 현재 답글 폼 토글
    replyForm.classList.toggle('hidden');
    
    // 답글 폼이 보면 입력창에 커스
    if (!replyForm.classList.contains('hidden')) {
        replyForm.querySelector('.reply-input').focus();
    }
}

// 현재 로그인한 사용자의 이름을 가져오는 함수
function getCurrentUsername() {
    try {
        const token = localStorage.getItem('accessToken');
        if (!token) return null;
        
        // Base64 디코딩을 위한 안전한 처리
        const base64Payload = token.split('.')[1];
        const base64 = base64Payload.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(
            decodeURIComponent(
                atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            )
        );
        
        console.log('Decoded token payload:', payload);
        return payload.username;
    } catch (error) {
        console.error('토큰 디코딩 중 오류 발생:', error);
        return null;
    }
}

// 작성자 권한 확인 함수 수정
function isAuthor(authorUsername) {
    const currentUsername = getCurrentUsername();
    console.log('isAuthor check:', {
        currentUsername,
        authorUsername,
        isMatch: currentUsername === authorUsername
    });
    return currentUsername === authorUsername;
}

