plugins {
    alias(libs.plugins.architectury.loom)
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
java.toolchain.languageVersion = JavaLanguageVersion.of(17)
group = "ru.vidtu.ias"
base.archivesName = "IAS-NeoForge-1.20.1"
description = "This mod allows you to change your logged in account in-game, without restarting Minecraft."
evaluationDependsOn(":1.20.1-root")
val shared = project(":1.20.1-root")

loom {
    silentMojangMappingsLicense()
    forge { // <- NeoForge in 1.20.1 is a fork of Forge, so we're using Forge platform.
        mixinConfigs = setOf("ias.mixins.json")
    }
    runs.named("client") {
        vmArgs(
            "-XX:+IgnoreUnrecognizedVMOptions",
            "-Xmx2G",
            "-XX:+AllowEnhancedClassRedefinition",
            "-XX:HotswapAgent=fatjar",
            "-Dfabric.debug.disableClassPathIsolation=true"
        )
        programArgs("--mixin", "ias.mixins.json")
    }
    @Suppress("UnstableApiUsage")
    mixin {
        useLegacyMixinAp = true
        defaultRefmapName = "ias.mixins.refmap.json"
    }
}

repositories {
    mavenCentral()
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    // Annotations (Compile)
    compileOnlyApi(libs.jetbrains.annotations)
    compileOnlyApi(libs.error.prone.annotations)

    // Minecraft (Provided)
    minecraft(libs.minecraft.mc1201)
    mappings(loom.officialMojangMappings())

    // NeoForge
    forge(libs.neoforge.mc1201) // <- NeoForge in 1.20.1 is a fork of Forge, so we're using Forge platform.

    // Root
    compileOnly(shared)
}

tasks.withType<JavaCompile> {
    source(rootProject.sourceSets.main.get().java)
    source(shared.sourceSets.main.get().java)
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-g", "-parameters"))
    options.release = 17
}

tasks.withType<ProcessResources> {
    from(rootProject.sourceSets.main.get().resources)
    from(shared.sourceSets.main.get().resources)
    inputs.property("version", project.version)
    filesMatching("META-INF/mods.toml") {
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
            "Implementation-Title" to "IAS-NeoForge-1.20.1",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "VidTu",
            "MixinConfigs" to "ias.mixins.json"
        )
    }
}
