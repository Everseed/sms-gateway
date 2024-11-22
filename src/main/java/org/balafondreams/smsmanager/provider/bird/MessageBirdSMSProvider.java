package org.balafondreams.smsmanager.provider.bird;

import org.balafondreams.smsmanager.provider.SMSProvider;
import org.balafondreams.smsmanager.provider.SMSRequest;
import org.balafondreams.smsmanager.provider.SMSResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class MessageBirdSMSProvider implements SMSProvider {

    @Value("${messagebird.access-key}")
    private String accessKey;

    /*private MessageBirdClient messageBirdClient;

    @PostConstruct
    public void init() {
        MessageBirdService messageBirdService = new MessageBirdServiceImpl(accessKey);
        this.messageBirdClient = new MessageBirdClient(messageBirdService);
    }*/

    @Override
    public SMSResponse sendMessage(SMSRequest request) {
//        try {
//            com.messagebird.objects.Message message = messageBirdClient.sendMessage(
//                    "MessageBird",
//                    request.getContent(),
//                    List.of(request.getPhoneNumber())
//            );
//
//            return SMSResponse.success(message.getId());
//        } catch (Exception e) {
//            return SMSResponse.error(
//                    "MESSAGEBIRD_ERROR",
//                    e.getMessage()
//            );
//        }
        return null;
    }
}
