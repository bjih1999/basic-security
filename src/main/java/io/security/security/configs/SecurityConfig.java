package io.security.security.configs;

//import io.security.security.filter.AjaxLoginProcessingFilter;
import io.security.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.security.security.provider.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final AuthenticationProvider authenticationProvider;

    private final AuthenticationDetailsSource authenticationDetailsSource;

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final AccessDeniedHandler accessDeniedHandler;

//    private final AjaxLoginProcessingFilter ajaxLoginProcessingFilter;

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());

        return filterSecurityInterceptor;
    }

    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoters());
        return affirmativeBased;
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        return Arrays.asList(new RoleVoter());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.
//                    userDetailsService(userDetailsService);
                    authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());    // 정적 파일들이 보안 필터를 거치지 않음 ex) "css/", "js/", "images/", "webjars/"
                                                                                                // Q: 그러면 permitAll과 다른 점은 무엇이냐?
                                                                                                // A: permitAll은 보안 필터를 거치긴 한다(익명 사용자를 허용하는 등) 하지만 web.ignoring은 아예 필터를 거치지 않는다.
    }

    //    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                    .inMemoryAuthentication()
//                    .withUser("user")
//                    .password("{noop}1111")
//                    .roles("USER")
//                .and()
//                    .withUser("sys")
//                    .password("{noop}1111")
//                    .roles("SYS", "USER")
//                .and()
//                    .withUser("admin")
//                    .password("{noop}1111")
//                    .roles("ADMIN", "SYS", "USER")
//
//
//        ;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/users", "/login*").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated()

            .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .defaultSuccessUrl("/")
                .authenticationDetailsSource(authenticationDetailsSource)
                .defaultSuccessUrl("/")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll()
        ;

        http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)  // 인가 예외 처리를 받아 처리하는 핸들러 등록
        ;

        http
                // 인가처리는 요청당 한번만 발생하기 때문에 customFilterSecurityInterceptor를 통과하면 FilterSecurityInterceptior는 자동 통과함
                // && cutomFilterSecurityInterceptor의 인가처리를 받는 요청들은 위 antMatchers의 영향을 받지 않음
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
//                .addFilterBefore(ajaxLoginProcessingFilter, UsernamePasswordAuthenticationFilter.class) //ajax 방식 요청
        ;

    }
    //    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http
//                .authorizeRequests()
//                .antMatchers("/login").permitAll()
//                .antMatchers("/user").hasRole("USER")
//                .antMatchers("/admin/pay").hasRole("ADMIN")
//                .antMatchers("/admin/**").access("hasRole('ADMIN') or hasRole('SYS')")
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()                    // form login을 사용하겠다.
////                    .loginPage("/loginPage")          // 사용자 정의 로그인 URI
//                .defaultSuccessUrl("/")             // 로그인 성공시 리다이렉션 URI
//                .failureUrl("/login")               // 로그인 실패시 리다이렉션 URI
//                .usernameParameter("userId")        // ID 파라미터명 설정
//                .passwordParameter("passwd")        // 비밀번호 파라미터명 설정
//                .loginProcessingUrl("/login_proc")  // 로그인 form action URI
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        System.out.println("authentication " + authentication.getName());
//                        RequestCache requestCache = new HttpSessionRequestCache();
//                        SavedRequest savedRequest = requestCache.getRequest(request, response);
//                        String redirectUrl = savedRequest.getRedirectUrl();
//                        response.sendRedirect(redirectUrl);
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                        System.out.println("exception " + exception.getMessage());
//                        response.sendRedirect("/login");
//                    }
//                })
//                .permitAll()
//
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    @Override
//                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                        response.sendRedirect("/login");
//                    }
//                })
//                .accessDeniedHandler(new AccessDeniedHandler() {
//                    @Override
//                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                        response.sendRedirect("/denied");
//                    }
//                })
//
//                .and()
//                .logout()                                       // 로그아웃 설정을 하겠다. **logout 요청은 원칙적으로 POST이어야함!!**
//                .logoutSuccessUrl("/login")                         // 로그아웃 성공시 리다이렉션 URI
//                .addLogoutHandler(new LogoutHandler() {             // 로그아웃을 처리할 핸들러
//                    @Override
//                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//                        HttpSession session = request.getSession();
//                        session.invalidate();
//                    }
//                })
//                .logoutSuccessHandler(new LogoutSuccessHandler() {  // 로그아웃 성공시 추가 처리를 수행할 핸들러
//                    @Override
//                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect("login"); // 앞에 logoutSuccessUrl을 설정하였기 때문에 사실 여기서 처리 안해줘도 상관 없음
//                    }
//                })
//                .deleteCookies("remember-me")                      // 쿠키 삭제
//                .and()
//                .rememberMe()                               // 리멤버 미 인증을 사용하겠다.
//                .rememberMeParameter("remember")             // 체크박스의 파라미터 이름, default "remeber-me"
//                .tokenValiditySeconds(3600)                 // 토큰 유효기간, default 14일
//                .userDetailsService(userDetailsService  )   // 토큰 발급을 위한 정보 조회
//                .and()
//                .sessionManagement()                                        // 세션관리를 활성화하겠다.
////                    .sessionFixation()                                            // 세션 고정 공격
////                    .changeSessionId()                                            // 인증마다 세션 재발급이며 기본값이다.
////                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)     // 기본값, 스프링 시큐리티가 필요시 세션을 생성
//                /*
//                세션 정책
//                SessionCreationPolicy.Always : 스프링 시큐리티가 항상 세션 생성
//                SessionCreationPolicy.If_Required : 스프링 시큐리티가 필요 시 세션 생성
//                SessionCreationPolicy.Never : 스프링 시큐리티가 세션을 생성하지 않지만 이미 존재하면 사용
//                SessionCreationPolicy.Stateless : 스프링 시큐리티가 세션을 생성하지 읺고 존재해도 사용하지 않음 (JWT와 같은 토큰 기반 인증 방식일 때 사용될듯)
//                 */
//                .maximumSessions(1)                                             // 최대 허용 가능 세션 수, -1은 무제한 로그인 세션 허용
//                .maxSessionsPreventsLogin(true)                                 // 동시 로그인 차단, default는 false
//                .expiredUrl("/expired")                                         // 세션이 만료된 경우 이동할 페이지
//        ;
//
////        http
////                .csrf().disable()     // api 개발 시, postman으로 테스트 하기 위해 csrf필터 비활성화
//    }

}
