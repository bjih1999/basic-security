package io.security.security.metadatasource;

import com.sun.jdi.connect.ListeningConnector;
import io.security.service.SecurityResourceSevice;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();
    private SecurityResourceSevice securityResourceSevice;


    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap, SecurityResourceSevice securityResourceSevice) {
        this.requestMap = requestMap;
        this.securityResourceSevice = securityResourceSevice;
    }
    /*
    요청 정보와 리소스(여기서는 URL)의 권한 정보를 비교
    SecurityConfig에서 이 정보를 FilterSecurityInterceptor를 감싸 새로운 bean으로 생성하여
    FilterSecurityInterceptor 앞에 필터를 등록하여 사용
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        if (request != null) {
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
                RequestMatcher matcher = entry.getKey();
                if (matcher.matches(request)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        this.requestMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    public void reload() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = securityResourceSevice.getResourceList();
        Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadedMap.entrySet().iterator();

        requestMap.clear();

        while (iterator.hasNext()) {
            Map.Entry<RequestMatcher, List<ConfigAttribute>> next = iterator.next();
            requestMap.put(next.getKey(), next.getValue());
        }
    }

}
