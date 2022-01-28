plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.3"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "xyz.tehbrian"
version = "0.1.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.thbn.me/snapshots/") {
        name = "thbn-snapshots"
    }
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    implementation("com.google.inject:guice:5.1.0")
    implementation("org.spongepowered:configurate-yaml:4.1.2")

    implementation("dev.tehbrian:tehlib-paper:0.1.0-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        expand("version" to project.version)
    }

    shadowJar {
        archiveBaseName.set("BuildersReach")
        archiveClassifier.set("")
    }

    runServer {
        minecraftVersion("1.18.1")
    }
}
