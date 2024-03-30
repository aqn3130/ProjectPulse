package com.projectpulse.projects.model

import com.projectpulse.projects.model.ProjectId
import org.jooq.JSONB
import java.time.LocalDateTime

data class Update(
    val id: ProjectUpdateId,
    val projectName: String,
    val projectId: ProjectId,
    val metadata: UpdateMetadata,
    val updatedAt: LocalDateTime,
    val createdAt: LocalDateTime,
)

data class UpdateMetadata(
    val metadata: JSONB
)
data class ProjectUpdateId(val value: Int)