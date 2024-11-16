package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.PageResponse;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserResponse;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserSearchCondition;
import jeiu.capstone.jongGangHaejo.dto.notice.NoticeResponse;
import jeiu.capstone.jongGangHaejo.dto.request.notice.NoticeCreateDto;
import jeiu.capstone.jongGangHaejo.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public ResponseEntity<PageResponse<NoticeResponse>> getNotices(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Notice> noticePage = noticeService.getNotices(pageable);
        Page<NoticeResponse> responsePage = noticePage.map(NoticeResponse::from);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    // 공지사항 단건 조회
    @GetMapping("/notice/{id}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
        NoticeResponse notice = noticeService.getNotice(id);
        return ResponseEntity.ok(notice);
    }

    @PostMapping("/admin/notice")
    public void CreateNotice(@RequestBody NoticeCreateDto dto) {
        noticeService.save(dto);
    }
}
