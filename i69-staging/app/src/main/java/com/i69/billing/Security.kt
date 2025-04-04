package com.i69.billing

import android.util.Base64
import android.util.Log
import com.i69.data.config.Constants.BASE_64_ENCODED_PUBLIC_KEY
import java.io.IOException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

private const val TAG = "Billing"
private const val KEY_FACTORY_ALGORITHM = "RSA"
private const val SIGNATURE_ALGORITHM = "SHA1withRSA"

object Security {
    private var TAG: String = Security::class.java.simpleName
    fun verifyPurchase(signedData: String, signature: String): Boolean {
        if (signedData.isEmpty() || BASE_64_ENCODED_PUBLIC_KEY.isEmpty() || signature.isEmpty()) {
            Log.e(TAG,"Purchase verification failed: missing data.")
            return false
        }

        return try {
            val key = generatePublicKey(BASE_64_ENCODED_PUBLIC_KEY)
            verify(key, signedData, signature)

        } catch (e: IOException) {
            Log.e(TAG,"Error generating PublicKey from encoded key: ${e.message}")
            false
        }
    }

    @Throws(IOException::class)
    fun generatePublicKey(encodedPublicKey: String): PublicKey {
        try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))

        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)

        } catch (e: InvalidKeySpecException) {
            val error = "Invalid Key Spec Exception: $e"
            Log.e(TAG,error)
            throw IOException(error)
        }
    }



    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        val signatureBytes: ByteArray = try {
            Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG,"Base64 decoding failed.")
            return false
        }

        try {
            val signatureAlgorithm: Signature = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                Log.e(TAG,"Signature verification failed...")
                return false
            }
            return true

        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)

        } catch (e: InvalidKeyException) {
            Log.e(TAG,"Invalid key specification.")

        } catch (e: SignatureException) {
            Log.e(TAG,"Signature exception.")
        }
        return false
    }

}