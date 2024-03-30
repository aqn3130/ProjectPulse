package com.projectpulse

import com.fasterxml.jackson.databind.JsonNode
import com.projectpulse.formats.JacksonMessage
import com.projectpulse.formats.jacksonMessageLens
import com.projectpulse.models.HandlebarsViewModel
import com.projectpulse.projects.config.db.db
import com.projectpulse.projects.model.Project
import com.projectpulse.projects.persistence.Jsonb
import com.projectpulse.projects.persistence.ProjectDao
import com.projectpulse.routes.ExampleContractRoute
import org.http4k.client.JavaHttpClient
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.format.Jackson.json
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.google
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

// Google OAuth Example
// Browse to: http://localhost:9000/oauth - you'll be redirected to google for authentication
val googleClientId = "myGoogleClientId"
val googleClientSecret = "myGoogleClientSecret"

// this is a test implementation of the OAuthPersistence interface, which should be
// implemented by application developers
val oAuthPersistence = InsecureCookieBasedOAuthPersistence("Google")

// pre-defined configuration exist for common OAuth providers
val oauthProvider = OAuthProvider.google(
        JavaHttpClient(),
        Credentials(googleClientId, googleClientSecret),
        Uri.of("http://localhost:9000/oauth/callback"),
        oAuthPersistence
)
val filters = DebuggingFilters.PrintRequestAndResponse().then(ServerFilters.CatchAll())

val app: HttpHandler = routes(
    "/home" bind GET to {
        Response(OK).body("Welcome to Project Pulse")
    },

    "/formats/json/jackson" bind GET to {
        Response(OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
    },

    "/templates/handlebars" bind GET to {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = HandlebarsViewModel("Welcome to Project Pulse!")
        Response(OK).with(view of viewModel)
    },

    "/testing/hamkrest" bind GET to {request ->
        Response(OK).body("Echo '${request.bodyString()}'")
    },

    "/contract/api/v1" bind contract {
        renderer = OpenApi3(ApiInfo("ProjectPulse API", "v1.0"))
    
        // Return Swagger API definition under /contract/api/v1/swagger.json
        descriptionPath = "/swagger.json"
    
        // You can use security filter tio protect routes
        security = ApiKeySecurity(Query.int().required("api"), { it == 42 }) // Allow only requests with &api=42
    
        // Add contract routes
        routes += ExampleContractRoute()
    },

    "/oauth" bind routes(
            "/" bind GET to oauthProvider.authFilter.then { Response(OK).body("hello!") },
            "/callback" bind GET to oauthProvider.callback
    ),
    "/projects" bind GET to {
        val projectDao = ProjectDao()
        val conn = db().connection()
        val allProjects = projectDao.findAllProjects(conn)
        val projects = jsonNodesProject(allProjects)
        Response(Status.OK)
            .with(Body.json().toLens() of Jackson.array(projects))
    }

).withFilter(filters)

private fun jsonNodesProject(allProjects: List<Project>): MutableList<JsonNode> {
    val projects = mutableListOf<JsonNode>()
    for (item in allProjects) {
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
fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(SunHttp(9001)).start()

    println("Server started on " + server.port())
}
