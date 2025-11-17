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
    }
}

rootProject.name = "EasyIPC"
include(":easyipc_core")
include(":easyipc_processor")
include(":easyipc_annotations")
include(":easyipc_transport_aidl")
include(":easyipc_transport_aidl_client")
include(":demoserver")
include(":democlient")
include(":easyipc_baseclient")
include(":easyipc_baseserver")
include(":easyipc_transport_contentprovider")
include(":easyipc_transport_contentprovider_client")
