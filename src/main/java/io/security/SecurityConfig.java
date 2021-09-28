package io.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                    .formLogin()                    // form login을 사용하겠다.
//                    .loginPage("/loginPage")          // 사용자 정의 로그인 URI
                    .defaultSuccessUrl("/")             // 로그인 성공시 리다이렉션 URI
                    .failureUrl("/login")               // 로그인 실패시 리다이렉션 URI
                    .usernameParameter("userId")        // ID 파라미터명 설정
                    .passwordParameter("passwd")        // 비밀번호 파라미터명 설정
                    .loginProcessingUrl("/login_proc")  // 로그인 form action URI
                    .successHandler(new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            System.out.println("authentication " + authentication.getName());
                            response.sendRedirect("/");
                        }
                    })
                    .failureHandler(new AuthenticationFailureHandler() {
                        @Override
                        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                            System.out.println("exception " + exception.getMessage());
                            response.sendRedirect("/login");
                        }
                    })
                    .permitAll()
                .and()
                    .logout()                                       // 로그아웃 설정을 하겠다. **logout 요청은 원칙적으로 POST이어야함!!**
                    .logoutSuccessUrl("/login")                         // 로그아웃 성공시 리다이렉션 URI
                    .addLogoutHandler(new LogoutHandler() {             // 로그아웃을 처리할 핸들러
                        @Override
                        public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                            HttpSession session = request.getSession();
                            session.invalidate();
                        }
                    })
                    .logoutSuccessHandler(new LogoutSuccessHandler() {  // 로그아웃 성공시 추가 처리를 수행할 핸들러
                        @Override
                        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            response.sendRedirect("login"); // 앞에 logoutSuccessUrl을 설정하였기 때문에 사실 여기서 처리 안해줘도 상관 없음
                        }
                    })
                    .deleteCookies("remember-me")                      // 쿠키 삭제
                .and()
                    .rememberMe()                               // 리멤버 미 인증을 사용하겠다.
                    .rememberMeParameter("remeber")             // 체크박스의 파라미터 이름, default "remeber-me"
                    .tokenValiditySeconds(3600)                 // 토큰 유효기간, default 14일
                    .userDetailsService(userDetailsService)   // 토큰 발급을 위한 정보 조회
        ;

    }

}
