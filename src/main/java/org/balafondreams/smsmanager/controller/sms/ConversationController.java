package org.balafondreams.smsmanager.controller.sms;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.sms.ConversationDTO;
import org.balafondreams.smsmanager.security.CurrentUser;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.balafondreams.smsmanager.service.sms.ConversationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<Page<ConversationDTO>> getConversations(
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault(sort = "lastMessageAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(conversationService.getUserConversations(
                currentUser.getId(),
                pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDTO> getConversation(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(conversationService.getConversation(
                id,
                currentUser.getId()
        ));
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<Void> updateConversationName(
            @PathVariable Long id,
            @RequestBody String name,
            @CurrentUser UserPrincipal currentUser) {
        conversationService.updateConversationName(id, name, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}