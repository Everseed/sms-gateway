package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.sms.Conversation;
import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.domain.entities.sms.MessageStatus;
import org.balafondreams.smsmanager.domain.models.sms.ConversationDTO;
import org.balafondreams.smsmanager.domain.models.sms.MessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        uses = {MessageMapper.class},
        imports = {Collectors.class, Comparator.class}
)
public interface ConversationMapper {
    @Mapping(target = "lastMessageContent", source = "messages", qualifiedByName = "extractLastMessageContent")
    @Mapping(target = "unreadCount", expression = "java(countUnreadMessages(conversation))")
    @Mapping(target = "recentMessages", source = "messages", qualifiedByName = "getRecentMessages")
    ConversationDTO toDto(Conversation conversation);

    List<ConversationDTO> toDtoList(List<Conversation> conversations);

    @Named("extractLastMessageContent")
    default String extractLastMessageContent(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getContent)
                .orElse(null);
    }

    @Named("getRecentMessages")
    default List<MessageDTO> getRecentMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        return List.of();
        // TODO: correct tihs
//        return messages.stream()
//                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
//                .limit(10)  // Limite aux 10 derniers messages
//                .map(message -> getMessageMapper().toDto(message))
//                .collect(Collectors.toList());
    }

    default int countUnreadMessages(Conversation conversation) {
        if (conversation.getMessages() == null) {
            return 0;
        }
        return (int) conversation.getMessages().stream()
                .filter(message -> MessageStatus.DELIVERED.equals(message.getStatus()))
                .count();
    }

    // Nécessaire pour accéder au MessageMapper dans les méthodes par défaut
//    @Named("messageMapper")
//    MessageMapper getMessageMapper();
//    @Named("messageMapper")
//    MessageMapper getMessageMapper(Message message, MessageDTO messageDTO);
}
