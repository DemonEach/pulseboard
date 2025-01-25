package org.goblintelligence.pulseboard.services.project.data.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

import java.time.Instant;

@Data
@Builder
public class ProjectPremissions {
    private UUID id;
    private UUID projectId;
    private Integer accountId;
    private String permission;
    private Instant creationTime;
    private Instant updateTime;
}
