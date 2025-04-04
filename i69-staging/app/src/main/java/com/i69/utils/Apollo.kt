package com.i69.utils

import android.content.Context
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpMethod
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.network.okHttpClient
import com.i69.BuildConfig
import com.i69.BuildConfig.BASE_URL_WEB_SOCKET
import com.i69.R
import okhttp3.*
import okhttp3.internal.concurrent.TaskRunner.Companion.logger
import okhttp3.logging.HttpLoggingInterceptor
import okio.*
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


private var instance: ApolloClient? = null
private var instanceForSubscription: ApolloClient? = null

fun apolloClientSubscription(context: Context, token: String): ApolloClient {

    if (instanceForSubscription != null) {
        return instanceForSubscription!!
    }
    Log.e("authh", " $token")
    val okHttpClientBuilder = getOkHttpClientBuilder(context, AuthorizationInterceptor(token))
    instanceForSubscription = ApolloClient.Builder()
        //.addHttpInterceptor(logging)
//        .httpServerUrl("https://admin.chatadmin-mod.click")
        .httpServerUrl(BuildConfig.BASE_URL).webSocketServerUrl(BASE_URL_WEB_SOCKET)
        .wsProtocol(SubscriptionWsProtocol.Factory(connectionPayload = { mapOf("content-type" to "application/json") }))
        //.subscriptionNetworkTransport(WebSocketNetworkTransport.Builder().serverUrl("ws://95.216.208.1:8000/ws/graphql").build())
        .okHttpClient(okHttpClientBuilder.build()).build()

    return instanceForSubscription!!
}

fun getOkHttpClientBuilder(context: Context, interceptor: Interceptor): OkHttpClient.Builder {
    val okHttpClientBuilder = OkHttpClient.Builder().addInterceptor(interceptor)
    setSSlCert(okHttpClientBuilder, context)
    return okHttpClientBuilder
}

fun setSSlCert(okHttpClientBuilder: OkHttpClient.Builder, context: Context) {
    val trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
    val trustManager = trustManagers[0] as X509TrustManager
    if (BuildConfig.BASE_URL == "https://api.i69app.com/") {
        okHttpClientBuilder.sslSocketFactory(
            SslUtils.getSslContextForCertificateFile(
//                context, R.raw.prod_ssl
                context, R.raw.api_i69app_com
            ).socketFactory, trustManager
        )
    } else {
        okHttpClientBuilder.sslSocketFactory(
            SslUtils.getSslContextForCertificateFile(
                // context, R.raw.fullchain_prod
                context, R.raw.chain
            ).socketFactory, trustManager
        )
    }
}

fun apolloClient(context: Context, token: String, isMultipart: Boolean = false): ApolloClient {/* if (instance != null) {
         return instance!!
     }*/
    Log.e("authh", " $token")
    Log.e("BaseUrl", " ${BuildConfig.BASE_URL}")
//    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
//        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
//        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
//        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//    })
//
//    val sslContext = SSLContext.getInstance("SSL")
//    sslContext.init(null, trustAllCerts, SecureRandom())
//    val okHttpClient = getOkHttpClientBuilder(context, AuthorizationInterceptor(token)).addInterceptor(HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }).sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager).hostnameVerifier {_, _ ->true}
    val okHttpClient =
        getOkHttpClientBuilder(context, AuthorizationInterceptor(token))
//            .addInterceptor(
//            HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            })

    if (isMultipart) {
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(60, TimeUnit.SECONDS)
        okHttpClient.readTimeout(60, TimeUnit.SECONDS)
    } else {
        okHttpClient.connectTimeout(45, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(30, TimeUnit.SECONDS)
        okHttpClient.readTimeout(30, TimeUnit.SECONDS)
    }

    //val logging = LoggingInterceptor()
    instance = ApolloClient.Builder()
//        .serverUrl("https://admin.chatadmin-mod.click")
        .serverUrl(BuildConfig.BASE_URL)
        //.addHttpInterceptor(AuthorizationHttpInterceptor(token))
        //.addHttpInterceptor(logging)
//        .addHttpInterceptor(LoggingInterceptor())
        .httpMethod(HttpMethod.Post).okHttpClient(okHttpClient.build())
//        .okHttpClient(okHttpClient)
        .build()
    return instance!!
}

fun apolloClientForContore(context: Context, token: String): ApolloClient {

    Log.e("authh", " $token")

    val okHttpClient = getOkHttpClientBuilder(context, AuthorizationInterceptorW(token))

    //val logging = LoggingInterceptor()
    instance = ApolloClient.Builder()
//        .serverUrl("https://admin.chatadmin-mod.click")
        .serverUrl(BuildConfig.BASE_URL)
        //.addHttpInterceptor(AuthorizationHttpInterceptor(token))
        //.addHttpInterceptor(logging)
        //.addHttpInterceptor(LoggingInterceptor())
        .httpMethod(HttpMethod.Post).okHttpClient(okHttpClient.build()).build()

    return instance!!
}

class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()
        logger.info(
            String.format(
                "Network_Interceptor request %s on %s%n%s",
                request.url,
                chain.connection(),
                request.headers
            )
        )

        val requestBuffer = Buffer()
        request.body?.writeTo(requestBuffer)
        logger.info("Network_Interceptor OkHttp RequestBody>>> ${requestBuffer.readUtf8()}")

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        logger.info(
            String.format(
                "Network_Interceptor Received response for %s in %.1fms%n%s",
                response.request.url,
                (t2 - t1) / 1e6,
                response.headers
            )
        )
        return response
    }

}

private class AuthorizationInterceptor(val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (chain.request().url.toString().contains("/ws/graphql?token=")) {
            chain.request()
        } else chain.request().newBuilder().addHeader("Authorization", "Token $token").build()
        return chain.proceed(request)
    }
}

private class AuthorizationInterceptorW(val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (chain.request().url.toString().contains("/ws/graphql?token=")) {
            chain.request()
        } else chain.request().newBuilder().addHeader("Authorization", "Token $token").build()
        return chain.proceed(request)
    }
}

private class AuthorizationHttpInterceptor(val token: String) : HttpInterceptor {
    override suspend fun intercept(
        request: HttpRequest, chain: HttpInterceptorChain
    ): HttpResponse {
        return chain.proceed(
            request.newBuilder().addHeader("Authorization", "Bearer $token").build()
        )
    }
}

internal class GzipRequestInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        if (originalRequest.body == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest)
        }
        val compressedRequest: Request =
            originalRequest.newBuilder().header("Content-Encoding", "gzip")
                .method(originalRequest.method, gzip(originalRequest.body!!)).build()
        return chain.proceed(compressedRequest)
    }

    private fun gzip(body: RequestBody): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return body.contentType()
            }

            override fun contentLength(): Long {
                return -1 // We don't know the compressed length in advance!
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                val gzipSink = GzipSink(sink).buffer()
                body.writeTo(gzipSink)
                logger.info(
                    "Network_Interceptor ${
                        String(
                            gzipSink.buffer.readByteArray(), Charsets.UTF_16
                        )
                    }"
                )
                gzipSink.close()
            }
        }
    }
}
