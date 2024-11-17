package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.dto.admin.user.PageResponse;
import jeiu.capstone.jongGangHaejo.dto.notice.NoticeResponse;
import jeiu.capstone.jongGangHaejo.dto.request.notice.NoticeCreateDto;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import jeiu.capstone.jongGangHaejo.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public ResponseEntity<PageResponse<NoticeResponse>> getNotices(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
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

    @PostMapping(value = "/admin/notice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestPart(value = "notice") NoticeCreateDto noticeCreateDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserConfig userConfig
    ) {
        NoticeResponse response = noticeService.createNotice(noticeCreateDto, file, userConfig.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/notice/{id}")
    public ResponseEntity<Map<String, String>> modifyNotice(@PathVariable Long id, @RequestBody NoticeCreateDto dto) {
        noticeService.noticeUpdate(id, dto);
        return ResponseEntity.ok(Map.of("message", "공지사항이 수정되었습니다"));
    }
 }
