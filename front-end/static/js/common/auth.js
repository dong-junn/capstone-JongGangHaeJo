// JWT 토큰에서 사용자 정보 추출
function decodeToken() {
    try {
        // localStorage에서 토큰 가져오기
        const token = localStorage.getItem('accessToken');
        if (!token) return null;
        
        // payload 부분만 추출해서 디코딩
        const base64Payload = token.split('.')[1];
        const payload = JSON.parse(atob(base64Payload));
        
        return payload;
    } catch (error) {
        console.error('Token decode failed:', error);
        return null;
    }
}

// 현재 사용자 ID 가져오기
function getCurrentUserId() {
    const payload = decodeToken();
    return payload ? payload.username : null;
}

// 사용자 권한 확인
function isAuthor(authorId) {
    const currentUserId = getCurrentUserId();
    return currentUserId === authorId;
} 