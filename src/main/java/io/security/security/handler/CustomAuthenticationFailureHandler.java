package io.security.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /*
    인증 실패시 발생하는 인증 예외(ex. AuthenticationProvider)를 받아 실패 처리를 하는 필터 설정
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        String errorMessage = exception.getMessage();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                                        .fromHttpUrl("http://localhost:8080")
                                        .path("/login")
                                        .queryParam("error", true)
                                        .queryParam("exception", exception.getMessage())
                                        .build()
                                        .encode()
                                        ;
        //로그인 URL에 쿼리스트링으로 에러 메세지를 전달함으로써, 로그인 화면에서 오류 메세지를 받아 출력할 수 있게함
        setDefaultFailureUrl(uriComponents.toString());

        super.onAuthenticationFailure(request, response, exception);

    }
}
