package org.goblintelligence.pulseboard.services.project.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Data
public class ProjectData {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private String permission;
    private Instant creationTime;
    private Instant updateTime;
}
