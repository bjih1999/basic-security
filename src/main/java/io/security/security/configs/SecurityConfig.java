package io.security.security.configs;

//import io.security.security.filter.AjaxLoginProcessingFilter;
import io.security.security.factory.UrlResourceMapFactoryBean;
import io.security.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.security.security.provider.CustomAuthenticationProvider;
import io.security.service.SecurityResourceSevice;
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

    private final SecurityResourceSevice securityResourceSevice;

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
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());    // ?????? ???????????? ?????? ????????? ????????? ?????? ex) "css/", "js/", "images/", "webjars/"
                                                                                                // Q: ????????? permitAll??? ?????? ?????? ?????????????
                                                                                                // A: permitAll??? ?????? ????????? ????????? ??????(?????? ???????????? ???????????? ???) ????????? web.ignoring??? ?????? ????????? ????????? ?????????.
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
                .accessDeniedHandler(accessDeniedHandler)  // ?????? ?????? ????????? ?????? ???????????? ????????? ??????
        ;

        http
                // ??????????????? ????????? ????????? ???????????? ????????? customFilterSecurityInterceptor??? ???????????? FilterSecurityInterceptior??? ?????? ?????????
                // && cutomFilterSecurityInterceptor??? ??????????????? ?????? ???????????? ??? antMatchers??? ????????? ?????? ??????
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
//                .addFilterBefore(ajaxLoginProcessingFilter, UsernamePasswordAuthenticationFilter.class) //ajax ?????? ??????
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
//                .formLogin()                    // form login??? ???????????????.
////                    .loginPage("/loginPage")          // ????????? ?????? ????????? URI
//                .defaultSuccessUrl("/")             // ????????? ????????? ??????????????? URI
//                .failureUrl("/login")               // ????????? ????????? ??????????????? URI
//                .usernameParameter("userId")        // ID ??????????????? ??????
//                .passwordParameter("passwd")        // ???????????? ??????????????? ??????
//                .loginProcessingUrl("/login_proc")  // ????????? form action URI
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
//                .logout()                                       // ???????????? ????????? ?????????. **logout ????????? ??????????????? POST????????????!!**
//                .logoutSuccessUrl("/login")                         // ???????????? ????????? ??????????????? URI
//                .addLogoutHandler(new LogoutHandler() {             // ??????????????? ????????? ?????????
//                    @Override
//                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//                        HttpSession session = request.getSession();
//                        session.invalidate();
//                    }
//                })
//                .logoutSuccessHandler(new LogoutSuccessHandler() {  // ???????????? ????????? ?????? ????????? ????????? ?????????
//                    @Override
//                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect("login"); // ?????? logoutSuccessUrl??? ??????????????? ????????? ?????? ????????? ?????? ???????????? ?????? ??????
//                    }
//                })
//                .deleteCookies("remember-me")                      // ?????? ??????
//                .and()
//                .rememberMe()                               // ????????? ??? ????????? ???????????????.
//                .rememberMeParameter("remember")             // ??????????????? ???????????? ??????, default "remeber-me"
//                .tokenValiditySeconds(3600)                 // ?????? ????????????, default 14???
//                .userDetailsService(userDetailsService  )   // ?????? ????????? ?????? ?????? ??????
//                .and()
//                .sessionManagement()                                        // ??????????????? ??????????????????.
////                    .sessionFixation()                                            // ?????? ?????? ??????
////                    .changeSessionId()                                            // ???????????? ?????? ??????????????? ???????????????.
////                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)     // ?????????, ????????? ??????????????? ????????? ????????? ??????
//                /*
//                ?????? ??????
//                SessionCreationPolicy.Always : ????????? ??????????????? ?????? ?????? ??????
//                SessionCreationPolicy.If_Required : ????????? ??????????????? ?????? ??? ?????? ??????
//                SessionCreationPolicy.Never : ????????? ??????????????? ????????? ???????????? ????????? ?????? ???????????? ??????
//                SessionCreationPolicy.Stateless : ????????? ??????????????? ????????? ???????????? ?????? ???????????? ???????????? ?????? (JWT??? ?????? ?????? ?????? ?????? ????????? ??? ????????????)
//                 */
//                .maximumSessions(1)                                             // ?????? ?????? ?????? ?????? ???, -1??? ????????? ????????? ?????? ??????
//                .maxSessionsPreventsLogin(true)                                 // ?????? ????????? ??????, default??? false
//                .expiredUrl("/expired")                                         // ????????? ????????? ?????? ????????? ?????????
//        ;
//
////        http
////                .csrf().disable()     // api ?????? ???, postman?????? ????????? ?????? ?????? csrf?????? ????????????
//    }

}
