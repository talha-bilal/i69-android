dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
//        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://github.com/QuickBlox/quickblox-android-sdk-releases/raw/master/")
        }
        maven {
            url = uri("https://storage.googleapis.com/r8-releases/raw")
        }
        maven {
            url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
            credentials {
                // Be sure to add these non-sensitive credentials in order to retrieve dependencies from
                // the private repository.
                username = "paypal_sgerritz"
                password = "AKCp8jQ8tAahqpT5JjZ4FRP2mW7GMoFZ674kGqHmupTesKeAY2G8NcmPKLuTxTGkKjDLRzDUQ"
            }
        }
    }
}

rootProject.name = "i69"
include(":app")
include(":chatkit")
include(":cropper")
