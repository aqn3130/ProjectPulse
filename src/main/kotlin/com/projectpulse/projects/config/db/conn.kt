package com.projectpulse.projects.config.db
import com.projectpulse.projects.persistence.Db

fun db(): Db {
    val dbHost = System.getenv("POSTGRES_DB_HOST") ?: "localhost"
    val dbPort = (System.getenv("POSTGRES_DB_PORT") ?: "5433").toInt()
    val dbName = System.getenv("POSTGRES_DB_NAME") ?: "project_tracker"
    val dbUser = System.getenv("POSTGRES_USER") ?: "postgres"
    val dbPassword = System.getenv("POSTGRES_PASSWORD") ?: "Secret321"

    return Db(dbHost, dbPort, dbName, dbUser, dbPassword)
}