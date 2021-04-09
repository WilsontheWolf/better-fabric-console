plugins {
    id("fabric-loom") version "0.6-SNAPSHOT"
    `maven-publish`
    `java-library`
}

version = "0.2.0"
group = "org.chrisoft"
description = "Enables command history, auto completion, and syntax highlighting on the server console."

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

val minecraftVersion = "1.16.5"

dependencies {
    minecraft("com.mojang", "minecraft", minecraftVersion)
    mappings(minecraft.officialMojangMappings())
    modImplementation("net.fabricmc", "fabric-loader", "0.11.1")
    //modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.32.5+1.16")

    annotationProcessor("org.apache.logging.log4j", "log4j-core", "2.8.1")
    val jlineVersion = "3.12.1"
    implementation(include("org.jline", "jline", jlineVersion))
    implementation(include("org.jline", "jline-terminal-jansi", jlineVersion))
    implementation(include("org.fusesource.jansi", "jansi", "1.18"))

    implementation(include("net.kyori", "adventure-text-serializer-legacy", "4.7.0"))
    modImplementation(include("net.kyori", "adventure-platform-fabric", "4.0.0-SNAPSHOT"))
    modImplementation(include("ca.stellardrift", "confabricate", "2.0.3"))
    compileOnly("org.checkerframework", "checker-qual", "3.11.0")
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            mapOf(
                    "\${NAME}" to "JLine for Minecraft Dedicated Server",
                    "\${DESCRIPTION}" to project.description as String,
                    "\${VERSION}" to project.version as String
            ).forEach { (token, replacement) ->
                filter { it.replace(token, replacement) }
            }
        }
    }
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.toString()
    }
    jar {
        from("LICENSE")
    }
    remapJar {
        archiveFileName.set("${project.name}-mc$minecraftVersion-${project.version}.jar")
        archiveBaseName.set(project.name)
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(8)
    targetCompatibility = JavaVersion.toVersion(8)
    withSourcesJar()
}
