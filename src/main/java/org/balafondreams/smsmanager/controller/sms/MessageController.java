package org.balafondreams.smsmanager.controller.sms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.sms.MessageCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.MessageDTO;
import org.balafondreams.smsmanager.security.CurrentUser;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.balafondreams.smsmanager.service.sms.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(
            @Valid @RequestBody MessageCreateDTO createDTO,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(messageService.sendMessage(createDTO, currentUser.getId()));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageDTO>> getConversationMessages(
            @PathVariable Long conversationId,
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(messageService.getConversationMessages(
                conversationId,
                currentUser.getId(),
                pageable
        ));
    }

    @PostMapping("/{id}/resend")
    public ResponseEntity<MessageDTO> resendMessage(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(messageService.resendMessage(id, currentUser.getId()));
    }
}
