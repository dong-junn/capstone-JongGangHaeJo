package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.dto.request.PostUpdateDto;
import jeiu.capstone.jongGangHaejo.dto.response.PagedResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostResponseDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

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


    @Transactional
    public void updatePost(Long postId, PostUpdateDto postUpdateDto, List<MultipartFile> files) {
        // 게시물 조회
        Post exPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, CommonErrorCode.RESOURCE_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!exPost.getUsername().equals(currentUsername)) {
            throw new UnauthorizedException("게시물을 수정할 권한이 없습니다.", CommonErrorCode.UNAUTHORIZED_ERROR);
        }

        // 게시물 필드 업데이트
        exPost.setTitle(postUpdateDto.getTitle());
        exPost.setContent(postUpdateDto.getContent());
        exPost.setTeam(postUpdateDto.getTeam());
        exPost.setYoutubelink(postUpdateDto.getYoutubelink());

        // 파일 관리
        if (files != null && !files.isEmpty()) {
            // 기존 파일 삭제 (필요 시)
            List<Long> exFileIds = exPost.getFileIds();
            fileService.deleteFiles(exFileIds);

            // 새로운 파일 업로드
            List<Long> newFileIds = fileService.uploadFiles(files);
            exPost.setFileIds(newFileIds);
        }

        // 게시물 저장
        postRepository.save(exPost);
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

    /**
     * 페이징된 게시물 목록을 조회합니다.
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 게시물 응답 DTO
     */
    @Transactional(readOnly = true)
    public PagedResponseDto<PostResponseDto> getPagedPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);

        List<PostResponseDto> content = postPage.getContent().stream()
                .map(post -> new PostResponseDto(
                        post.getPostid(),
                        post.getTitle(),
                        post.getContent(),
                        post.getTeam(),
                        post.getYoutubelink(),
                        post.getUsername(),
                        post.getCreatedAt().toString(),
                        post.getUpdatedAt().toString(),
                        post.getViewCount()
                ))
                .collect(Collectors.toList());

        return new PagedResponseDto<>(
                content,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );
    }


    @Transactional
    public void deletePost(Long postId) {
        Post Post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, CommonErrorCode.RESOURCE_NOT_FOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!Post.getUsername().equals(currentUsername)) {
            throw new UnauthorizedException("게시물을 삭제할 권한이 없습니다.", CommonErrorCode.UNAUTHORIZED_ERROR);
        }

        //파일 삭제
        if (Post.getFileIds() != null && !Post.getFileIds().isEmpty()) {
            fileService.deleteFiles(Post.getFileIds());
        }

        //게시물 삭제
        postRepository.delete(Post);

    }

    public List<PostResponseDto> getTop3Posts() {
        // 최신 게시물 3개를 조회
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAll(pageable).stream()
                .map(post -> new PostResponseDto(
                        post.getPostid(),
                        post.getTitle(),
                        post.getContent(),
                        post.getTeam(),
                        post.getYoutubelink(),
                        post.getUsername(),
                        post.getCreatedAt().toString(),
                        post.getUpdatedAt().toString(),
                        post.getViewCount() // 조회수 포함
                ))
                .collect(Collectors.toList());
    }
}
