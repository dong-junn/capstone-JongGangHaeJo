package jeiu.capstone.jongGangHaejo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(requests -> requests
                    .requestMatchers("/","/login", "post").permitAll() // "/", "/post", "/login"에 대해선 모든 권한을 열어준다
                    .anyRequest().authenticated() //위 허용한 api외에는 권한을 인가받아야 한다
            )
            .csrf(AbstractHttpConfigurer::disable); //람다식으로 간략화

        return http.build();
    }
}
