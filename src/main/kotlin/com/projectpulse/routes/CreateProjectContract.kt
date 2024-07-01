package com.projectpulse.routes

import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status

object CreateProjectContract {
    private val spec = "/projects" meta {
        summary = "Create a new project"
    } bindContract Method.POST

    private val createProject: HttpHandler = {
        Response(Status.OK)
            .body("Create a new project")
    }

    operator fun invoke(): CreateProjectContract = spec to createProject
}