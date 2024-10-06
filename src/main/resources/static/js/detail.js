document.addEventListener('DOMContentLoaded', function() {
    includeHTML();

    const likeIcon = document.getElementById('likeIcon');
    const likeCount = document.getElementById('likeCount');
    const likeNum = document.getElementById('likeNum');
    let liked = false;
    let likes = parseInt(likeNum.innerText);
    const isMember = true;  // 회원 여부를 확인하는 로직으로 교체해야 합니다.
    const memberId = 'user123';  // 사용자 ID, 실제 애플리케이션에서는 서버에서 받아와야 합니다.

    likeIcon.addEventListener('click', function() {
        if (!isMember) {
            alert('좋아요는 회원만 가능합니다.');
            return;
        }

        liked = !liked;
        if (liked) {
            likeIcon.classList.remove('far');
            likeIcon.classList.add('fas');
            likes++;
        } else {
            likeIcon.classList.remove('fas');
            likeIcon.classList.add('far');
            likes--;
        }
        likeNum.innerText = likes;
    });

    const commentInput = document.getElementById('commentInput');
    const submitComment = document.getElementById('submitComment');
    const commentsList = document.getElementById('commentsList');
    const commentNum = document.getElementById('commentNum');
    let comments = parseInt(commentNum.innerText);

    submitComment.addEventListener('click', function() {
        if (!isMember) {
            alert('댓글은 회원만 작성할 수 있습니다.');
            return;
        }

        const commentText = commentInput.value;
        if (commentText.trim() !== '') {
            const newComment = document.createElement('div');
            newComment.classList.add('comment');

            const commentDate = new Date().toLocaleString();
            newComment.innerHTML = `<strong>${memberId}</strong> (${commentDate}): ${commentText}`;

            commentsList.appendChild(newComment);
            commentInput.value = '';
            comments++;
            commentNum.innerText = comments;
        }
    });
});
