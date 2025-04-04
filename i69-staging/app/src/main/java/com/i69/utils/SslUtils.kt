package com.i69.utils

import android.content.Context
import android.util.Log
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object SslUtils {



    fun getSslContextForCertificateFile(context: Context,certResource:Int): SSLContext {
        try {
            val keyStore = getKeyStore(context,certResource)
            val sslContext = SSLContext.getInstance("SSL")
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
            return sslContext
        } catch (e: Exception) {
            val msg = "Error during creating SslContext for certificate from assets"
            e.printStackTrace()
            throw RuntimeException(msg)
        }
    }

    private fun getKeyStore(context: Context,certResource:Int): KeyStore? {
        var keyStore: KeyStore? = null
        try {

            val cf = CertificateFactory.getInstance("X.509")
            val caInput = context.resources.openRawResource(certResource)
            val ca: Certificate
            try {
                ca = cf.generateCertificate(caInput)
                Log.e("SslUtils","ca=" + (ca as X509Certificate).subjectDN)
            } finally {
                caInput.close()
            }

            val keyStoreType = KeyStore.getDefaultType()
            keyStore = KeyStore.getInstance(keyStoreType)
            keyStore!!.load(null, null)
            keyStore.setCertificateEntry("ca", ca)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return keyStore
    }

}