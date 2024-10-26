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
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileUrl = fileService.uploadFile(file); // S3에 파일 업로드 후 URL 획득

                File fileEntity = File.builder()
                        .fileName(file.getOriginalFilename())
                        .s3Path(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .build();

                post.addFile(fileEntity); // 게시물과 파일의 연관 설정
            }
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
}
