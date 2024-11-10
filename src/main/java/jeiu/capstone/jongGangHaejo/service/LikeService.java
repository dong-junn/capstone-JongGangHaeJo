package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Like;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.LikeRepository;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public void toggleLike(Long postId) {
        // 게시물 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 현재 로그인한 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 이미 좋아요를 눌렀는지 확인
        likeRepository.findByPostIdAndUsername(postId, username)
                .ifPresentOrElse(
                        like -> likeRepository.deleteByPostIdAndUsername(postId, username), // 이미 눌렀다면 삭제
                        () -> likeRepository.save(new Like(postId, username)) // 안 눌렀다면 저장
                );
    }
} 