package org.goblintelligence.pulseboard.services.project.data.repository;

import org.goblintelligence.pulseboard.services.project.data.entity.Project;
import org.goblintelligence.pulseboard.services.project.data.entity.ProjectWithPermissionProjection;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends CrudRepository<Project, UUID> {

    @Query(value = """
                select p.id, p.name, p.code, p.description, pp.permission, pp.creation_time, pp.update_time from project p 
                    join project_permission pp on pp.project_id = p.id
                where pp.account_id = :accountId
            """)
    List<ProjectWithPermissionProjection> getAvailableProjectsByAccountId(Integer accountId);
}
