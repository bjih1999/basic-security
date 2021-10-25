package io.security.security.handler;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
AuthenticationSuccessHandler
인증 성공 후 처리를 담당하는 핸들러 클래스
Request를 통해 세션을 가져와 세션과 관련된 작업을 한다던지,
인증이 성공된 인증 객체에서 데이터를 가져와 인증 정책을 구현할 수 있음
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     *
     * @param request 요청 객체
     * @param response  응답 객체
     * @param authentication    인증완료된 인증 객체
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        setDefaultTargetUrl("/");

        /* 사용자가 인증에 성공하기 전에 요청을 했던 정보를 담고 있는 객체
        기존 요청 내역을 참조하여 인증 후 원하는 URL로 바로 리다이렉션 가능
        ex) 1. 로그인 안한 상태로 인가가 필요한 리소스에 접근 시도
            2. 요청 내역은 RequestCache에 저장
            3. 로그인 된 상태가 아니기 때문에 인증 요청 예외
            4. 인증 예외 이후 로그인 수행
            5. 기존에 요청 내역이 있기 때문에 기조넹 접근하고자 하는 리소스에 접근할 수 있도록 리다이렉션
        */
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // 인증 성공 이후, 기존에 요청한 기록이 있는 경우 이전에 요청했던 URL로 보내줌
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
        else {  //기존에 요청한 기록이 없는 경우 DefaultTargetUrl로 보내줌
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
