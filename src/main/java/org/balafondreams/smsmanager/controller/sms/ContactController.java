package org.balafondreams.smsmanager.controller.sms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.sms.ContactCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.ContactDTO;
import org.balafondreams.smsmanager.security.CurrentUser;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.balafondreams.smsmanager.service.sms.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<ContactDTO>> getContacts(
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(contactService.getUserContacts(
                currentUser.getId(),
                pageable
        ));
    }

    @PostMapping
    public ResponseEntity<ContactDTO> createContact(
            @Valid @RequestBody ContactCreateDTO createDTO,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(contactService.createContact(
                createDTO,
                currentUser.getId()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactDTO updateDTO,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(contactService.updateContact(
                id,
                updateDTO,
                currentUser.getId()
        ));
    }
}