import net.minecrell.pluginyml.bukkit.BukkitPluginDescription


plugins {
    `java-library`
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.run.paper)
    idea
}

group = "me.nitkanikita21"
version = "1.0.0-SNAPSHOT"

allprojects {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
        maven("https://jitpack.io")
        maven("https://repo.extendedclip.com/releases/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://repo.negative.games/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/nitkanikita21/")
    }
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)


    library(libs.bundles.sponge.configurate)
    library(libs.bundles.cloud.framework)
    library(libs.bundles.registry)

    library(libs.vavr)
    library(libs.bstats.bukkit)

    compileOnly(libs.papi)
    compileOnly(libs.paper)
    compileOnly(libs.item.nbt.api)
    compileOnly(libs.packetevents)
}

idea {
    module {
        sourceDirs = sourceDirs + file("src/main/antlr")
        generatedSourceDirs = generatedSourceDirs + file("build/generated-src/antlr/java")
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val debugMode: Boolean = System.getenv("DEBUG")?.toBoolean() ?: false

/*tasks.shadowJar {
    val libsPackage = "${project.group}.${project.name.lowercase()}.libs"
    relocate("org.incendo.cloud", "$libsPackage.cloud")
    relocate("org.spongepowered", "$libsPackage.spongepowered")
    relocate("org.yaml.snakeyaml", "$libsPackage.snakeyaml")
    relocate("org.bstats", "$libsPackage.bstats")
    relocate("net.kyori", "$libsPackage.kyori")
    relocate("io.vavr", "$libsPackage.vavr")
    relocate("io.leangen", "$libsPackage.leangen")
    relocate("com.typesafe", "$libsPackage.typesafe")
}*/

tasks.runServer {
    minecraftVersion("1.21.1")

    downloadPlugins {
        modrinth("SimpleItemGenerator", "1.7.2")
    }
}

paper {
    main = "me.nitkanikita21.customblocks.CustomBlocksPlugin"
    apiVersion = "1.21"
    authors = listOf("nitkanikita21")
    loader = "me.nitkanikita21.customblocks.PluginLoader"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    serverDependencies {
        register("packetevents") {
            joinClasspath = true
        }
        register("NBTAPI") {
            joinClasspath = true
        }
        register("DisplayEntityUtils") {
            joinClasspath = true
        }
    }
    generateLibrariesJson = true
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(tasks.withType<AntlrTask>())
}

tasks.withType<Jar>().configureEach {
    dependsOn(tasks.withType<AntlrTask>())
}
