plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "xyz.tehbrian"
version = "0.1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

repositories {
    mavenCentral()
    mavenLocal() // required for craftbukkit dependency

    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://s01.oss.sonatype.org/content/groups/public/") {
        name = "sonatype-s01"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper:1.17.1-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        expand("version" to project.version)
    }

    shadowJar {
        archiveBaseName.set("BuildersReach")
    }
}
