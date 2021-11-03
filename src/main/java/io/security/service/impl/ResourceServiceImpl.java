package io.security.service.impl;

import io.security.domain.entity.Resource;
import io.security.repository.ResourceRepository;
import io.security.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository ResourceRepository;

    @Transactional
    public Resource getResource(long id) {
        return ResourceRepository.findById(id).orElse(new Resource());
    }

    @Transactional
    public List<Resource> getResources() {
        return ResourceRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    @Transactional
    public void createResource(Resource resources){
        ResourceRepository.save(resources);
    }

    @Transactional
    public void deleteResource(long id) {
        ResourceRepository.deleteById(id);
    }
}