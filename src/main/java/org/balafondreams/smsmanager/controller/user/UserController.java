package org.balafondreams.smsmanager.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.user.UserDTO;
import org.balafondreams.smsmanager.domain.models.user.UserUpdateDTO;
import org.balafondreams.smsmanager.repository.UserSearchCriteria;
import org.balafondreams.smsmanager.security.CurrentUser;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(userService.getUser(currentUser.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getUsers(
            UserSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(criteria, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(userService.updateUser(id, updateDTO));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roles) {
        return ResponseEntity.ok(userService.updateUserRoles(id, roles));
    }
}
