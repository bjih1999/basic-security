package io.security.service;

import io.security.domain.entity.Resource;
import io.security.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityResourceSevice {

    private final ResourceRepository resourceRepository;

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resource> resourcesList = resourceRepository.findAllResources();
        resourcesList.forEach(resource -> {
            List<ConfigAttribute> configAttributeList = new ArrayList<>();
            resource.getRoleSet().forEach(role -> {
                configAttributeList.add(new SecurityConfig((role.getRoleName())));
                result.put(new AntPathRequestMatcher(resource.getResourceName()), configAttributeList);
            });
        });

        return result;
    }
}
