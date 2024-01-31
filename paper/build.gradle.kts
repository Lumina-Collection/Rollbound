import org.apache.groovy.util.Maps

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

dependencies {
    implementation(project(":common"))
    implementation(libs.paper)
    implementation(libs.commandapi.shade)
    api(libs.axios)
    implementation(libs.venturechat)
    api(libs.carbon)
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