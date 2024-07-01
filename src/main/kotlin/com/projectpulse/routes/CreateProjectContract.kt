package com.projectpulse.routes

import com.projectpulse.projects.config.db.db
import com.projectpulse.projects.persistence.Jsonb
import com.projectpulse.projects.persistence.ProjectDao
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.mapper
import org.http4k.lens.Header
import org.slf4j.Logger
import org.slf4j.LoggerFactory
data class ProjectName(val projectname: String)

object CreateProjectContract {
    private val spec = "/projects" meta {
        summary = "Create a new project"
    } bindContract Method.POST

    private val createProject: HttpHandler = {
        addProject(it)
    }

    operator fun invoke(): ContractRoute = spec to createProject
}

fun addProject(request: Request): Response {
    val LOG: Logger = LoggerFactory.getLogger(CreateProjectContract::class.java)
    val db = db()
    val projectDao = ProjectDao()
    val projectName = Body.auto<ProjectName>().toLens()
    val body = Jackson.parse(request.bodyString())
    val projName = projectName(request)
    val metadata = Jsonb.to(body.get("metadata"))

    db.connection().use { conn ->
        val dbResponse = projectDao.addProject(ProjectDao.ProjectPayload(projName.projectname, metadata), conn)
        conn.commit()

        val responseDb = mapper.writeValueAsString(dbResponse)
        LOG.info("$responseDb added")

        return Response(Status.OK).with(Header.CONTENT_TYPE of ContentType.APPLICATION_JSON)
            .body(responseDb)
    }
}