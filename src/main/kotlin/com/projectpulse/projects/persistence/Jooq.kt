package com.projectpulse.projects.persistence

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.jooq.tools.LoggerListener
import java.sql.Connection

object Jooq {
    private val DEFAULT_CONFIGURATION =
        DefaultConfiguration()
            .set(SQLDialect.POSTGRES)
            .set(LoggerListener())

    val Connection.sql: DSLContext
        get() = DEFAULT_CONFIGURATION.derive(this).dsl()

    /**
     * Extracts a required field from the record.
     * This should be only used when being sure that the field is non-null in the query.
     * This throws an exception if a value is null as that case is a programming error.
     */
    fun <R : Record, A> R.require(field: Field<A?>): A =
        this.get(field).also {
            if (it == null) {
                throw NullPointerException("Expected field to be non-null {field: '$field', record: '$this'}")
            }
        }!!
}