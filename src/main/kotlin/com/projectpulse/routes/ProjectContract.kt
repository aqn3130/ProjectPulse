package com.projectpulse.routes
import com.fasterxml.jackson.databind.JsonNode
import com.projectpulse.projects.config.db.db
import com.projectpulse.projects.model.Project
import com.projectpulse.projects.persistence.Jsonb
import com.projectpulse.projects.persistence.ProjectDao
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.json

object ProjectContract {
    private val spec = "/projects" meta {
        summary = "Returns all projects"
    } bindContract Method.GET

    private val projects: HttpHandler = {
        val projectDao = ProjectDao()
        val conn = db().connection()
        val allProjects = projectDao.findAllProjects(conn)
        val projects = jsonNodesProject(allProjects)
        Response(Status.OK)
            .with(Body.json().toLens() of Jackson.array(projects))
    }
    operator fun invoke(): ContractRoute = spec to projects
}

private fun jsonNodesProject(allProjects: List<Project>): MutableList<JsonNode> {
    val projects = mutableListOf<JsonNode>()
    allProjects.forEach { item ->
        val jsonObject = Jackson.obj(
            "id" to Jackson.number(item.id.value),
            "projectname" to Jackson.string(item.projectname),
            "metadata" to Jsonb.from(item.metadata.metadata),
            "updatedAt" to Jackson.string(item.updatedAt.toString()),
            "createdAt" to Jackson.string(item.createdAt.toString()),
        )
        projects.add(jsonObject)
    }
    return projects
}