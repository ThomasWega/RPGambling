plugins {
    `java-library`
    id("io.freefair.lombok") version "8.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("maven-publish")
}

group = "me.wega"
version = "1.0.0-SNAPSHOT"
description = "Gambling plugin"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri ("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://maven.citizensnpcs.co/repo") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.12")
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.2.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.github.NetzkroneHD:WGRegionEvents:v1.7.3")
    implementation("com.github.unldenis:Hologram-Lib:2.6.0")
    compileOnly("net.citizensnpcs:citizens-main:2.0.33-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("me.clip:placeholderapi:2.11.5")
}

tasks {
    build {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("RPGambling") {
            from(components["java"])
        }
    }
}