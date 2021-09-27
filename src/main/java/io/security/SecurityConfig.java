package io.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                    .formLogin()                    // form login을 사용하겠다.
//                    .loginPage("/loginPage")          // 사용자 정의 로그인 URI
                    .defaultSuccessUrl("/")             // 로그인 성공 시 리다이렉션 URI
                    .failureUrl("/login")               // 로그인 실패 시 리다이렉션 URI
                    .usernameParameter("userId")        // ID 파라미터명 설정
                    .passwordParameter("passwd")        // 비밀번호 파라미터명 설정
                    .loginProcessingUrl("/login_proc")  //
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
                    .permitAll();
    }
}
