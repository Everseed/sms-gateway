---- Indexes pour optimiser les requêtes
--CREATE INDEX idx_user_username ON users(username);
--CREATE INDEX idx_user_email ON users(email);
--CREATE INDEX idx_user_enabled ON users(enabled);
--CREATE INDEX idx_user_roles ON user_roles(user_id, role_id);
--
--
--ALTER TABLE messages
--ADD COLUMN template_id BIGINT,
--ADD COLUMN template_variables jsonb,
--ADD CONSTRAINT fk_message_template
--    FOREIGN KEY (template_id)
--    REFERENCES message_templates(id);
--
--CREATE INDEX idx_message_template ON messages(template_id);
--
--ALTER TABLE messages
--ADD COLUMN error_code VARCHAR(50),
--ADD COLUMN error_message TEXT;
--
---- Index pour améliorer les performances des recherches
--CREATE INDEX idx_messages_status ON messages(status);
--CREATE INDEX idx_messages_created_at ON messages(created_at);
--CREATE INDEX idx_messages_scheduled_at ON messages(scheduled_at);
--CREATE INDEX idx_messages_phone_number ON messages(phone_number);
--
--CREATE INDEX idx_conversations_user_phone ON conversations(user_id, phone_number);
--CREATE INDEX idx_conversations_last_message ON conversations(last_message_at DESC);
--CREATE INDEX idx_conversations_name ON conversations(name);

-- Indices pour les performances
--CREATE INDEX idx_messages_user ON messages(user_id);
--CREATE INDEX idx_messages_conversation ON messages(conversation_id);
--CREATE INDEX idx_messages_template ON messages(template_id);
--CREATE INDEX idx_messages_status ON messages(status);
--CREATE INDEX idx_messages_phone ON messages(phone_number);
--CREATE INDEX idx_messages_created ON messages(created_at);
--
---- Index pour les recherches de messages programmés
--CREATE INDEX idx_messages_scheduled ON messages(scheduled_at)
--WHERE status = 'PENDING' AND scheduled_at IS NOT NULL;
--
---- Contraintes de validation
--ALTER TABLE messages ADD CONSTRAINT chk_messages_status
--    CHECK (status IN ('PENDING', 'SENT', 'DELIVERED', 'FAILED'));
--
--ALTER TABLE messages ADD CONSTRAINT chk_messages_type
--    CHECK (type IN ('SMS', 'MMS', 'TEMPLATE'));
--
---- Trigger pour mettre à jour last_message_at dans conversations
--CREATE OR REPLACE FUNCTION update_conversation_timestamp()
--RETURNS TRIGGER AS $$
--BEGIN
--    UPDATE conversations
--    SET last_message_at = NEW.created_at
--    WHERE id = NEW.conversation_id;
--    RETURN NEW;
--END;
--$$ LANGUAGE plpgsql;
--
--CREATE TRIGGER messages_update_conversation_timestamp
--    AFTER INSERT ON messages
--    FOR EACH ROW
--    EXECUTE FUNCTION update_conversation_timestamp();
/*
ALTER TABLE messages
ALTER COLUMN template_variables TYPE TEXT;
*/