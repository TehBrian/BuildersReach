plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.5.0"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.kyori.indra.checkstyle") version "3.0.1"
    id("com.github.ben-manes.versions") version "0.45.0"
}

group = "xyz.tehbrian"
version = "0.2.0"
description = "Build from afar."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.thbn.me/releases/")
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    implementation("com.google.inject:guice:5.1.0")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("dev.tehbrian:tehlib-paper:0.4.2")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        expand("version" to project.version, "description" to project.description)
    }

    shadowJar {
        val libsPackage = "xyz.tehbrian.buildersreach.libs"
        relocate("com.google.inject", "$libsPackage.guice")
        relocate("org.spongepowered.configurate", "$libsPackage.configurate")
        relocate("dev.tehbrian.tehlib", "$libsPackage.tehlib")
        relocate("cloud.commandframework", "$libsPackage.cloud")
    }

    runServer {
        minecraftVersion("1.19.3")
    }
}
