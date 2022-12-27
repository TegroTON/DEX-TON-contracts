import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
//    alias(libs.plugins.jib)
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

tasks.dockerfile {
    addFile("https://storage.yandexcloud.net/cloud-certs/CA.pem", "/root/.postgresql/root.crt")
}

//jib {
//    container {
//        jvmFlags = listOf(
//            // Flags to make the GC not suck as much; https://aikar.co/mcflags.html
//            "-XX:+UseG1GC",
//            "-XX:+ParallelRefProcEnabled",
//            "-XX:MaxGCPauseMillis=200",
//            "-XX:+UnlockExperimentalVMOptions",
//            "-XX:+DisableExplicitGC",
//            "-XX:+AlwaysPreTouch",
//            "-XX:G1NewSizePercent=30",
//            "-XX:G1MaxNewSizePercent=40",
//            "-XX:G1HeapRegionSize=8M",
//            "-XX:G1ReservePercent=20",
//            "-XX:G1HeapWastePercent=5",
//            "-XX:G1MixedGCCountTarget=4",
//            "-XX:InitiatingHeapOccupancyPercent=15",
//            "-XX:G1MixedGCLiveThresholdPercent=90",
//            "-XX:G1RSetUpdatingPauseTimePercent=5",
//            "-XX:SurvivorRatio=32",
//            "-XX:+PerfDisableSharedMem",
//            "-XX:MaxTenuringThreshold=1",
//        )
//    }
//}

dependencies {
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

    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)

    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.http.validation)
    kapt(libs.micronaut.openapi)

    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.http.server.netty)

    implementation(libs.micronaut.data.jdbc)
    implementation(libs.micronaut.flyway)

    implementation(libs.micronaut.micrometer.core)
    implementation(libs.micronaut.micrometer.annotations)
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
