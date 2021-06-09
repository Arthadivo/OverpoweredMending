plugins {
    id("fabric-loom")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(minecraft.officialMojangMappings())
    modImplementation(libs.bundles.fabric)
    implementation(project(":common"))

    modImplementation(libs.modmenu) {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from("src/main/resources", "../common/src/main/resources")

        filesMatching("fabric.mod.json") {
            expand(project.properties)
        }
    }

    compileJava {
        options.encoding = "UTF-8"
        options.isDeprecation = true
        options.release.set(16)
    }
}

loom {
    val modId: String by project
    refmapName = "fabric.${modId}.refmap.json"

    runs {
        create("overpoweredMendingClient") {
            client()

            property("fabric.log.level", "debug")
            vmArg("-XX:+ShowCodeDetailsInExceptionMessages")
        }
        create("overpoweredMendingServer") {
            server()

            property("fabric.log.level", "debug")
            vmArg("-XX:+ShowCodeDetailsInExceptionMessages")
        }
    }
}