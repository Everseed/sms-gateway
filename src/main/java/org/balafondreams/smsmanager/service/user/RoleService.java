package org.balafondreams.smsmanager.service.user;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.user.ERole;
import org.balafondreams.smsmanager.domain.entities.user.Role;
import org.balafondreams.smsmanager.domain.mapper.RoleMapper;
import org.balafondreams.smsmanager.domain.models.user.RoleDTO;
import org.balafondreams.smsmanager.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Set<Role> getDefaultRoles() {
        return Set.of(roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found")));
    }

    public Set<Role> getRolesByNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(name -> ERole.valueOf("ROLE_" + name.toUpperCase()))
                .map(eRole -> roleRepository.findByName(eRole)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + eRole)))
                .collect(Collectors.toSet());
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }
}
