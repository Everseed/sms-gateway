package org.balafondreams.smsmanager.domain.entities.template;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "template_variables")
@NoArgsConstructor
public class TemplateVariable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String key;

    private String defaultValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VariableType type;

    private String description;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private MessageTemplate template;

    public enum VariableType {
        TEXT,
        NUMBER,
        DATE,
        BOOLEAN,
        CHOICE
    }
}
