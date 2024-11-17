package jeiu.capstone.jongGangHaejo.service;

import jakarta.persistence.EntityNotFoundException;
import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.dto.notice.NoticeResponse;
import jeiu.capstone.jongGangHaejo.dto.request.notice.NoticeCreateDto;
import jeiu.capstone.jongGangHaejo.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileService fileService;

    @Transactional
    public void save(NoticeCreateDto dto) {
        Notice entity = dto.toEntity();
        noticeRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findAllWithPaging(pageable);
    }

    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));
        
        List<File> files = fileService.getFilesByIds(notice.getFileIds());
        return NoticeResponse.from(notice, files);
    }

    @Transactional
    public void noticeUpdate(Long currentId, NoticeCreateDto dto) {
        Notice notice = noticeRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("수정하려는 공지글을 찾을 수 없습니다. ID: " + currentId));
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        noticeRepository.save(notice);
    }

    @Transactional
    public NoticeResponse createNotice(NoticeCreateDto dto, MultipartFile file, String username) {
        // Notice 엔티티 생성
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .creator(username)
                .build();

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            List<Long> fileIds = fileService.uploadFiles(List.of(file), "notice");
            notice.setFileIds(fileIds);
        }

        // 저장
        Notice savedNotice = noticeRepository.save(notice);
        
        // 파일 정보 조회
        List<File> files = fileService.getFilesByIds(savedNotice.getFileIds());
        
        // DTO 변환 후 반환
        return NoticeResponse.from(savedNotice, files);
    }
}
