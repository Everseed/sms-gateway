package org.balafondreams.smsmanager.domain.entities.template;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "message_templates")
@NoArgsConstructor
public class MessageTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String description;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TemplateCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TemplateVariable> variables = new HashSet<>();

    @OneToMany(mappedBy = "template")
    private Set<Message> messages = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
