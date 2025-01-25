package org.goblintelligence.pulseboard.services.project.data.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Table(name = "project")
public class Project {

    @Id
    private UUID id;
    private Integer owner;
    private String code;
    private String name;
    private String description;
    private Instant creationTime;
    private Instant updateTime;
}
