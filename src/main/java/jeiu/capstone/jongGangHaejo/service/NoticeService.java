package jeiu.capstone.jongGangHaejo.service;

import jakarta.persistence.EntityNotFoundException;
import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.dto.notice.NoticeResponse;
import jeiu.capstone.jongGangHaejo.dto.request.notice.NoticeCreateDto;
import jeiu.capstone.jongGangHaejo.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void save(NoticeCreateDto dto) {
        Notice entity = dto.toEntity();
        noticeRepository.save(entity);
    }

    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findAllWithPaging(pageable);
    }

    private NoticeResponse convertToDto(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .creator(notice.getCreator())
                .build();
    }

    public NoticeResponse getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));
        return convertToDto(notice);
    }

    @Transactional
    public void noticeUpdate(Long currentId, NoticeCreateDto dto) {
        Notice notice = noticeRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("수정하려는 공지글을 찾을 수 없습니다. ID: " + currentId));
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        noticeRepository.save(notice);
    }
}
