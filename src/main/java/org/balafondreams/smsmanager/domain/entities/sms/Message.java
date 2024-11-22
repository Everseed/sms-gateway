package org.balafondreams.smsmanager.domain.entities.sms;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.template.MessageTemplate;
import org.balafondreams.smsmanager.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@Entity
@Table(name = "messages")
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    // Nouvelle relation avec MessageTemplate
    @ManyToOne
    @JoinColumn(name = "template_id")
    private MessageTemplate template;

    // Stockage des variables utilisées pour le template
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> templateVariables;

    // Code d'erreur en cas d'échec
    private String errorCode;

    // Message d'erreur détaillé
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = MessageStatus.PENDING;
        }
        if (type == null) {
            type = MessageType.SMS;
        }
    }

    // Méthodes utilitaires
    public boolean isPending() {
        return MessageStatus.PENDING.equals(status);
    }

    public boolean isSent() {
        return MessageStatus.SENT.equals(status);
    }

    public boolean isDelivered() {
        return MessageStatus.DELIVERED.equals(status);
    }

    public boolean isFailed() {
        return MessageStatus.FAILED.equals(status);
    }

    public boolean isScheduled() {
        return scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now());
    }

    public boolean canBeSent() {
        return isPending() && !isScheduled();
    }

    public boolean isTemplate() {
        return MessageType.TEMPLATE.equals(type);
    }
}