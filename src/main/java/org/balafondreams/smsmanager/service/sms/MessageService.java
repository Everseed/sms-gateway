package org.balafondreams.smsmanager.service.sms;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.sms.Conversation;
import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.domain.entities.sms.MessageStatus;
import org.balafondreams.smsmanager.domain.entities.sms.MessageType;
import org.balafondreams.smsmanager.domain.entities.template.MessageTemplate;
import org.balafondreams.smsmanager.domain.exception.InvalidRequestException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.exception.SMSGatewayException;
import org.balafondreams.smsmanager.domain.mapper.MessageMapper;
import org.balafondreams.smsmanager.domain.models.sms.MessageCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.MessageDTO;
import org.balafondreams.smsmanager.repository.ConversationRepository;
import org.balafondreams.smsmanager.repository.MessageRepository;
import org.balafondreams.smsmanager.repository.MessageSearchCriteria;
import org.balafondreams.smsmanager.repository.MessageSpecification;
import org.balafondreams.smsmanager.service.SMSGatewayService;
import org.balafondreams.smsmanager.service.template.MessageTemplateService;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final MessageTemplateService templateService;
    private final SMSGatewayService smsGatewayService;

    /**
     * Envoi d'un nouveau message
     */
    public MessageDTO sendMessage(MessageCreateDTO createDTO, Long userId) {
        // Valider le numéro de téléphone
        if (!isValidPhoneNumber(createDTO.getPhoneNumber())) {
            throw new InvalidRequestException("Invalid phone number format");
        }

        Message message = new Message();

        // Traitement du contenu selon le type (template ou direct)
        if (createDTO.getTemplateId() != null) {
            processTemplateMessage(message, createDTO, userId);
        } else {
            processDirectMessage(message, createDTO);
        }

        // Configuration commune
        message.setPhoneNumber(createDTO.getPhoneNumber());
        message.setStatus(MessageStatus.PENDING);
        message.setUser(userService.getCurrentUser());
        message.setScheduledAt(createDTO.getScheduledAt());

        // Gestion de la conversation
        Conversation conversation = getOrCreateConversation(createDTO.getPhoneNumber(), userId);
        message.setConversation(conversation);

        // Envoi immédiat ou programmé
        if (message.getScheduledAt() == null) {
            sendMessageImmediately(message);
        }

        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    /**
     * Récupération des messages d'une conversation
     */
    public Page<MessageDTO> getConversationMessages(Long conversationId, Long userId, Pageable pageable) {
        if (!conversationRepository.existsByIdAndUserId(conversationId, userId)) {
            throw new ResourceNotFoundException("Conversation not found");
        }

        return messageRepository.findByConversationIdAndUserIdOrderByCreatedAtDesc(
                conversationId,
                userId,
                pageable
        ).map(messageMapper::toDto);
    }

    /**
     * Recherche de messages
     */
    public Page<MessageDTO> searchMessages(MessageSearchCriteria criteria, Long userId, Pageable pageable) {
        return messageRepository.findAll(
                MessageSpecification.withCriteria(criteria, userId),
                pageable
        ).map(messageMapper::toDto);
    }

    /**
     * Traitement des messages programmés
     */
    @Transactional
    public void processScheduledMessages() {
        List<Message> scheduledMessages = messageRepository.findByStatusAndScheduledAtBeforeAndSentAtIsNull(
                MessageStatus.PENDING,
                LocalDateTime.now()
        );

        for (Message message : scheduledMessages) {
            try {
                sendMessageImmediately(message);
                messageRepository.save(message);
            } catch (Exception e) {
                handleMessageFailure(message, e);
            }
        }
    }

    /**
     * Renvoyer un message
     */
    public MessageDTO resendMessage(Long messageId, Long userId) {
        Message message = messageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (message.getStatus() != MessageStatus.FAILED) {
            throw new InvalidRequestException("Only failed messages can be resent");
        }

        message.setStatus(MessageStatus.PENDING);
        message.setSentAt(null);

        try {
            sendMessageImmediately(message);
        } catch (Exception e) {
            handleMessageFailure(message, e);
        }

        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    /**
     * Annuler un message programmé
     */
    public void cancelScheduledMessage(Long messageId, Long userId) {
        Message message = messageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (message.getStatus() != MessageStatus.PENDING || message.getScheduledAt() == null) {
            throw new InvalidRequestException("Only scheduled messages can be cancelled");
        }

        messageRepository.delete(message);
    }

    /**
     * Obtenir les statistiques des messages
     */
    public MessageStats getMessageStats(Long userId) {
        return MessageStats.builder()
                .totalMessages(messageRepository.countByUserId(userId))
                .sentMessages(messageRepository.countByUserIdAndStatus(userId, MessageStatus.SENT))
                .failedMessages(messageRepository.countByUserIdAndStatus(userId, MessageStatus.FAILED))
                .pendingMessages(messageRepository.countByUserIdAndStatus(userId, MessageStatus.PENDING))
                .build();
    }

    // Méthodes privées d'aide

    private void processTemplateMessage(Message message, MessageCreateDTO createDTO, Long userId) {
        MessageTemplate template = templateService.getTemplate(createDTO.getTemplateId(), userId);

        String processedContent = templateService.processTemplate(
                template.getId(),
                createDTO.getTemplateVariables(),
                userId
        );

        message.setTemplate(template);
        message.setTemplateVariables(createDTO.getTemplateVariables());
        message.setContent(processedContent);
        message.setType(MessageType.TEMPLATE);
    }

    private void processDirectMessage(Message message, MessageCreateDTO createDTO) {
        if (createDTO.getContent() == null || createDTO.getContent().trim().isEmpty()) {
            throw new InvalidRequestException("Message content is required");
        }

        message.setContent(createDTO.getContent());
        message.setType(MessageType.SMS);
    }

    private Conversation getOrCreateConversation(String phoneNumber, Long userId) {
        return conversationRepository
                .findByPhoneNumberAndUserId(phoneNumber, userId)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setPhoneNumber(phoneNumber);
                    newConversation.setUser(userService.getCurrentUser());
                    return conversationRepository.save(newConversation);
                });
    }

    private void sendMessageImmediately(Message message) {
        try {
            smsGatewayService.sendSMS(message);
            message.setStatus(MessageStatus.SENT);
            message.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            handleMessageFailure(message, e);
            throw new SMSGatewayException("Failed to send message", e);
        }
    }

    private void handleMessageFailure(Message message, Exception e) {
        message.setStatus(MessageStatus.FAILED);
        messageRepository.save(message);
        // Ici, vous pourriez ajouter de la journalisation ou des notifications
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Implémentez votre logique de validation de numéro de téléphone
        return phoneNumber != null && phoneNumber.matches("^\\+?[1-9]\\d{1,14}$");
    }

    @Data
    @Builder
    public static class MessageStats {
        private long totalMessages;
        private long sentMessages;
        private long failedMessages;
        private long pendingMessages;
    }
}