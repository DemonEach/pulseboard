package org.goblintelligence.pulseboard.services.project.data.dto;

import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateNewProjectRequest {
    private String name;
    private String description;
    @Max(10)
    private String code;
    private Integer projectOwner;
    private List<ProjectAccountPermissions> accountPermissionsList;

    @Data
    @Builder
    public static class ProjectAccountPermissions {
        private Integer accountId;
        private String permission;
    }
}
