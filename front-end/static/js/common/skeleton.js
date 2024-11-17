class SkeletonUI {
    constructor() {
        this.templates = {
            // 프로젝트 카드용 스켈레톤
            projectCard: `
                <div class="skeleton-card">
                    <div class="skeleton skeleton-thumbnail"></div>
                    <div class="skeleton-content">
                        <div class="skeleton skeleton-title"></div>
                        <div class="skeleton skeleton-text" style="width: 60%"></div>
                        <div class="skeleton skeleton-text" style="width: 40%"></div>
                        <div class="skeleton skeleton-text" style="width: 30%"></div>
                    </div>
                </div>
            `,
            
            // 공지사항 리스트용 스켈레톤
            noticeItem: `
                <div class="skeleton-notice">
                    <div class="skeleton skeleton-title" style="width: 60%"></div>
                    <div class="skeleton skeleton-text" style="width: 20%"></div>
                    <div class="skeleton skeleton-text" style="width: 15%"></div>
                </div>
            `,
            
            // 상세 페이지용 스켈레톤
            detailContent: `
                <div class="skeleton-detail">
                    <div class="skeleton skeleton-title"></div>
                    <div class="skeleton skeleton-meta">
                        <div class="skeleton skeleton-text" style="width: 20%"></div>
                        <div class="skeleton skeleton-text" style="width: 15%"></div>
                    </div>
                    <div class="skeleton skeleton-content">
                        <div class="skeleton skeleton-text"></div>
                        <div class="skeleton skeleton-text"></div>
                        <div class="skeleton skeleton-text"></div>
                    </div>
                </div>
            `,
            
            projectDetail: `
                <div class="skeleton-detail-container">
                    <!-- 제목 영역 -->
                    <div class="skeleton skeleton-title" style="height: 2.5em; width: 70%; margin-bottom: 20px;"></div>
                    
                    <!-- 메타 정보 영역 -->
                    <div class="skeleton-meta-info" style="display: flex; gap: 20px; margin-bottom: 30px;">
                        <div class="skeleton skeleton-text" style="width: 150px;"></div>
                        <div class="skeleton skeleton-text" style="width: 150px;"></div>
                        <div class="skeleton skeleton-text" style="width: 150px;"></div>
                    </div>
                    
                    <!-- 상호작용 정보 (좋아요, 댓글 수 등) -->
                    <div class="skeleton-interaction" style="display: flex; gap: 15px; margin-bottom: 30px;">
                        <div class="skeleton skeleton-text" style="width: 80px;"></div>
                        <div class="skeleton skeleton-text" style="width: 80px;"></div>
                    </div>
                    
                    <!-- 프로젝트 내용 -->
                    <div class="skeleton-content" style="margin-bottom: 40px;">
                        <div class="skeleton skeleton-text" style="width: 100%; height: 1em; margin-bottom: 10px;"></div>
                        <div class="skeleton skeleton-text" style="width: 95%; height: 1em; margin-bottom: 10px;"></div>
                        <div class="skeleton skeleton-text" style="width: 90%; height: 1em; margin-bottom: 10px;"></div>
                        <div class="skeleton skeleton-text" style="width: 85%; height: 1em;"></div>
                    </div>
                    
                    <!-- 유튜브 영상 플레이스홀더 -->
                    <div class="skeleton skeleton-video" style="width: 100%; height: 400px; margin-bottom: 30px;"></div>
                    
                    <!-- 댓글 섹션 -->
                    <div class="skeleton-comments">
                        <div class="skeleton skeleton-title" style="width: 200px; margin-bottom: 20px;"></div>
                        <!-- 댓글 입력창 -->
                        <div class="skeleton skeleton-input" style="width: 100%; height: 100px; margin-bottom: 30px;"></div>
                        <!-- 댓글 리스트 -->
                        <div class="skeleton-comment-list">
                            ${Array(3).fill().map(() => `
                                <div class="skeleton-comment" style="margin-bottom: 20px;">
                                    <div class="skeleton skeleton-text" style="width: 120px; margin-bottom: 10px;"></div>
                                    <div class="skeleton skeleton-text" style="width: 90%; margin-bottom: 5px;"></div>
                                    <div class="skeleton skeleton-text" style="width: 40%;"></div>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                </div>
            `,
            
            commentList: `
                <div class="skeleton-comment">
                    <div class="skeleton-comment-header">
                        <div class="skeleton skeleton-text" style="width: 100px; height: 20px;"></div>
                        <div class="skeleton skeleton-text" style="width: 80px; height: 20px;"></div>
                    </div>
                    <div class="skeleton skeleton-text" style="width: 90%; height: 40px; margin-top: 10px;"></div>
                    <div class="skeleton-comment-actions" style="display: flex; gap: 10px; margin-top: 10px;">
                        <div class="skeleton skeleton-text" style="width: 60px; height: 20px;"></div>
                        <div class="skeleton skeleton-text" style="width: 60px; height: 20px;"></div>
                    </div>
                </div>
            `,
            topProject: `
                <div class="thumbnail">
                    <div class="skeleton skeleton-thumbnail"></div>
                    <div class="skeleton skeleton-title"></div>
                    <div class="skeleton skeleton-text"></div>
                </div>
            `,
            userCard: `
                <div class="skeleton-user-card">
                    <div class="skeleton skeleton-text" style="width: 80%"></div>
                    <div class="skeleton skeleton-text" style="width: 70%"></div>
                    <div class="skeleton skeleton-text" style="width: 90%"></div>
                    <div class="skeleton skeleton-text" style="width: 60%"></div>
                    <div class="skeleton skeleton-button"></div>
                </div>
            `,
            // 프로젝트 관리 페이지용 스켈레톤 추가
            adminProjectCard: `
                <div class="skeleton-admin-project">
                    <div class="skeleton skeleton-title" style="width: 70%; height: 24px; margin-bottom: 15px;"></div>
                    <div class="skeleton skeleton-text" style="width: 40%; height: 16px; margin-bottom: 8px;"></div>
                    <div class="skeleton skeleton-text" style="width: 30%; height: 16px; margin-bottom: 8px;"></div>
                    <div class="skeleton skeleton-text" style="width: 35%; height: 16px; margin-bottom: 15px;"></div>
                    <div class="skeleton-actions">
                        <div class="skeleton skeleton-button" style="width: 60px; height: 32px; margin-right: 8px;"></div>
                        <div class="skeleton skeleton-button" style="width: 60px; height: 32px;"></div>
                    </div>
                </div>
            `,
            // 공지사항 관리 페이지용 스켈레톤 추가
            adminNoticeRow: `
                <tr class="skeleton-notice-row">
                    <td><div class="skeleton" style="width: 30px; height: 16px; margin: 0 auto;"></div></td>
                    <td><div class="skeleton" style="width: 80%; height: 16px; margin: 0 auto;"></div></td>
                    <td><div class="skeleton" style="width: 80px; height: 16px; margin: 0 auto;"></div></td>
                    <td>
                        <div style="display: flex; gap: 8px; justify-content: center;">
                            <div class="skeleton" style="width: 40px; height: 24px;"></div>
                            <div class="skeleton" style="width: 40px; height: 24px;"></div>
                        </div>
                    </td>
                </tr>
            `
        };
    }

    // 스켈레톤 UI 표시
    show(container, type, count = 1) {
        if (!this.templates[type]) {
            console.error(`Unknown skeleton type: ${type}`);
            return;
        }

        const targetContainer = 
            (typeof container === 'string') 
                ? document.querySelector(container) 
                : container;

        if (!targetContainer) {
            console.error('Target container not found');
            return;
        }

        // 기존 내용 비우기
        targetContainer.innerHTML = '';
        
        // 지정된 개수만큼 스켈레톤 추가
        for (let i = 0; i < count; i++) {
            targetContainer.innerHTML += this.templates[type];
        }
    }

    // 스켈레톤 UI 제거
    hide(container) {
        const targetContainer = 
            (typeof container === 'string') 
                ? document.querySelector(container) 
                : container;

        if (!targetContainer) {
            console.error('Target container not found');
            return;
        }

        // 모든 스켈레톤 요소 제거
        const skeletons = targetContainer.querySelectorAll('[class*="skeleton-"]');
        skeletons.forEach(skeleton => skeleton.remove());
    }

    // 커스텀 스켈레톤 템플릿 추가
    addTemplate(name, template) {
        this.templates[name] = template;
    }
}

// 전역에서 사용할 수 있도록 인스턴스 생성
const skeletonUI = new SkeletonUI();