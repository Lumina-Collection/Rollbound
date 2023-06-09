import org.apache.groovy.util.Maps
import org.gradle.crypto.checksum.Checksum

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("org.gradle.crypto.checksum") version "1.4.0"
}

dependencies {
    implementation(project(":common"))
    implementation(libs.paper)
    implementation(libs.commandapi.shade)
    api(libs.axios)
    implementation(libs.venturechat)
}

tasks {
    processResources {
        outputs.upToDateWhen { false }
        filesMatching("**/*.yml") {
            val properties = Maps.of(
                "name", "Rollbound",
                "version", rootProject.extra.get("fullVersion"),
                "group", project.group
            )
            expand(properties)
        }
    }
    shadowJar {
        dependencies {
            include(dependency("net.luminacollection:.*"))
        }
        relocate("dev.jorel.commandapi", "software.axios.libs.commandapi")
        archiveFileName.set("Rollbound-Paper-${rootProject.extra.get("fullVersion")}.jar")
    }
    artifacts {
        archives(shadowJar)
    }
}
tasks.create("createChecksum", Checksum::class) {
    dependsOn("shadowJar")
    inputFiles.setFrom(tasks.get("shadowJar").outputs.files)
    outputDirectory.set(file("${project.buildDir}/checksums"))
    checksumAlgorithm.set(Checksum.Algorithm.SHA256)
    appendFileNameToChecksum.set(true)
}