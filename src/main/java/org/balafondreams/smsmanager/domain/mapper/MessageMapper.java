package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.domain.models.sms.MessageCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.MessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "conversation.id", target = "conversationId")
    MessageDTO toDto(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    Message toEntity(MessageCreateDTO createDTO);

    @Named("toMessagePreview")
    @Mapping(target = "content", expression = "java(truncateContent(message.getContent()))")
    MessageDTO toMessagePreview(Message message);

    default String truncateContent(String content) {
        if (content == null) return null;
        return content.length() > 50 ? content.substring(0, 47) + "..." : content;
    }
    default MessageMapper getMessageMapper() {
        return this;
    }
}
