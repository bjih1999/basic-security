package io.security.repository;

import io.security.domain.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    /*
    orderNum으로 내림차순 정렬해주는 이유
    권한 계층 구조인 USER < ADMIN < SYS를 유지하기 위해서
    마찬가지로 FilterInvocationSecurityMetaDataSource의 requestMap에 권한 정보를 담을 때 LinkedHashMap을 사용하는 이유도
    순서를 유지하기 위해서이다.
     */
    @Query("select r from Resource r join fetch r.roleSet where r.resourceType = 'url' order by r.orderNum desc")
    List<Resource> findAllResources();
}