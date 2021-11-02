package io.security.security.provider;

import io.security.security.common.FormWebAuthenticationDetails;
import io.security.security.service.AccountContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    /*
    AuthenticationProvider
    인증되지 않은 인증 객체를 전달 받아, 인증 객체를 검증한 후 인증 여부와 권한을 담은 인증 객체를 생성하여 리턴하는 클래스
    
    인자의 authentication는 AuthenticationManager로부터 전달 받는 인증 객체
    사용자가 입력한 Id, password 정보가 담겨 있음
     */
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();

        AccountContext accountContext = (AccountContext)userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, accountContext.getPassword())) {
            throw new BadCredentialsException("Bad Credential");
        }

        /*
        authentication의 detail 객체를 리턴받아 커스텀 인증 로직 추가
         */
        FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication.getDetails();
        String secretKey = details.getSecretKey();
        if (secretKey == null || !"secret".equals(secretKey)) {
            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");
        }

        /*
        UsernamePasswordAuthenticationToken의 생성자는 2개이다. principal(유저 객체)과 credential(pw)만 받는 생성자와, 권한정보까지 받는 생성자
        첫번째 생성자는 AuthenticationManager에게 전달할 때 일단 인증 객체를 만들기 위해서 사용하는 생성자이고,
        두번째 생성자는 AuthenticationProvider에서 인증 여부가 확인되면 권한정보까지 포함하여 인증 객체를 재생성할때 사용되는 생성자이다.
        따라서 우리는 두번째 생성자를 사용한다.
         */
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(accountContext.getAccount(), accountContext.getPassword(), accountContext.getAuthorities());
        
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
