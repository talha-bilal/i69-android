//import Others.exoplayer
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
    id("com.apollographql.apollo3")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id ("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

hilt {
    enableAggregatingTask = true
}

android {
    namespace = "com.i69"
    compileSdk = 35

    signingConfigs {
        create("release") {
//            storeFile = file("C:\\Users\\theze\\StudioProjects\\i69\\jks\\i69sasu.jks")
//            storeFile = file("/Users/amanarora/Documents/workspace_matheiu_holding/i69/jks/i69sasu.jks")
            storeFile = file("../jks/i69sasu.jks")
            // storeFile = file("C:\\Users\\hp\\Downloads\\i69_keystore (10).jks")
            storePassword = "Trs@Yv*BJ46L"
            keyAlias = "i69sasu"
            keyPassword = "Trs@Yv*BJ46L"
        }
        getByName("debug") {

        }
    }

//    signingConfigs {
//        create("release") {
//            storeFile = file("C:\\Users\\theze\\StudioProjects\\i69\\jks\\i69sasu.jks")
//            storeFile = file("/Users/amanarora/Documents/workspace_matheiu_holding/i69/jks/i69sasu.jks")

//            storeFile = file("/Users/abdulrehman/Projects/Yann/2024_06_15_i69/i69/jks/i69sasu.jks")
//            storeFile = file("../jks/i69sasu.jks")
    // storeFile = file("C:\\Users\\hp\\Downloads\\i69_keystore (10).jks")
//            storePassword = "Trs@Yv*BJ46L"
//            keyAlias = "i69sasu"
//            keyPassword = "Trs@Yv*BJ46L"
//        }
//        getByName("debug") {
//        }
//    }


//    splits {
//        abi {
//            isEnable = true
//            reset()
//            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
//            isUniversalApk = false
//        }
//    }

//    defaultConfig {
//        applicationId = Android.appId
//        minSdk = Android.minSdk
//        targetSdk = Android.targetSdk
//        versionCode = Android.versionCode
//        versionName = Android.versionName
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
////        archivesName = "i69-v$versionCode($versionName)"
//
////        signingConfig = signingConfigs.getByName("release")
//        multiDexEnabled = true
//    }


    defaultConfig {
        applicationId = "com.i69.isyxtinine"
        minSdk = 21
        targetSdk = 35
        versionCode = 381
        versionName = "64.9.181"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        archivesName = "i69-v$versionCode($versionName)"

        signingConfig = signingConfigs.getByName("release")
        multiDexEnabled = true
    }

//    buildTypes {
//        release {
////            isMinifyEnabled = true
////            proguardFiles(
////                getDefaultProguardFile("proguard-android-optimize.txt"),
////                "proguard-rules.pro"
////            )
//            /* isMinifyEnabled=true0
//             isShrinkResources=true*/
//
////            signingConfig = signingConfigs.getByName("release")
//            isDebuggable = false
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
////            ndk {
////                debugSymbolLevel = "FULL"
////            }
//            ndk.debugSymbolLevel = "FULL"
//
//            /*buildConfigField("String", "BASE_URL", "\"https://api.i69app.com/\"")
//            buildConfigField("String", "BASE_URL_REP", "\"http://95.216.208.1:8000/\"")
//            buildConfigField(
//                "String",
//                "BASE_URL_WEB_SOCKET",
//                "\"wss://api.chatadmin-mod.click/ws/graphql\""
//            )*/
//        }


    buildTypes {
        release {
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
            /* isMinifyEnabled=true0
             isShrinkResources=true*/

            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk {
                debugSymbolLevel = "FULL"
            }

            /*buildConfigField("String", "BASE_URL", "\"https://api.i69app.com/\"")
            buildConfigField("String", "BASE_URL_REP", "\"http://95.216.208.1:8000/\"")
            buildConfigField(
                "String",
                "BASE_URL_WEB_SOCKET",
                "\"wss://api.chatadmin-mod.click/ws/graphql\""
            )*/
        }

//        debug {
//            isDebuggable = true
//            isMinifyEnabled = true
//            isShrinkResources = true
////            signingConfig = signingConfigs.getByName("release")
//        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        flavorDimensions += "default"
        productFlavors {

//            create("staging") {
//                applicationId = "com.i69.isyxtinine"
//                dimension = "default"
//                //applicationIdSuffix = ".demo"
//                //versionNameSuffix = "-demo"
//                // /*test url*/
////                buildConfigField("String", "BASE_URL", "\"https://api.i69app.com/\"")
//                buildConfigField("String", "BASE_URL", "\"https://api.chatadmin-mod.click/\"")
//                buildConfigField("String", "BASE_URL_MARKET", "\"https://stesting-sslonline.online/\"")
//                // buildConfigField("String", "BASE_URL", "\"https://api.i69app.com/\"")
//                //Staging
//                buildConfigField("String", "BASE_URL_REP", "\"http://95.216.208.1:8000/\"")
//                buildConfigField("String", "BASE_URL_WEB_SOCKET", "\"wss://api.chatadmin-mod.click/ws/graphql\"")
//
//                buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyBNDQFHOXjOH-AJH_tvgd7FM_IxLNClDRk\"")
//                buildConfigField("boolean", "USE_S3", "true")
//                buildConfigField("String", "APPLICATION_ID", "\"$applicationId\"")
//
//                /*   buildConfigField("String ", "BASE_URL", "\"https://api.i69app.com\"")
//                   buildConfigField("String", "BASE_URL_REP", "\"http://95.216.208.1:8000/\"")
//                   buildConfigField(
//                       "String",
//                       "BASE_URL_WEB_SOCKET",
//                       "\"wss://api.i69app.com/ws/graphql\""
//                   )*/
//            }
            create("production") {
                applicationId = "com.i69.isyxtinine"
                dimension = "default"
                /* live url*//*    buildConfigField("String", "BASE_URL", "\"https://api.chatadmin-mod.click/\"")  //Staging
                      buildConfigField("String", "BASE_URL_REP", "\"http://95.216.208.1:8000/\"")
                      buildConfigField("String", "BASE_URL_WEB_SOCKET", "\"wss://api.chatadmin-mod.click/ws/graphql\"")
*/

//                STAGING SERVER
//                buildConfigField("String", "BASE_URL", "\"https://api.chatadmin-mod.click/\"")
//                buildConfigField("String", "BASE_URL_WEB_SOCKET", "\"wss://api.chatadmin-mod.click/ws/graphql\"")
//                buildConfigField("boolean", "USE_S3", "true")

//                Live Server
                buildConfigField("String", "BASE_URL", "\"https://api.i69app.com/\"")
                buildConfigField("String", "BASE_URL_WEB_SOCKET", "\"wss://api.i69app.com/ws/graphql\"")
                buildConfigField("boolean", "USE_S3", "false")  // for production


                buildConfigField("String", "BASE_URL_MARKET", "\"https://stesting-sslonline.online/\"")
//                buildConfigField("String", "BASE_URL_MARKET", "\"https://stesting-sslonline.online/i69/graphql\"")
//                buildConfigField("String", "BASE_URL_MARKET", "\"https://stesting-sslonline.online/aliexpress/graphql/\"")

                buildConfigField("String", "BASE_URL_REP", "\"http://95.216.208.1:8000/\"")


                buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyBNDQFHOXjOH-AJH_tvgd7FM_IxLNClDRk\"")

                buildConfigField("String", "APPLICATION_ID", "\"$applicationId\"")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    viewBinding {
        enable = true
    }

    dataBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes += "META-INF/library_release.kotlin_module"
        jniLibs {
            useLegacyPackaging = true
        }
    }

//    kapt {
//        this.correctErrorTypes = false
//    }

    lint {
        //isAbortOnError = true
        abortOnError = false
        //isExplainIssues = true
        baseline = file("lint-baseline.xml")
    }
    ndkVersion = "25.1.8937393"
}

dependencies {

    // Kotlin
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }

    // UI
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-runtime-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Firebase
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-crashlytics:19.3.0")
    implementation("com.google.firebase:firebase-messaging:24.1.0")

    // Login
    implementation("com.facebook.android:facebook-login:17.0.0")
//    implementation("com.facebook.android:facebook-android-sdk:17.0.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")


    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")

    // Lottie
    implementation("com.airbnb.android:lottie:6.4.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")

    // Google Libraries
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.android.billingclient:billing-ktx:7.1.1")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Expandable Layout
    implementation("com.github.cachapa:ExpandableLayout:2.9.2")

    // Chat UI
    implementation(project(":chatkit"))

    implementation("io.ak1.pix:piximagepicker:1.6.3")
    implementation("com.github.sayyam:carouselview:d46b3c394f")

    // Worker
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Local Unit Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")

    // Instrumentation Tests
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

//    implementation(Others.media3Session)
    implementation("androidx.media3:media3-exoplayer:1.5.1")
//    implementation(Others.media3Dash)
    implementation("androidx.media3:media3-ui:1.4.0") {
        exclude("com.google.android.exoplayer2")
        exclude("android.support.v4")
    }

    //PayPal
    implementation("com.paypal.checkout:android-sdk:1.3.0")

    //Google Maps
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("androidx.multidex:multidex:2.0.1")

    // The core runtime dependencies
    implementation("com.apollographql.apollo3:apollo-runtime:3.2.2")
    implementation("com.apollographql.apollo3:apollo-api:3.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.6")

    //sdp
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    //circularprogressindicator
    implementation("com.github.jakob-grabner:Circle-Progress-View:1.4")

    //blurimage
    implementation("jp.wasabeef:blurry:4.0.1")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    implementation("com.github.skydoves:powermenu:2.2.2")

    implementation("com.stripe:stripe-android:20.19.0")


    implementation("com.google.android.gms:play-services-auth:21.3.0")
//payment
    implementation("com.google.android.gms:play-services-wallet:19.4.0")
    implementation("com.google.android.gms:play-services-pay:16.5.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.20"))

    implementation("com.github.3llomi:RecordView:3.1.3")

    implementation ("com.github.AbedElazizShe:LightCompressor:1.3.3")
    api("com.otaliastudios:cameraview:2.7.2")

    implementation(project(":cropper"))
//    implementation("com.arthenica:mobile-ffmpeg-full:4.4")
//    implementation("com.arthenica:mobile-ffmpeg-video:4.4")
    implementation("com.github.chrisbanes:PhotoView:2.0.0")

    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.dafruits:webrtc:123.0.0")
//    implementation("org.webrtc:google-webrtc:1.0.32006")
    implementation("androidx.preference:preference:1.2.1")

}

apollo {
    mapScalarToUpload("Upload")
    packageName.set("com.i69")
    generateKotlinModels.set(true)
}
