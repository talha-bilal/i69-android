# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/chris/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascriptToInject.interface.for.webview {
#   public *;
#}

-keepclassmembers,allowobfuscation class * {
@com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**


## keep Enum in Response Objects
-keepclassmembers enum com.android.services.** { *; }


## Note not be needed unless some model classes don't implement Serializable interface
## Keep model classes used by ORMlite
-keep class com.android.model.**


## keep classes and class members that implement java.io.Serializable from being removed or renamed
## Fixes "Class class com.twinpeek.android.model.User does not have an id field" execption
-keep class * implements java.io.Serializable {
    *;
}

## Rules for Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


## Rules for Gson
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** {
    *;
}
-keepattributes EnclosingMethod

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Rules for OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

# Rules for Javamail
-keep class javax.** {*;}
-keep class com.sun.** {*;}
-keep class myjava.** {*;}
-keep class org.apache.harmony.** {*;}

-dontwarn com.sun.mail.**
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**

# Otto Library
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# Remove logs, don't forget to use 'proguard-android-optimize.txt' file in build.gradle
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}
-keep class com.squareup.okhttp.** { *;}
# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keepclassmembers class com.google.gson.** {
    *;
}
-keepattributes EnclosingMethod

#FFMPeg
#-keep class com.arthenica.mobileffmpeg.** { *; }
#-dontwarn com.arthenica.mobileffmpeg.**

# Firebase
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

##############################
#Google Authentication
-keepclassmembers class com.google.android.gms.dynamite.DynamiteModule {
    ** MODULE_ID;
    ** MODULE_VERSION;
    ** sClassLoader;
}
-keepclassmembers class com.google.android.gms.internal.in {
    ** mOrigin;
    ** mCreationTimestamp;
    ** mName;
    ** mValue;
    ** mTriggerEventName;
    ** mTimedOutEventName;
    ** mTimedOutEventParams;
    ** mTriggerTimeout;
    ** mTriggeredEventName;
    ** mTriggeredEventParams;
    ** mTimeToLive;
    ** mExpiredEventName;
    ** mExpiredEventParams;
}
-keepclassmembers class com.google.devtools.build.android.desugar.runtime.ThrowableExtension {
    ** SDK_INT;
}
-keep class com.google.android.gms.dynamic.IObjectWrapper
-keep class com.google.android.gms.tasks.Task
-keep class com.google.android.gms.tasks.TaskCompletionSource
-keep class com.google.android.gms.tasks.OnSuccessListener
-keep class com.google.android.gms.tasks.OnFailureListener
-keep class com.google.android.gms.tasks.OnCompleteListener
-keep class com.google.android.gms.tasks.Continuation
-keep class com.google.android.gms.measurement.AppMeasurement$EventInterceptor
-keep class com.google.android.gms.measurement.AppMeasurement$OnEventListener
-keep class com.google.android.gms.measurement.AppMeasurement$zza
-keep class com.google.android.gms.internal.zzcgl
-keep class com.google.android.gms.internal.zzbhh
-keep class com.google.android.gms.internal.aad
-keep class com.google.android.gms.internal.aae
-keep class com.google.android.gms.internal.iq
-keep class com.google.android.gms.internal.ly
-keep class com.google.android.gms.internal.kx
-keep class com.google.android.gms.internal.xf
-keep class com.google.android.gms.internal.qu
-keep class com.google.android.gms.internal.qr
-keep class com.google.android.gms.internal.xm
-keep class com.google.android.gms.internal.aaj
-keep class com.google.android.gms.internal.aat
-keep class com.google.android.gms.internal.aah
-keep class com.google.android.gms.internal.rx
-keep class com.google.android.gms.internal.qg
-keep class com.google.android.gms.internal.sh
-keep class com.google.android.gms.internal.qu
-keep class com.google.android.gms.internal.vq
-keep class com.google.android.gms.internal.qi
-keep class com.google.android.gms.internal.oh
-keep class com.google.android.gms.internal.oo
-keep class com.google.android.gms.internal.oc
-keep class com.google.android.gms.internal.oi
-keep class com.google.android.gms.internal.ol
-keep class com.google.android.gms.internal.wn
-keep class com.google.android.gms.internal.oj
-keep class com.google.android.gms.internal.om
-keep class com.google.android.gms.internal.pf
-keep class com.google.android.gms.internal.za
-keep class com.google.android.gms.internal.pz
-keep class com.google.android.gms.internal.zn
-keep class com.google.android.gms.internal.zi
-keep class com.google.android.gms.internal.aen
-keep class com.google.android.gms.internal.aas
-keep class com.google.android.gms.internal.aav
-keep class com.google.android.gms.internal.aag
-keep class com.google.android.gms.internal.abh
-keep class com.google.android.gms.internal.abk
-keep class com.google.android.gms.internal.abq
-keep class com.google.android.gms.internal.abl
-keep class com.google.android.gms.internal.acf
-keep class com.google.android.gms.common.api.Result
-keep class com.google.android.gms.common.zza

-dontnote com.google.android.gms.internal.ql
-dontnote com.google.android.gms.internal.zzcem
-dontnote com.google.android.gms.internal.zzchl

# Firebase notes
-dontnote com.google.firebase.messaging.zza

# Protobuf notes
-dontnote com.google.protobuf.zzc
-dontnote com.google.protobuf.zzd
-dontnote com.google.protobuf.zze

# For Media3/PlayerView
-keep class androidx.media3.** { *; }
-keep class com.google.android.exoplayer2.** { *; }
-keep interface com.google.android.exoplayer2.** { *; }

# For FFmpeg
-keep class com.arthenica.** { *; }
-keep class org.bytedeco.** { *; }

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class com.i69.** { *; }  # Replace with your package

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.cardinalcommerce.dependencies.internal.minidev.asm.Accessor
-dontwarn com.cardinalcommerce.dependencies.internal.minidev.asm.BeansAccess
-dontwarn com.cardinalcommerce.dependencies.internal.minidev.asm.ConvertDate
-dontwarn com.cardinalcommerce.dependencies.internal.minidev.asm.FieldFilter
-dontwarn com.google.android.libraries.places.api.Places
-dontwarn com.google.android.libraries.places.api.model.AddressComponent
-dontwarn com.google.android.libraries.places.api.model.AddressComponents
-dontwarn com.google.android.libraries.places.api.model.AutocompletePrediction
-dontwarn com.google.android.libraries.places.api.model.AutocompleteSessionToken
-dontwarn com.google.android.libraries.places.api.model.Place$Field
-dontwarn com.google.android.libraries.places.api.model.Place
-dontwarn com.google.android.libraries.places.api.model.TypeFilter
-dontwarn com.google.android.libraries.places.api.net.FetchPlaceRequest
-dontwarn com.google.android.libraries.places.api.net.FetchPlaceResponse
-dontwarn com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest$Builder
-dontwarn com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
-dontwarn com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
-dontwarn com.google.android.libraries.places.api.net.PlacesClient
-dontwarn com.stripe.android.stripecardscan.cardscan.CardScanSheet$CardScanResultCallback
-dontwarn com.stripe.android.stripecardscan.cardscan.CardScanSheet$Companion
-dontwarn com.stripe.android.stripecardscan.cardscan.CardScanSheet
-dontwarn com.stripe.android.stripecardscan.cardscan.CardScanSheetResult$Completed
-dontwarn com.stripe.android.stripecardscan.cardscan.CardScanSheetResult$Failed
-dontwarn com.stripe.android.stripecardscan.cardscan.CardScanSheetResult
-dontwarn com.stripe.android.stripecardscan.cardscan.exception.UnknownScanException
-dontwarn com.stripe.android.stripecardscan.payment.card.ScannedCard

# Keep the CameraUseCaseAdapter class with all its members
-keep class androidx.camera.core.internal.CameraUseCaseAdapter {
    *;
}
-keep class org.webrtc.** { *; }

#-printmapping build/outputs/mapping/productionRelease/mapping.txt