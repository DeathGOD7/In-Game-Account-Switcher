plugins {
    alias(libs.plugins.architectury.loom)
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21
java.toolchain.languageVersion = JavaLanguageVersion.of(21)
group = "ru.vidtu.ias"
base.archivesName = "IAS-Fabric-1.20.6"
description = "This mod allows you to change your logged in account in-game, without restarting Minecraft."
evaluationDependsOn(":1.20.6-root")
val shared = project(":1.20.6-root")

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.nucleoid.xyz/") // TODO: Remove when ModMenu fixes dependency issues.
}

loom {
    silentMojangMappingsLicense()
    runs.named("client") {
        vmArgs(
            "-XX:+IgnoreUnrecognizedVMOptions",
            "-Xmx2G",
            "-XX:+AllowEnhancedClassRedefinition",
            "-XX:HotswapAgent=fatjar",
            "-Dfabric.debug.disableClassPathIsolation=true"
        )
    }
    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName = "ias.mixins.refmap.json"
    }
}

dependencies {
    // Annotations (Compile)
    compileOnlyApi(libs.jetbrains.annotations)
    compileOnlyApi(libs.error.prone.annotations)

    // Minecraft (Provided)
    minecraft(libs.minecraft.mc1206)
    mappings(loom.officialMojangMappings())

    // Fabric
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.mc1206)
    modImplementation(libs.modmenu.mc1206)

    // Root
    compileOnly(shared)
}

tasks.withType<JavaCompile> {
    source(rootProject.sourceSets.main.get().java)
    source(shared.sourceSets.main.get().java)
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-g", "-parameters"))
    options.release = 21
}

tasks.withType<ProcessResources> {
    from(rootProject.sourceSets.main.get().resources)
    from(shared.sourceSets.main.get().resources)
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<Jar> {
    from(rootDir.resolve("LICENSE"))
    from(rootDir.resolve("GPL"))
    from(rootDir.resolve("NOTICE"))
    manifest {
        attributes(
            "Specification-Title" to "In-Game Account Switcher",
            "Specification-Version" to project.version,
            "Specification-Vendor" to "VidTu",
            "Implementation-Title" to "IAS-Fabric-1.20.6",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "VidTu"
        )
    }
}
