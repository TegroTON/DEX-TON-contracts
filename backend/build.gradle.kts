import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
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
    implementation(libs.reflect)
    implementation(libs.ton)

    implementation(libs.bundles.coroutines)

    implementation(libs.jackson)
    runtimeOnly(libs.bundles.data.runtime)

    implementation(libs.bundles.logging)
    runtimeOnly(libs.bundles.logging.runtime)

    implementation(libs.bundles.annotations)


    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.validation)

    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.http.validation)
    kapt(libs.micronaut.openapi)

    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.http.server.netty)

    implementation(libs.micronaut.data.r2dbc)
    implementation(libs.micronaut.flyway)

    implementation(libs.micronaut.micrometer.core)
    implementation(libs.micronaut.micrometer.annotations)
}

group = "money.tegro"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
