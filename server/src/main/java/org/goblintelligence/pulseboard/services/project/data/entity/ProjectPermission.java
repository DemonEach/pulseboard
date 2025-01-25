package org.goblintelligence.pulseboard.services.project.data.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Table(name = "project_permission")
public class ProjectPermission {

    @Id
    private UUID id;
    private UUID projectId;
    private Integer accountId;
    private String permission;
    private Instant creationTime;
    private Instant updateTime;
}
