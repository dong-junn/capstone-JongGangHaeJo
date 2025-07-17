package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.dataAccess.PostDataAccess;
import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.dto.request.PostUpdateDto;
import jeiu.capstone.jongGangHaejo.dto.response.PagedResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostResponseDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.LikeRepository;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import jeiu.capstone.jongGangHaejo.security.auth.AuthenticationCheck;
import jeiu.capstone.jongGangHaejo.validation.YoutubeUrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final FileService fileService;
    private final PostDataAccess postDataAccess;
    private final AuthenticationCheck getLoginUser;

    /**
     * 게시물을 생성하고 파일을 업로드한 후, 게시물과 파일을 저장합니다.
     *
     * @param postCreateDto 게시물 생성 DTO
     * @param files         업로드할 파일 목록
     */

    public void createPost(PostCreateDto postCreateDto, List<MultipartFile> files, MultipartFile thumbnail) {
        List<Long> fileIds = new ArrayList<>();
        
        // 디버깅을 위한 로깅 추가 (null 체크 포함)
        loggingFileAndThumbnail(files, thumbnail);

        // 썸네일 파일 먼저 업로드
        uploadThumbNailFile(thumbnail, fileIds);

        // 일반 파일 업로드 (썸네일과 중복된 파일 제외)
        generalFileUpload(files, thumbnail, fileIds);

        // YouTube URL 변환 (null 체크)
        yooutubeUrlConvert(postCreateDto);

        // 아래 두줄의 책임이 굳이 PostService에 있어야 할까???
        Post post = postCreateDto.toEntity(); // DTO에서 엔티티로 변환
        post.setFileIds(fileIds); // 게시물에 파일 ID 목록 설정

        // 게시물 저장
        postDataAccess.save(post); // postRepositorySavor를 통해 createPost에 걸린 @Transactional을 삭제 할 수 있게 되었다
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateDto postUpdateDto, List<MultipartFile> files, MultipartFile thumbnail) {
        Post exPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, CommonErrorCode.RESOURCE_NOT_FOUND));

        // 권한 검사
        String currentUsername = getLoginUser.getAuthentication();

        if (!exPost.getUsername().equals(currentUsername)) {
            throw new UnauthorizedException("게시물을 수정할 권한이 없습니다.", CommonErrorCode.UNAUTHORIZED_ERROR);
        }

        // 게시물 필드 업데이트
        exPost.update(postUpdateDto);

        List<Long> newFileIds = new ArrayList<>();

        // 새로운 썸네일이 있는 경우
        ifHasNewThumbnail(thumbnail, newFileIds);

        // 새로운 일반 파일이 있는 경우
        ifHasNewFile(files, newFileIds);

        // 새로운 파일이 있는 경우에만 기존 파일 삭제 및 교체
        ifHasNewFileDeleteOriginalFile(newFileIds, exPost);

        postDataAccess.save(exPost);
    }

    private void ifHasNewFileDeleteOriginalFile(List<Long> newFileIds, Post exPost) {
        if (!newFileIds.isEmpty()) {
            // 기존 파일이 있다면 삭제
            if (exPost.getFileIds() != null && !exPost.getFileIds().isEmpty()) {
                log.info("기존 파일 삭제");
                fileService.deleteFiles(exPost.getFileIds());
            }
            exPost.setFileIds(newFileIds);
        }
    }

    private void ifHasNewFile(List<MultipartFile> files, List<Long> newFileIds) {
        if (files != null && !files.isEmpty()) {
            log.info("새로운 일반 파일 업로드 시작");
            List<Long> regularFileIds = fileService.uploadFiles(files, "posts", false);
            newFileIds.addAll(regularFileIds);
            log.info("새로운 일반 파일 업로드 완료");
        }
    }

    private void ifHasNewThumbnail(MultipartFile thumbnail, List<Long> newFileIds) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            log.info("새로운 썸네일 업로드 시작");
            List<Long> thumbnailId = fileService.uploadFiles(
                    Collections.singletonList(thumbnail),
                    "posts/thumbnails",
                    true
            );
            newFileIds.addAll(thumbnailId);
            log.info("새로운 썸네일 업로드 완료");
        }
    }

    private static void yooutubeUrlConvert(PostCreateDto postCreateDto) {
        String youtubeUrl = postCreateDto.getYoutubelink();
        if (youtubeUrl != null && !youtubeUrl.trim().isEmpty()) {
            String embedUrl = YoutubeUrlValidator.convertToEmbedUrl(youtubeUrl);
            postCreateDto.setYoutubelink(embedUrl);
        }
    }

    private void generalFileUpload(List<MultipartFile> files, MultipartFile thumbnail, List<Long> fileIds) {
        if (files != null && !files.isEmpty()) {
            String thumbnailFilename = thumbnail != null ? thumbnail.getOriginalFilename() : null;

            // 중복 제거를 위해 Set 사용
            Set<String> processedFilenames = new HashSet<>();
            if (thumbnailFilename != null) {
                processedFilenames.add(thumbnailFilename);
            }

            List<MultipartFile> nonDuplicateFiles = files.stream()
                .filter(file -> {
                    String filename = file.getOriginalFilename();
                    if (filename != null && !processedFilenames.contains(filename)) {
                        processedFilenames.add(filename);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

            if (!nonDuplicateFiles.isEmpty()) {
                List<Long> regularFileIds = fileService.uploadFiles(nonDuplicateFiles, "posts", false);
                fileIds.addAll(regularFileIds);
                log.info("일반 파일 업로드 완료. 업로드된 파일 수: {}", nonDuplicateFiles.size());
            }
        }
    }

    @Transactional
    public PostResponseDto getSinglePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + id, CommonErrorCode.RESOURCE_NOT_FOUND));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 조회수 증가
        postRepository.incrementViewCount(id);

        // 파일 정보 조회 및 변환
        List<PostResponseDto.FileInfo> fileInfos = new ArrayList<>();
        if (post.getFileIds() != null && !post.getFileIds().isEmpty()) {
            List<File> files = fileService.getFilesByIds(post.getFileIds());
            fileInfos = files.stream()
                    .map(file -> new PostResponseDto.FileInfo(
                            file.getFileName(),
                            file.getS3Path(),
                            file.getThumbnailPath()  // null이면 null 반환
                    ))
                    .collect(Collectors.toList());
        }

        boolean isLiked = likeRepository.existsByPostIdAndUsername(id, currentUsername);

        // PostResponseDto로 변환하면서 좋아요 수 포함
        return new PostResponseDto(
                post.getPostid(),
                post.getTitle(),
                post.getContent(),
                post.getTeam(),
                post.getYoutubelink(),
                post.getUsername(),
                post.getCreatedAt().toString(),
                post.getUpdatedAt().toString(),
                post.getViewCount(),
                likeRepository.countByPostId(post.getPostid()), // 좋아요 수 조회
                isLiked,
                fileInfos
        );
    }

    private void uploadThumbNailFile(MultipartFile thumbnail, List<Long> fileIds) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            List<Long> thumbnailId = fileService.uploadFiles(
                Collections.singletonList(thumbnail),
                "posts/thumbnails",
                true
            );
            fileIds.addAll(thumbnailId);
            log.info("썸네일 업로드 완료: {}", thumbnail.getOriginalFilename());
        }
    }

    private void loggingFileAndThumbnail(List<MultipartFile> files, MultipartFile thumbnail) {
        log.info("전체 파일 목록:");
        if (files != null && !files.isEmpty()) {
            files.forEach(file -> log.info("파일명: {}", file.getOriginalFilename()));
        }

        if (thumbnail != null) {
            log.info("썸네일 파일명: {}", thumbnail.getOriginalFilename());
        }
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
                .map(post -> {
                    // 파일 정보 조회 및 변환
                    List<PostResponseDto.FileInfo> fileInfos = new ArrayList<>();
                    if (post.getFileIds() != null && !post.getFileIds().isEmpty()) {
                        List<File> files = fileService.getFilesByIds(post.getFileIds());
                        fileInfos = files.stream()
                                .map(file -> new PostResponseDto.FileInfo(
                                        file.getFileName(),
                                        file.getS3Path(),
                                        file.getThumbnailPath()  // 썸네일 URL 포함
                                ))
                                .collect(Collectors.toList());
                    }

                    return new PostResponseDto(
                            post.getPostid(),
                            post.getTitle(),
                            post.getContent(),
                            post.getTeam(),
                            post.getYoutubelink(),
                            post.getUsername(),
                            post.getCreatedAt().toString(),
                            post.getUpdatedAt().toString(),
                            post.getViewCount(),
                            likeRepository.countByPostId(post.getPostid()),
                            false,  // isLiked는 목록에서는 false로 기본 설정
                            fileInfos  // 파일 정보 포함
                    );
                })
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
        // 조회수 순 인기 게시물 3개를 조회
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "ViewCount"));
        
        return postRepository.findAll(pageable).stream()
                .map(post -> {
                    // 파일 정보 조회 및 변환
                    List<PostResponseDto.FileInfo> fileInfos = new ArrayList<>();
                    if (post.getFileIds() != null && !post.getFileIds().isEmpty()) {
                        List<File> files = fileService.getFilesByIds(post.getFileIds());
                        fileInfos = files.stream()
                                .map(file -> new PostResponseDto.FileInfo(
                                        file.getFileName(),
                                        file.getS3Path(),
                                        file.getThumbnailPath()  // 썸네일 URL 포함
                                ))
                                .collect(Collectors.toList());
                    }

                    return new PostResponseDto(
                            post.getPostid(),
                            post.getTitle(),
                            post.getContent(),
                            post.getTeam(),
                            post.getYoutubelink(),
                            post.getUsername(),
                            post.getCreatedAt().toString(),
                            post.getUpdatedAt().toString(),
                            post.getViewCount(),
                            likeRepository.countByPostId(post.getPostid()),
                            false,  // isLiked는 목록에서는 false로 기본 설정
                            fileInfos  // 파일 정보 포함
                    );
                })
                .collect(Collectors.toList());
    }
}
