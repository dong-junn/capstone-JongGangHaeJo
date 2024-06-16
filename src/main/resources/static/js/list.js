includeHTML();

        // 사용자 역할에 따라 페이지 기능 제어
        document.addEventListener("DOMContentLoaded", function() {
            const userRole = 'user'; // 'user' 또는 'admin'으로 설정

            if (userRole === 'admin') {
                // 관리자일 경우: 공지사항 등록 폼과 수정/삭제 버튼 활성화
                document.querySelector('.admin-controls').style.display = 'block';

                // 공지사항 수정 버튼 이벤트 리스너
                document.querySelectorAll('.edit-notice').forEach(button => {
                    button.addEventListener('click', function() {
                        alert('공지사항 수정 기능');
                    });
                });

                // 공지사항 삭제 버튼 이벤트 리스너
                document.querySelectorAll('.delete-notice').forEach(button => {
                    button.addEventListener('click', function() {
                        alert('공지사항 삭제 기능');
                    });
                });

                // 공지사항 등록 폼 제출 이벤트 리스너
                document.getElementById('noticeForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    alert('공지사항 등록 기능');
                });

            } else {
                // 일반 회원일 경우: 공지사항 등록 폼과 수정/삭제 버튼 숨김
                document.querySelector('.admin-controls').style.display = 'none';
            }
        });