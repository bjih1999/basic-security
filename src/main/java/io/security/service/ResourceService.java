package io.security.service;

import io.security.domain.entity.Resource;

import java.util.List;

public interface ResourceService {

    Resource getResource(long id);

    List<Resource> getResources();

    void createResource(Resource Resources);

    void deleteResource(long id);
}