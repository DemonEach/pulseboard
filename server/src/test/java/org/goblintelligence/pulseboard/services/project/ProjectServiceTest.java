package org.goblintelligence.pulseboard.services.project;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.goblintelligence.pulseboard.PulseBoardApplicationTests;
import org.goblintelligence.pulseboard.services.auth.data.entity.Account;
import org.goblintelligence.pulseboard.services.auth.data.repository.AccountRepository;
import org.goblintelligence.pulseboard.services.project.data.dto.CreateNewProjectRequest;
import org.goblintelligence.pulseboard.services.project.data.dto.ProjectData;
import org.goblintelligence.pulseboard.services.project.data.entity.Project;
import org.goblintelligence.pulseboard.services.project.data.entity.ProjectPermission;
import org.goblintelligence.pulseboard.services.project.data.repository.ProjectPermissionsRepository;
import org.goblintelligence.pulseboard.services.project.data.repository.ProjectRepository;
import org.goblintelligence.pulseboard.services.project.service.ProjectService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.*;

@Slf4j
@DisplayName("Tests for project service")
public class ProjectServiceTest extends PulseBoardApplicationTests {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectPermissionsRepository projectPermissionsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    TransactionTemplate transactionTemplate;

    private Integer acc1Id;
    private Integer acc2Id;

    @BeforeEach
    void prepareData() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.excludeField(named("id").and(ofType(Integer.class)).and(inClass(Account.class)));
        EasyRandom easyRandom = new EasyRandom(parameters);

        var acc1 = easyRandom.nextObject(Account.class);
        var acc2 = easyRandom.nextObject(Account.class);

        accountRepository.save(acc1);
        accountRepository.save(acc2);

        acc1Id = acc1.getId();
        acc2Id = acc2.getId();
    }

    @AfterEach
    void cleanUp() {
        projectPermissionsRepository.deleteAll();
        projectRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Delete project")
    public void testDeleteProject() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.excludeField(named("id").and(ofType(UUID.class)).and(inClass(Project.class)));
        parameters.excludeField(named("id").and(ofType(UUID.class)).and(inClass(ProjectPermission.class)));
        parameters.randomize(FieldPredicates.named("code"), new StringRandomizer(10));
        EasyRandom easyRandom = new EasyRandom(parameters);

        Project testProject = easyRandom.nextObject(Project.class);
        testProject.setOwner(acc1Id); //required to test properly
        projectRepository.save(testProject);

        ProjectPermission projectPermissionAcc1 = easyRandom.nextObject(ProjectPermission.class);
        projectPermissionAcc1.setProjectId(testProject.getId());
        projectPermissionAcc1.setAccountId(acc1Id);
        projectPermissionsRepository.save(projectPermissionAcc1);

        ProjectPermission projectPermissionAcc2 = easyRandom.nextObject(ProjectPermission.class);
        projectPermissionAcc2.setProjectId(testProject.getId());
        projectPermissionAcc2.setAccountId(acc2Id);
        projectPermissionsRepository.save(projectPermissionAcc2);

        projectService.deleteProject(testProject.getId());

        Assertions.assertEquals(0, projectPermissionsRepository.count());
        Assertions.assertEquals(0, projectRepository.count());
    }

    @Test
    @DisplayName("Create new project")
    public void testCreateNewProject() {
        CreateNewProjectRequest createNewProjectRequest = createNewProjectRequest();
        projectService.createProject(createNewProjectRequest);

        Project project = Lists.newArrayList(projectRepository.findAll()).getFirst();

        Assertions.assertEquals(acc1Id, project.getOwner());
        Assertions.assertEquals(createNewProjectRequest.getDescription(), project.getDescription());
        Assertions.assertEquals(createNewProjectRequest.getName(), project.getName());
        Assertions.assertEquals(createNewProjectRequest.getCode(), project.getCode());

        List<ProjectPermission> projectPermissions = Lists.newArrayList(projectPermissionsRepository.findAll());

        assertThat(projectPermissions)
                .satisfiesExactlyInAnyOrder(
                        i -> {
                            assertThat(i.getAccountId()).isEqualTo(acc1Id);
                            assertThat(i.getPermission()).isEqualTo("OWNER");
                        },
                        i -> {
                            assertThat(i.getAccountId()).isEqualTo(acc2Id);
                            assertThat(i.getPermission()).isEqualTo("TEST");
                        }
                );
    }

    @Test
    @DisplayName("Get project (user does have permissions)")
    public void userHasProjectPermissions() {
        CreateNewProjectRequest createNewProjectRequest = createNewProjectRequest();
        projectService.createProject(createNewProjectRequest);

        CreateNewProjectRequest.ProjectAccountPermissions acc2Permission = createNewProjectRequest.getAccountPermissionsList()
                .stream()
                .filter(i -> i.getAccountId().equals(acc2Id))
                .findFirst()
                .orElse(null);
        assertThat(acc2Permission).isNotNull();

        List<ProjectData> projectData = projectService.getAvailableProjects(acc2Id);
        assertThat(projectData)
                .satisfiesExactlyInAnyOrder(
                        i -> {
                            assertThat(i.getName()).isEqualTo(createNewProjectRequest.getName());
                            assertThat(i.getDescription()).isEqualTo(createNewProjectRequest.getDescription());
                            assertThat(i.getCode()).isEqualTo(createNewProjectRequest.getCode());
                            assertThat(i.getPermission()).isEqualTo(acc2Permission.getPermission());
                        }
                );
    }

    private CreateNewProjectRequest createNewProjectRequest() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.randomize(FieldPredicates.named("code"), new StringRandomizer(10));
        EasyRandom easyRandom = new EasyRandom(parameters);

        CreateNewProjectRequest createNewProjectRequest = easyRandom.nextObject(CreateNewProjectRequest.class);
        createNewProjectRequest.setProjectOwner(acc1Id);
        var permissionAcc1 = CreateNewProjectRequest.ProjectAccountPermissions.builder()
                .accountId(acc1Id)
                .permission("OWNER")
                .build();
        var permissionAcc2 = CreateNewProjectRequest.ProjectAccountPermissions.builder()
                .accountId(acc2Id)
                .permission("TEST")
                .build();

        createNewProjectRequest.setAccountPermissionsList(List.of(permissionAcc1, permissionAcc2));

        return createNewProjectRequest;
    }

    @Test
    @DisplayName("Get project (user doesn't have permissions)")
    public void userHasNoProjectPermissions() {
        EasyRandom easyRandom = new EasyRandom();

        projectService.createProject(createNewProjectRequest());

        List<ProjectData> projectData = projectService.getAvailableProjects(easyRandom.nextInt());
        assertThat(projectData).isEmpty();
    }
}
