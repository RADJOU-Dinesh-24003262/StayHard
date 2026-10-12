pluginManagement {
    // Fix for AGP AndroidLocationsException when both ANDROID_PREFS_ROOT and ANDROID_USER_HOME are set in system environment
    try {
        val processEnvClass = Class.forName("java.lang.ProcessEnvironment")
        val theEnvironmentField = processEnvClass.getDeclaredField("theEnvironment")
        theEnvironmentField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val envMap = theEnvironmentField.get(null) as MutableMap<String, String>
        envMap.remove("ANDROID_PREFS_ROOT")

        val theCaseInsensitiveEnvironmentField = processEnvClass.getDeclaredField("theCaseInsensitiveEnvironment")
        theCaseInsensitiveEnvironmentField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val caseInsensitiveEnvMap = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
        caseInsensitiveEnvMap.remove("ANDROID_PREFS_ROOT")
    } catch (_: Exception) {
    }

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
    }
}

rootProject.name = "StayHard"
include(":app")
 