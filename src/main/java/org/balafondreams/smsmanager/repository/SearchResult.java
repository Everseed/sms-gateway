package org.balafondreams.smsmanager.repository;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
@Builder
public class SearchResult<T> {
    private Page<T> content;
    private Map<String, Object> stats;
}
