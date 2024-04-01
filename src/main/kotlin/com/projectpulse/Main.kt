package com.projectpulse

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val printingApp: HttpHandler = DebuggingFilters.PrintRequest().then(app)

    val server = printingApp.asServer(SunHttp(9001)).start()

    println("Server started on " + server.port())
}