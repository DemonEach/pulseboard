package org.goblintelligence.pulseboard.services.project.data.repository;

import org.goblintelligence.pulseboard.services.project.data.entity.ProjectPermission;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProjectPermissionsRepository extends CrudRepository<ProjectPermission, UUID> {

    @Modifying
    @Query(value = """
                delete from project_permission where project_id = :projectId
            """)
    void deleteAllByProjectId(UUID projectId);
}
