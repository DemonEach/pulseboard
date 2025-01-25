package org.goblintelligence.pulseboard.services.project.data.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ProjectWithPermissionProjection {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private String permission;
    private Instant creationTime;
    private Instant updateTime;
}
