package com.projectpulse.projects.persistence

import com.projectpulse.projects.model.ProjectId
import com.projectpulse.projects.model.ProjectUpdateId
import com.projectpulse.projects.model.Update
import com.projectpulse.projects.model.UpdateMetadata
import com.projectpulse.projects.persistence.Jooq.require
import com.projectpulse.projects.persistence.Jooq.sql
import com.projectpulse.projects.persistence.schema.tables.references.UPDATES
import org.jooq.JSONB
import org.jooq.Record
import java.sql.Connection
import java.time.LocalDateTime

class ProjectUpdateDao {
    data class ProjectUpdatePayload(val projectId: Int, val projectName: String, val updateMetadata: JSONB)

    fun addProjectUpdate(
        body: ProjectUpdatePayload,
        conn: Connection
    ): ProjectUpdateId? = conn.sql.insertInto(UPDATES, UPDATES.PROJECT_NAME, UPDATES.PROJECT_ID, UPDATES.METADATA,
        UPDATES.UPDATED_AT, UPDATES.CREATED_AT)
        .values(body.projectName, body.projectId, JSONB.valueOf(body.updateMetadata.toString()), LocalDateTime.now(), LocalDateTime.now())
        .returningResult(UPDATES.ID)
        .fetchOne()
        ?.getValue(UPDATES.ID)
        ?.let(::ProjectUpdateId)

    fun getProjectUpdatesByProjectName(projectName: String, conn: Connection): List<Update> =
        selectUpdatesWithProject(conn)
            .where(UPDATES.projects().PROJECT_NAME.eq(projectName))
            .map(::asProjectUpdate)
    private fun selectUpdatesWithProject(conn: Connection) =
        conn.sql
            .select(
                UPDATES.ID,
                UPDATES.PROJECT_ID,
                UPDATES.PROJECT_NAME,
                UPDATES.METADATA,
                UPDATES.UPDATED_AT,
                UPDATES.CREATED_AT
            )
            .from(UPDATES)

    // An alternative way to get all updates by project-name
    fun findAllByProjectName(projectName: String, conn: Connection): List<Update> =
        conn.sql
            .select(
                UPDATES.ID,
                UPDATES.PROJECT_ID,
                // jOOQ can implicitly generate the needed joins, alternatively specify `.join(PROJECTS).on(PROJECTS.ID.eq(UPDATES.PROJECT_ID))`
                UPDATES.projects().PROJECT_NAME,
                UPDATES.METADATA,
                UPDATES.UPDATED_AT,
                UPDATES.CREATED_AT
            )
            .from(UPDATES)
            .where(UPDATES.projects().PROJECT_NAME.eq(projectName))
            .map(::asProjectUpdate)

    private fun asProjectUpdate(it: Record) =
        Update(
            id = ProjectUpdateId(it.require(UPDATES.ID)),
            projectId = ProjectId(it.require(UPDATES.PROJECT_ID)),
            projectName = it.require(UPDATES.PROJECT_NAME),
            metadata = UpdateMetadata(it.require(UPDATES.METADATA)),
            updatedAt = it.require(UPDATES.UPDATED_AT),
            createdAt = it.require(UPDATES.CREATED_AT)
        )
}