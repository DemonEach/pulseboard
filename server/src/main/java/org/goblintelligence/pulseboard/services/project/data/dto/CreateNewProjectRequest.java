package org.goblintelligence.pulseboard.services.project.data.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateNewProjectRequest {
    @NotEmpty(message = "Project name cannot be empty!")
    private String name;
    private String description;
    @Max(10)
    @NotEmpty(message = "Code for project cannot empty!")
    private String code;
    @NotEmpty(message = "Project must have an owner!")
    private Integer projectOwner;
    private List<ProjectAccountPermissions> accountPermissionsList;

    @Valid
    @Data
    @Builder
    public static class ProjectAccountPermissions {
        private Integer accountId;
        private String permission;
    }
}
