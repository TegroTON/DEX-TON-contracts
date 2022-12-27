import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
}

application {
    mainClass.set("money.tegro.dex.tool.Application")
}

micronaut {
    version("3.5.1")
    processing {
        incremental(true)
        annotations("money.tegro.dex.*")
    }
}

dependencies {
    implementation(libs.picocli)
    implementation(libs.reflect)
    implementation(libs.ton)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.reactor)

    implementation(libs.jackson)
    runtimeOnly(libs.bundles.data.runtime)

    implementation(libs.bundles.logging)
    runtimeOnly(libs.bundles.logging.runtime)

    implementation(libs.bundles.annotations)

    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.validation)
    implementation(libs.micronaut.picocli)

    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)

    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.http.validation)

    implementation(libs.micronaut.http.client)

    implementation(libs.micronaut.data.jdbc)

    implementation(projects.dex)
}

group = "money.tegro.dex"
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
