import org.apache.groovy.util.Maps

plugins {
    id("com.gradleup.shadow") version "9.+"
    id("io.papermc.paperweight.userdev") version "2.+"
}

dependencies {
    paperweight.paperDevBundle(libs.paper.get().version)
    implementation(project(":common"))
    implementation(libs.commandapi.shade)
    api(libs.axios)
    implementation(libs.venturechat)
    api(libs.carbon)
}
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
tasks {
    processResources {
        outputs.upToDateWhen { false }
        filesMatching("**/*.yml") {
            val properties = Maps.of(
                "name", "Rollbound",
                "version", rootProject.extra.get("fullVersion"),
                "group", "net.luminacollection"
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