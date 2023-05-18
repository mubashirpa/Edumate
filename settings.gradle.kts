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
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") // Sonatype Maven repository for Lottie
        maven(url = "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases") // Jitsi Maven repository
    }
}

rootProject.name = "Edumate"
include(":app")