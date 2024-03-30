package com.projectpulse.projects.persistence
import com.projectpulse.projects.model.Project
import com.projectpulse.projects.model.ProjectId
import com.projectpulse.projects.model.ProjectMetadata
import com.projectpulse.projects.persistence.Jooq.require
import com.projectpulse.projects.persistence.Jooq.sql
import com.projectpulse.projects.persistence.schema.tables.references.PROJECTS

import org.jooq.JSONB
import org.jooq.Record
import java.sql.Connection
import java.time.LocalDateTime

class ProjectDao {

    data class ProjectPayload(val projectName: String, val metadata: JSONB)
    fun addProject(
        body: ProjectPayload,
        conn: Connection
    ): ProjectId? = conn.sql.insertInto(PROJECTS, PROJECTS.PROJECT_NAME, PROJECTS.METADATA, PROJECTS.UPDATED_AT, PROJECTS.CREATED_AT)
        .values(body.projectName, JSONB.valueOf(body.metadata.toString()), LocalDateTime.now(), LocalDateTime.now())
        .returningResult(PROJECTS.ID)
        .fetchOne()
        ?.getValue(PROJECTS.ID)
        ?.let(::ProjectId)

    fun findAllProjects(conn: Connection): List<Project> = selectProjects(conn).map(::asProject)

    fun findProjectByName(conn: Connection, projectName: String): List<Project> = selectProject(conn, projectName).map(::asProject)

    private fun selectProject(conn: Connection, projectName: String) =
        conn.sql
            .select(
                PROJECTS.ID,
                PROJECTS.PROJECT_NAME,
                PROJECTS.METADATA,
                PROJECTS.UPDATED_AT,
                PROJECTS.CREATED_AT
            )
            .from(PROJECTS)
            .where(PROJECTS.PROJECT_NAME.eq(projectName))

    private fun selectProjects(conn: Connection) =
        conn
            .sql
            .select(
                PROJECTS.ID,
                PROJECTS.PROJECT_NAME,
                PROJECTS.METADATA,
                PROJECTS.UPDATED_AT,
                PROJECTS.CREATED_AT
            )
        .from(PROJECTS)

    fun updateProject(id: ProjectId, body: ProjectPayload, conn: Connection) : ProjectId {
        return conn.sql.update(PROJECTS)
            .set(PROJECTS.PROJECT_NAME, body.projectName)
            .set(PROJECTS.METADATA, body.metadata)
            .set(PROJECTS.UPDATED_AT, LocalDateTime.now())
            .where(PROJECTS.ID.eq(id.value))
            .execute()
            .let(::ProjectId)
    }

    fun deleteProject(id: ProjectId, conn: Connection): ProjectId {
        return conn.sql.delete(PROJECTS)
            .where(PROJECTS.ID.eq(id.value))
            .execute()
            .let(::ProjectId)
    }

    private fun asProject(it: Record) =
        Project(
            id = ProjectId(it.require(PROJECTS.ID)),
            projectname = it.require(PROJECTS.PROJECT_NAME),
            metadata = ProjectMetadata(it.require(PROJECTS.METADATA)),
            updatedAt = it.require(PROJECTS.UPDATED_AT),
            createdAt = it.require(PROJECTS.CREATED_AT)
        )
}