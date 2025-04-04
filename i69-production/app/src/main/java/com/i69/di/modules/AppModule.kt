package com.i69.di.modules

import android.content.Context
import androidx.room.Room
import com.i69.applocalization.AppStringConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.i69.data.remote.api.Api
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.repository.*
import com.i69.profile.db.dao.UserDao
import com.i69.db.AppDatabase
import com.i69.BuildConfig
import com.i69.data.remote.api.MarketGraphqlApi
import com.i69.utils.SharedPref
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private fun getRetrofit(converterFactory: Converter.Factory, contentType: String): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val clientBuilder = OkHttpClient.Builder()

        if (contentType.isNotEmpty()) {
            clientBuilder.addNetworkInterceptor { chain ->
                val request =
                    chain.request().newBuilder().header("Content-Type", contentType).build()
                return@addNetworkInterceptor chain.proceed(request)
            }
        }

        val client = clientBuilder.addInterceptor(logging).connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES).build()

        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
            .addConverterFactory(converterFactory).build()
    }

    private fun getRetrofitMarketOld(converterFactory: Converter.Factory, contentType: String): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val clientBuilder = OkHttpClient.Builder()

        if (contentType.isNotEmpty()) {
            clientBuilder.addNetworkInterceptor { chain ->
                val request =
                    chain.request().newBuilder().header("Content-Type", contentType).build()
                return@addNetworkInterceptor chain.proceed(request)
            }
        }

        val client = clientBuilder.addInterceptor(logging).connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES).build()

        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL_MARKET).client(client)
            .addConverterFactory(converterFactory).build()
    }
    private fun getRetrofitMarket(converterFactory: Converter.Factory, contentType: String): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val clientBuilder = OkHttpClient.Builder()
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        val trustManagerFactory: TrustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> =
            trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + trustManagers.contentToString()
        }

        val trustManager =
            trustManagers[0] as X509TrustManager


       // val builder = OkHttpClient.Builder()

        clientBuilder.sslSocketFactory(sslSocketFactory, trustManager)
        clientBuilder.hostnameVerifier(HostnameVerifier { _, _ -> true })

       // builder.build()
        // end cert



        if (contentType.isNotEmpty()) {
            clientBuilder.addNetworkInterceptor { chain ->
                val request =
                    chain.request().newBuilder().header("Content-Type", contentType).build()
                return@addNetworkInterceptor chain.proceed(request)
            }
        }

        val client = clientBuilder.addInterceptor(logging).connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES).build()

        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL_MARKET).client(client)
            .addConverterFactory(converterFactory).build()
    }

    // unsafe cert
    private fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()

            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    @Singleton
    @Provides
    fun provideGraphqlApi(): GraphqlApi = getRetrofit(
        converterFactory = ScalarsConverterFactory.create(), "application/json"
    ).create(GraphqlApi::class.java)

    fun provideGraphqlApiMarket(): MarketGraphqlApi = getRetrofitMarket(
        converterFactory = ScalarsConverterFactory.create(), "application/json"
    ).create(MarketGraphqlApi::class.java)


    @Singleton
    @Provides
    fun provideApi(): Api = getRetrofit(
        converterFactory = GsonConverterFactory.create(), ""
    ).create(Api::class.java)

    @Singleton
    @Provides
    fun provideAppRepository(api: GraphqlApi) = AppRepository(api)

    @Singleton
    @Provides
    fun provideLoginRepository(api: GraphqlApi) = LoginRepository(api)

    @Singleton
    @Provides
    fun provideCoinRepository(api: GraphqlApi) = CoinRepository(api)

    @Singleton
    @Provides
    fun provideUserUpdateRepository(graphqlApi: GraphqlApi, api: Api) =
        UserUpdateRepository(graphqlApi = graphqlApi, api = api)

    @Singleton
    @Provides
    fun provideUserDetailsRepository(
        api: GraphqlApi,
        userUpdateRepository: UserUpdateRepository,
        dao: UserDao,
        @ApplicationContext context: Context
    ) = UserDetailsRepository(api, userUpdateRepository, dao, context)

    /* @Singleton
     @Provides
     fun provideChatRepository(api: GraphqlApi, userDetailsRepository: UserDetailsRepository, userDao: UserDao, chatsDao: ChatDialogsDao) = ChatRepository(api, userDetailsRepository, chatsDao, userDao)

     @Singleton
     @Provides
     fun provideChatLoginRepository() = ChatLoginRepository()*/

    @Singleton
    @Provides
    fun provideSearchRepository(api: GraphqlApi) = SearchRepository(api)

    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ) = context

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_db.db")
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideMomentDao(db: AppDatabase) = db.momentDao()

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideChatDialogsDao(db: AppDatabase) = db.chatDialogDao()

    @Provides
    fun provideAppStringConstant(context: Context) = AppStringConstant(context)


    @Provides
    fun getSharedPref(@ApplicationContext context: Context): SharedPref {
        return SharedPref(context)
    }

}