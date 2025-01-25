pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

val keyProps = java.util.Properties().apply {
    file("apikey.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Fitness app"
include(":app", ":ui")

include(":localdatasource")
include(":localdatasourceroom")
include(":repository")
include(":repositoryimpl")
