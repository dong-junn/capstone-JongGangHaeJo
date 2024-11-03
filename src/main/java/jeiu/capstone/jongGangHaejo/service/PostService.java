package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FileService fileService;

    /**
     * 게시물을 생성하고 파일을 업로드한 후, 게시물과 파일을 저장합니다.
     *
     * @param postCreateDto 게시물 생성 DTO
     * @param files         업로드할 파일 목록
     */
    @Transactional
    public void createPost(PostCreateDto postCreateDto, List<MultipartFile> files) {
        // 파일 업로드 및 파일 ID 목록 획득
        List<Long> fileIds = fileService.uploadFiles(files);

        // DTO에서 엔티티로 변환
        Post post = postCreateDto.toEntity();

        // 게시물에 파일 ID 목록 설정
        post.setFileIds(fileIds);

        // 게시물 저장
        postRepository.save(post);
    }


    /**
     * 기존의 단순 게시물 저장 메서드.
     * 필요 시 컨트롤러에서 직접 사용할 수 있습니다.
     *
     * @param post 저장할 게시물 엔티티
     */
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post getSinglePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + id, CommonErrorCode.RESOURCE_NOT_FOUND));

        // 조회수 증가
        postRepository.incrementViewCount(id);

        // 조회된 게시물 반환
        return post;
    }
}
