package com.projectpulse.projects.persistence

import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.jooq.JSONB

object Json {
    val MAPPER =
        ObjectMapper()
            .configure(FAIL_ON_NULL_FOR_PRIMITIVES, true)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(FAIL_ON_IGNORED_PROPERTIES, false)
            .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
            .configure(USE_BIG_INTEGER_FOR_INTS, true)
            .registerKotlinModule()


    inline fun <reified T : Any> from(value: String): T = MAPPER.readValue(value, T::class.java)

    fun to(value: Any): String = MAPPER.writeValueAsString(value)

}

object Jsonb {
    inline fun <reified T : Any> from(value: JSONB): T = Json.from(value.data())

    fun to(value: Any): JSONB = JSONB.valueOf(Json.to(value))
}