package org.balafondreams.smsmanager.service.sms;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.sms.Conversation;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.mapper.ConversationMapper;
import org.balafondreams.smsmanager.domain.models.sms.ConversationDTO;
import org.balafondreams.smsmanager.repository.ConversationRepository;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final UserService userService;

    public Page<ConversationDTO> getUserConversations(Long userId, Pageable pageable) {
        return conversationRepository.findByUserId(userId, pageable)
                .map(conversationMapper::toDto);
    }

    public ConversationDTO getConversation(Long id, Long userId) {
        Conversation conversation = conversationRepository.findByIdWithMessages(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        return conversationMapper.toDto(conversation);
    }

    public Page<ConversationDTO> searchConversations(String search, Long userId, Pageable pageable) {
        return conversationRepository.searchConversations(userId, search, pageable)
                .map(conversationMapper::toDto);
    }

    public void updateConversationName(Long id, String name, Long userId) {
        Conversation conversation = conversationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        conversation.setName(name);
        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public boolean hasRecentMessages(Long conversationId, Long userId) {
        if (!conversationRepository.existsByIdAndUserId(conversationId, userId)) {
            throw new ResourceNotFoundException("Conversation not found");
        }

        LocalDateTime since = LocalDateTime.now().minusDays(1);
        return conversationRepository.countRecentMessages(conversationId, since) > 0;
    }
}
