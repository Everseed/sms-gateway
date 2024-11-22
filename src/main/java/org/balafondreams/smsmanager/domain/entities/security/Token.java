package org.balafondreams.smsmanager.domain.entities.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tokens")
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private boolean revoked;

    private LocalDateTime expiryDate;

    private LocalDateTime issuedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}