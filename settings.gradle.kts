pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HabitTracker"

include(":app")
include(":core:ui")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":feature:habits")
include(":feature:reminders")
include(":feature:analytics")
include(":feature:usagestats")
