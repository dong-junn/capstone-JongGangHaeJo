package jeiu.capstone.jongGangHaejo.security.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationCheck {
    public String getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
