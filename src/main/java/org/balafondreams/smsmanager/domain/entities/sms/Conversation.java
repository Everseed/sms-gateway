package org.balafondreams.smsmanager.domain.entities.sms;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "conversations")
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    private String name;

    @Column(nullable = false)
    private LocalDateTime lastMessageAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        lastMessageAt = LocalDateTime.now();
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
        lastMessageAt = message.getCreatedAt();
    }
}
