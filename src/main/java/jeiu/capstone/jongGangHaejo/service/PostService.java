package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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
        // 게시물 엔티티 생성
        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .team(postCreateDto.getTeam())
                .youtubelink(postCreateDto.getYoutubelink())
                .build();

        // 파일 처리 및 연관 설정
        if (files != null && !files.isEmpty()) {
            List<Long> fileIds = files.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String fileUrl = fileService.uploadFile(file); // S3에 파일 업로드 후 URL 획득

                        File fileEntity = File.builder()
                                .fileName(file.getOriginalFilename())
                                .s3Path(fileUrl)
                                .fileType(file.getContentType())
                                .fileSize(file.getSize())
                                .build();

                        // 파일을 DB에 저장하고 파일 ID 반환
                        File savedFile = fileService.saveFile(fileEntity);
                        return savedFile != null ? savedFile.getFileId() : null;
                    })
                    .filter(fileId -> fileId != null) // Null ID는 제외
                    .collect(Collectors.toList());

            // 게시물에 파일 ID 목록 설정
            post.setFileIds(fileIds);
        }

        // 게시물 저장 (연관된 파일들도 함께 저장됨)
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

    public Post getSinglePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글 입니다"));
        return post;
    }
}
