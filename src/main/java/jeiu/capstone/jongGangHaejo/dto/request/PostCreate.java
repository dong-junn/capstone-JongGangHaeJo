package jeiu.capstone.jongGangHaejo.dto.request;

import lombok.Setter;
import lombok.ToString;

//@Setter //안열어 주면 Controller에 null값이 들어감 -> params=PostCreate(title=null, content=null)[log결과]
@ToString //toString 구현
public class PostCreate { //Controller에서 params를 넘기기 위한 DTO

    public String title;
    public String content;
}
