pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"

                // Lee el token secreto de tu local.properties
                val localProps = java.util.Properties()
                val propsFile = File(rootDir, "local.properties")
                if (propsFile.exists()) {
                    localProps.load(java.io.FileInputStream(propsFile))
                }
                password = localProps.getProperty("MAPBOX_SECRET_TOKEN") ?: ""
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "Civinews"
include(":app")
 