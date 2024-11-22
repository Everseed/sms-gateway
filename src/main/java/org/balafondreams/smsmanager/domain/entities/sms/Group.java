package org.balafondreams.smsmanager.domain.entities.sms;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "groups")
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "group_contacts",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private Set<Contact> contacts = new HashSet<>();

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