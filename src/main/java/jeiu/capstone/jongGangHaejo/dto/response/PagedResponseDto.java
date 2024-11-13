package jeiu.capstone.jongGangHaejo.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedResponseDto<T> {
    private List<T> content;      // 현재 페이지의 데이터
    private int page;             // 현재 페이지 번호
    private int size;             // 페이지당 데이터 수
    private long totalElements;   // 전체 데이터 수
    private int totalPages;       // 전체 페이지 수
    private boolean last;         // 마지막 페이지 여부

    public PagedResponseDto(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
