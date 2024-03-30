package com.projectpulse.projects.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.sql.Connection
import javax.sql.DataSource

class Db(
    postgresHost: String,
    postgresPort: Int,
    postgresDbName: String,
    postgresUsername: String,
    postgresPassword: String
) {
    private val jdbcUrl = "jdbc:postgresql://${postgresHost}:${postgresPort}/${postgresDbName}"

    private val dataSource by lazy {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = postgresUsername
        config.password = postgresPassword
        config.isAutoCommit = false
        val dataSource = HikariDataSource(config)

        migrateDatabase(dataSource)

        dataSource
    }

    private fun migrateDatabase(dataSource: DataSource) {
        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
    }


    fun connection(): Connection = dataSource.connection
}