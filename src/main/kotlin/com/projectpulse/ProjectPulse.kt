package com.projectpulse

import com.projectpulse.formats.JacksonMessage
import com.projectpulse.formats.jacksonMessageLens
import com.projectpulse.models.HandlebarsViewModel
import com.projectpulse.routes.ExampleContractRoute
import org.http4k.client.JavaHttpClient
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
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
    )
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(SunHttp(9001)).start()

    println("Server started on " + server.port())
}
