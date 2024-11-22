package org.balafondreams.smsmanager.domain.entities.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, String>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_KEY_LENGTH = 50;
    private static final int MAX_VALUE_LENGTH = 1000;

    @Override
    public String convertToDatabaseColumn(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        validateMap(map);
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Error converting map to JSON", e);
            return null;
        }
    }

    private void validateMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getKey().length() > MAX_KEY_LENGTH) {
                throw new IllegalArgumentException(
                        "Key length exceeds maximum allowed length of " + MAX_KEY_LENGTH
                );
            }
            if (entry.getValue() != null && entry.getValue().length() > MAX_VALUE_LENGTH) {
                throw new IllegalArgumentException(
                        "Value length exceeds maximum allowed length of " + MAX_VALUE_LENGTH
                );
            }
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            log.error("Error converting JSON to map", e);
            return new HashMap<>();
        }
    }
}