import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin.allopen)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
}

application {
    mainClass.set("money.tegro.dex.Application")
}

micronaut {
    version("3.5.1")
    processing {
        incremental(true)
        annotations("money.tegro.dex.*")
    }
}

dependencies {
    kapt(libs.micronaut.data.processor)
    kapt(libs.picocli.codegen)
    kapt(libs.micronaut.http.validation)
    kapt(libs.micronaut.openapi)

    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.http.server.netty)
    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.data.r2dbc)
    implementation(libs.micronaut.flyway)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)
    implementation(libs.jakarta.annotation)
    implementation(libs.swagger.annotations)
    implementation(libs.reflect)
    implementation(libs.logging)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.reactor)

    runtimeOnly(libs.r2dbc.pool)
    runtimeOnly(libs.r2dbc.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.logback.core)
    runtimeOnly(libs.logback.classic)
    implementation(libs.logstash.logback.encoder)

    implementation(libs.micronaut.validation)
    implementation(libs.ton)

    implementation(libs.jackson)
}

group = "money.tegro"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
