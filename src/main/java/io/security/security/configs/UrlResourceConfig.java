package io.security.security.configs;

import io.security.security.factory.UrlResourceMapFactoryBean;
import io.security.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.security.service.SecurityResourceSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UrlResourceConfig {

    private final SecurityResourceSevice securityResourceSevice;

    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception{
        return new UrlFilterInvocationSecurityMetadataSource(urlResourceMapFactoryBean().getObject(), securityResourceSevice);
    }

    private UrlResourceMapFactoryBean urlResourceMapFactoryBean() {
        UrlResourceMapFactoryBean urlResourceMapFactoryBean = new UrlResourceMapFactoryBean();
        urlResourceMapFactoryBean.setSecurityResourceSevice(securityResourceSevice);

        return urlResourceMapFactoryBean;
    }
}
