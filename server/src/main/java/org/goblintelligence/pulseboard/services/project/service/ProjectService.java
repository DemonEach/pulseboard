package org.goblintelligence.pulseboard.services.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goblintelligence.pulseboard.services.project.data.dto.CreateNewProjectRequest;
import org.goblintelligence.pulseboard.services.project.data.dto.ProjectData;
import org.goblintelligence.pulseboard.services.project.data.dto.ProjectMapper;
import org.goblintelligence.pulseboard.services.project.data.entity.Project;
import org.goblintelligence.pulseboard.services.project.data.entity.ProjectPermission;
import org.goblintelligence.pulseboard.services.project.data.entity.ProjectWithPermissionProjection;
import org.goblintelligence.pulseboard.services.project.data.repository.ProjectPermissionsRepository;
import org.goblintelligence.pulseboard.services.project.data.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectPermissionsRepository projectPermissionsRepository;

    /**
     * Method to get available
     * @param accountId id of the account
     * @return list of {@link ProjectData}
     */
    public List<ProjectData> getAvailableProjects(Integer accountId) {
        List<ProjectWithPermissionProjection> projectWithPermissions = projectRepository.getAvailableProjectsByAccountId(accountId);

        return ProjectMapper.INSTANCE.fromProjectsWithPermissiontoProjectDatas(projectWithPermissions);
    }

    /**
     * Method to create new project, in project owner is the user who created it
     * @param newProjectRequest {@link CreateNewProjectRequest}
     */
    public void createProject(CreateNewProjectRequest newProjectRequest) {
        Instant now = Instant.now();

        Project newProject = Project.builder()
                .name(newProjectRequest.getName())
                .description(newProjectRequest.getDescription())
                .code(newProjectRequest.getCode())
                .owner(newProjectRequest.getProjectOwner())
                .creationTime(now)
                .updateTime(now)
                .build();

        newProject = projectRepository.save(newProject);
        log.debug("Saved new project in DB: {}", newProject);

        List<ProjectPermission> projectPermissions = new ArrayList<>();

        for (CreateNewProjectRequest.ProjectAccountPermissions accountPremissions : newProjectRequest.getAccountPermissionsList()) {
            ProjectPermission newPermission = ProjectPermission.builder()
                    .projectId(newProject.getId())
                    .accountId(accountPremissions.getAccountId())
                    .permission(accountPremissions.getPermission())
                    .creationTime(now)
                    .updateTime(now)
                    .build();

            projectPermissions.add(newPermission);
        }

        projectPermissionsRepository.saveAll(projectPermissions);
    }

    /**
     * Method for deleting project
     * @param projectId Id of the project
     */
    public void deleteProject(UUID projectId) {
        // TODO: also delete boards when they arrive
        projectPermissionsRepository.deleteAllByProjectId(projectId);
        projectRepository.deleteById(projectId);
    }
}
