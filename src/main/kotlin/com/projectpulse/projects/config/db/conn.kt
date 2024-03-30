package com.projectpulse.projects.config.db
import com.projectpulse.projects.persistence.Db

fun db(): Db {
    val dbHost = System.getenv("POSTGRES_DB_HOST") ?: "localhost"
    val dbPort = (System.getenv("POSTGRES_DB_PORT") ?: "5432").toInt()
    val dbName = System.getenv("POSTGRES_DB_NAME") ?: "projecttracker"
    val dbUser = System.getenv("POSTGRES_USER") ?: "aqn3130"
    val dbPassword = System.getenv("POSTGRES_PASSWORD") ?: ""

    return Db(dbHost, dbPort, dbName, dbUser, dbPassword)
}