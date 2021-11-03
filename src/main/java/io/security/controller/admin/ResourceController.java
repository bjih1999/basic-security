package io.security.controller.admin;


import io.security.domain.Dto.ResourceDto;
import io.security.domain.entity.Resource;
import io.security.domain.entity.Role;
import io.security.repository.RoleRepository;
import io.security.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.security.service.ResourceService;
import io.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    private final RoleRepository roleRepository;

    private final RoleService roleService;

    private final UrlFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    @GetMapping(value="/admin/resources")
    public String getResources(Model model) throws Exception {

        List<Resource> resources = resourceService.getResources();
        model.addAttribute("resources", resources);

        return "admin/resource/list";
    }

    @PostMapping(value="/admin/resources")
    public String createResources(ResourceDto resourceDto) throws Exception {

        ModelMapper modelMapper = new ModelMapper();
        Role role = roleRepository.findByRoleName(resourceDto.getRoleName());
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        Resource resources = modelMapper.map(resourceDto, Resource.class);
        resources.setRoleSet(roles);

        resourceService.createResource(resources);

        filterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }

    @GetMapping(value="/admin/resources/register")
    public String viewRoles(Model model) throws Exception {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);

        ResourceDto resources = new ResourceDto();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());
        resources.setRoleSet(roleSet);
        model.addAttribute("resources", resources);

        return "admin/resource/detail";
    }

    @GetMapping(value="/admin/resources/{id}")
    public String getResources(@PathVariable String id, Model model) throws Exception {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
        Resource resources = resourceService.getResource(Long.valueOf(id));

        ModelMapper modelMapper = new ModelMapper();
        ResourceDto resourceDto = modelMapper.map(resources, ResourceDto.class);
        model.addAttribute("resources", resourceDto);

        return "admin/resource/detail";
    }

    @GetMapping(value="/admin/resources/delete/{id}")
    public String removeResources(@PathVariable String id, Model model) throws Exception {

        Resource resources = resourceService.getResource(Long.valueOf(id));
        resourceService.deleteResource(Long.valueOf(id));

        filterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }
}