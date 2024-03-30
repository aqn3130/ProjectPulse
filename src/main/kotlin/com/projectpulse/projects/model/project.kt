package com.projectpulse.projects.model

import org.jooq.JSONB
import java.time.LocalDateTime

data class Project(
    val id: ProjectId,
    val projectname: String,
    val metadata: ProjectMetadata,
    val updatedAt: LocalDateTime,
    val createdAt: LocalDateTime,
)

data class ProjectId(val value: Int)

data class ProjectMetadata(
    val metadata: JSONB
)

data class ProjectWithUpdates(
    val project: Project,
    val updates: MutableList<Update>
)