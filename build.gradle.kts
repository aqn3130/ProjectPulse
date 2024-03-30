import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    application
    id("dev.monosoul.jooq-docker") version "2.0.0"
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
    }
}

val http4kVersion: String by project
val http4kConnectVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project

application {
    mainClass = "com.projectpulse.ProjectPulseKt"
}

repositories {
    mavenCentral()
}

apply(plugin = "kotlin")


dependencies {
    implementation("org.http4k:http4k-contract:${http4kVersion}")
    implementation("org.http4k:http4k-core:${http4kVersion}")
    implementation("org.http4k:http4k-format-jackson:${http4kVersion}")
    implementation("org.http4k:http4k-security-oauth:${http4kVersion}")
    implementation("org.http4k:http4k-template-handlebars:${http4kVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.jooq:jooq:3.17.6")
    jooqCodegen("org.jooq:jooq-codegen:3.17.6")
    implementation("org.flywaydb:flyway-core:9.2.1")
    jooqCodegen("org.flywaydb:flyway-core:9.2.1")
    jooqCodegen("org.postgresql:postgresql:42.5.1")
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.postgresql:postgresql:42.5.1")

    testImplementation("org.http4k:http4k-testing-approval:${http4kVersion}")
    testImplementation("org.http4k:http4k-testing-hamkrest:${http4kVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")

}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            allWarningsAsErrors = false
            jvmTarget = "11"
            freeCompilerArgs += "-Xjvm-default=all"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = VERSION_11
        targetCompatibility = VERSION_11
    }

    generateJooqClasses {
        basePackageName.set("com.projectpulse.projects.persistence.schema")
        usingJavaConfig {
            name = "org.jooq.codegen.KotlinGenerator"
            generate
                .withGlobalCatalogReferences(false)
                .withGlobalSchemaReferences(false)
        }
    }

    build {
        dependsOn(generateJooqClasses)
    }
}