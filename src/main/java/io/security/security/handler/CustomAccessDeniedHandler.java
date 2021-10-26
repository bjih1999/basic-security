package io.security.security.handler;

import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Setter
    private String errorPage = "/denied";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        UriComponents deniedUriComponents = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8080")
                    .path("/" + errorPage)
                    .queryParam("exception", accessDeniedException.getMessage())
                    .build()
                    .encode()
                ;

        response.sendRedirect(deniedUriComponents.toString());
    }
}
