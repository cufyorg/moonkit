dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "monkt"

include(":bson")
include(":codec")
include(":coroutines")
include(":op")
include(":orm")
