package org.goblintelligence.pulseboard.services.project.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.goblintelligence.pulseboard.services.project.data.dto.CreateNewProjectRequest;
import org.goblintelligence.pulseboard.services.project.data.dto.ProjectData;
import org.goblintelligence.pulseboard.services.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{accountId}")
    public ResponseEntity<List<ProjectData>> getAvailableProjects(
            @NotNull(message = "Account id cannot be empty!")
            @PathVariable Integer accountId) {
        return ResponseEntity.ok(projectService.getAvailableProjects(accountId));
    }

    @PostMapping
    public ResponseEntity<Void> createNewProject(@Valid @RequestBody CreateNewProjectRequest newProjectRequest) {
        projectService.createProject(newProjectRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@NotNull(message = "Project id cannot be empty!")
                                                     @PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }
}
