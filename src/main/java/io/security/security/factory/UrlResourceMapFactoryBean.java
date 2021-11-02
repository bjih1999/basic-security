package io.security.security.factory;

import io.security.service.SecurityResourceSevice;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;

public class UrlResourceMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    @Setter
    private SecurityResourceSevice securityResourceSevice;

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap;

    /*
    싱글톤 처리를 할것인지 설정하는 함수
    */
    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() throws Exception {
        if (resourceMap == null) {
            init();
        }

        return resourceMap;
    }

    private void init() {
        resourceMap = securityResourceSevice.getResourceList();
    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }
}
