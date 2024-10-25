package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.response.PostDetailResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostListResponseDto;
import jeiu.capstone.jongGangHaejo.exception.PostNotFoundException;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void savePost(Post post) {
        postRepository.save(post);
    }

    // 전체 게시물 목록 조회 (페이징)
    public Page<PostListResponseDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostListResponseDto::from);
    }

    // 단일 게시물 상세 조회
    public PostDetailResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));
        return PostDetailResponseDto.from(post);
    }

    // 제목으로 게시물 검색
    public Page<PostListResponseDto> searchPostsByTitle(String keyword, Pageable pageable) {
        return postRepository.findByTitleContaining(keyword, pageable)
                .map(PostListResponseDto::from);
    }

}
