package jeiu.capstone.jongGangHaejo.dto.request;

import lombok.Setter;
import lombok.ToString;

@Setter //안열어 주면 Controller params에 null값이 들어감 -> params=PostCreate(title=null, content=null)[log결과]
@ToString //toString 구현
public class PostCreateDto { //Controller에 params를 넘기기 위한 DTO

    //public으로 노출 할 이유가 없으므로 private
    private String title;
    private String content;
}
