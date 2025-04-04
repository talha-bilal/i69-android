package com.i69.utils

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.i69.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class UploadUtility(var fragment: Fragment) {

    val activity: Activity
        get() = fragment.requireActivity()
    private var TAG: String = UploadUtility::class.java.simpleName
    private lateinit var loadingDialog: Dialog
    var serverURL: String = "${BuildConfig.BASE_URL}chat/image_upload"
    private var client: OkHttpClient

    init {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        setSSlCert(clientBuilder, fragment.requireContext())
        client = clientBuilder.build()
    }

    fun uploadFile(
        sourceFilePath: String,
        uploadedFileName: String? = null,
        authorization: String?,
        upload_type: String?,
        callback: ((String?) -> Unit)
    ) {
        uploadFile(File(sourceFilePath), uploadedFileName, authorization, upload_type, callback)
    }

    fun uploadFile2(
        sourceFilePath: File,
        uploadedFileName: String? = null,
        authorization: String?,
        upload_type: String?,
        callback: ((String?) -> Unit)
    ) {
        uploadFile(sourceFilePath, uploadedFileName, authorization, upload_type, callback)
    }

    private fun uploadFile(
        sourceFile: File,
        uploadedFileName: String? = null,
        authorization: String?,
        upload_type: String?,
        callback: ((String?) -> Unit)
    ) {
        Thread {
            val mimeType = sourceFile.getMimeType() ?: return@Thread;
            val fileName: String = uploadedFileName ?: sourceFile.name
            Log.e("mimeType", "-->" + mimeType)
            Log.e("upload_type", "-->" + upload_type)
            toggleProgressDialog(true)
            try {
                val d = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.DAY_OF_MONTH)
                    .toFloat()
                val result = d * (33333333f / 222f)
                val token = result.toInt()
                Log.e(
                    TAG,
                    "checkauth d ${
                        Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.DAY_OF_MONTH)
                    }"
                )
                Log.e(TAG, "checkauth f $result")
                Log.e(TAG, "checkauth t $token")

                Log.e(TAG, "GalleryImageLauncher" + "Calling $token")
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("token", token.toString())
                        .addFormDataPart("upload_type", upload_type.toString())
                        .addFormDataPart(
                            "image",
                            fileName,
                            sourceFile.asRequestBody(mimeType.toMediaTypeOrNull())
                        )
                        .build()

                Log.e(TAG, "DValue" + "d = $d , token $token")
                Log.e(TAG, "TOKENN" + token.toString())
                Log.e(TAG, "UPLOADTYPE" + upload_type.toString())
                Log.e(TAG, "checkauth type[$mimeType] exist[${sourceFile.exists()}] $token")
                Log.e(TAG, "checkauth $serverURL")
                Log.e(TAG, "AUTHORIZATION" + authorization.toString())
                val request: Request = Request.Builder()
                    .addHeader("Authorization", "Token $authorization")
                    .url(serverURL).post(requestBody).build()

                val response: Response = client.newCall(request).execute()

                val responseBody = response.body?.string()
                Log.e(TAG,"checkauth $responseBody")
                if (response.isSuccessful) {
                    try {
                        val json = JSONObject(responseBody!!)
                        if (json.getBoolean("success")) {
                            callback.invoke(json.getString("img"))
                        } else {

                            if (json.getString("message").contains("Insufficient coins")) {
                                callback.invoke("error")
                            } else {
                                Log.e(
                                    "File cc",
                                    "-" + "Error: ${response.code} ${json.getString("message")}"
                                )
                                showToast("Error: ${response.code} ${json.getString("message")}")
                            }

                        }
                    } catch (je: JSONException) {
                        showToast("Invalid Json: ${response.code} ${responseBody}")
                    } catch (e: java.lang.Exception) {
                        showToast("Failed: ${response.code} ${responseBody}")
                    }
                    //showToast("File uploaded successfully at ${response.body.toString()} $serverUploadDirectoryPath$fileName")
                } else {

                    // if()
                    Log.e("File upload", "-" + responseBody)
                    Log.e("File c", "-" + response.code)
                    showToast("Failed: ${response.code} ${responseBody}")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e(TAG,"File ex.message"+ "-" + ex.message)
                Log.e(TAG, "File uploading failed ${ex.message}")
                //Log.e("File upload", "failed")
                showToast("File uploading failed ${ex.message}")
            }
            toggleProgressDialog(false)
        }.start()
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            //fragment.view?.snackbar(message)
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                //dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
                loadingDialog = activity.createLoadingDialog()
                loadingDialog.show()
            } else {
                loadingDialog.dismiss();
            }
        }
    }
}