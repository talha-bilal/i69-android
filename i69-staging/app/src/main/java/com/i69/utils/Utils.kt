package com.i69.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.i69.R
import com.i69.languages.LanguageModel
import org.json.JSONObject
import java.util.Locale

private const val START_ANIM_VALUE = 0.9f
private const val END_ANIM_VALUE = 1f
const val EXTRA_USER_MODEL = "user"
const val EXTRA_FIELD_VALUE = "EXTRA_FIELD_VALUE"
var animSideDuration: Float = 0f
var prevDiff: Int = 0

private fun getCurrentLanguage(): String = Locale.getDefault().language

fun isCurrentLanguageFrench() = getCurrentLanguage() == "fr"

fun getChatMsgNotificationBody(msgText: String): String =
    if (msgText.length > 15) msgText.take(15) + "..." else msgText

class Utils {
    companion object {
        @JvmStatic
        fun convertInchToLb(value: Float): String {
            val inchInt = value.toInt()
            val inch = inchInt % 12
            val lb = inchInt / 12
            return "$lb'$inch\""
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels
        }

        fun isImageFile(contentUri: Uri, context: Context): Boolean {
            val mimeType = context.contentResolver.getType(contentUri)
            return mimeType?.startsWith("image/") == true
        }

        fun isVideoFile(contentUri: Uri, context: Context): Boolean {
            val mimeType = context.contentResolver.getType(contentUri)
            return mimeType?.startsWith("video/") == true
        }

        fun isImageFile(fileUri: Uri): Boolean {
            val path = fileUri.path ?: return false
            val extension = path.substringAfterLast('.', "").lowercase()
            return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
        }

        fun isVideoFile(fileUri: Uri): Boolean {
            val path = fileUri.path ?: return false
            val extension = path.substringAfterLast('.', "").lowercase()
            return extension in listOf("mp4", "mkv", "avi", "mov", "flv", "wmv", "webm")
        }
    }
}


fun Activity.startEmailIntent(toEmail: String, subject: String, message: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)

    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.setTextOrHide(text: String?) {
    if (text.isNullOrEmpty()) {
        this.visibility = View.GONE
    } else {
        this.text = text
        this.visibility = View.VISIBLE
    }
}

fun View.setVisibility(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setVisibleOrInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun View.setViewGone() = this.setVisibility(false)

fun View.setViewVisible() = this.setVisibility(true)


fun JSONObject.getStringOrNull(key: String): String? {
    if (has(key)) {
        return this.getString(key)
    }
    return null
}

fun Context.inflate(
    @LayoutRes resId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View =
    LayoutInflater.from(this).inflate(resId, parent, attachToRoot)

fun View.inflate(
    @LayoutRes resId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View =
    LayoutInflater.from(this.context).inflate(resId, parent, attachToRoot)

fun FragmentManager.transact(fragment: Fragment, addToBackStack: Boolean = false) {
    val res = this.beginTransaction()
        .replace(R.id.container, fragment)
    if (addToBackStack)
        res.addToBackStack(null)

    res.commit()
}

fun View.defaultAnimate(duration: Long, delay: Long) {
    this.scaleX = START_ANIM_VALUE
    this.scaleY = START_ANIM_VALUE
    this.alpha = 0f
    ViewCompat.animate(this)
        .scaleX(END_ANIM_VALUE)
        .scaleY(END_ANIM_VALUE)
        .alpha(END_ANIM_VALUE)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(duration)
        .setStartDelay(delay)
        .start()
}

fun View.animateFromLeft(duration: Long, delay: Long, width: Int = 0) {
    this.alpha = 0f
    val side = getAnimSideTransactionX(context, width)
    this.x = this.x - side
    ViewCompat.animate(this)
        .translationXBy(side)
        .alpha(END_ANIM_VALUE)
        .setInterpolator(OvershootInterpolator())
        .setDuration(duration)
        .setStartDelay(delay)
        .start()
}

fun View.animateFromRight(duration: Long, delay: Long, width: Int = 0) {
    this.alpha = 0f
    val side = getAnimSideTransactionX(context, width)
    this.x = this.x + side
    ViewCompat.animate(this)
        .translationXBy(-side)
        .alpha(END_ANIM_VALUE)
        .setInterpolator(OvershootInterpolator())
        .setDuration(duration)
        .setStartDelay(delay)
        .start()
}

fun getAnimSideTransactionX(ctx: Context, widthDiff: Int): Float {
    if (prevDiff != widthDiff || animSideDuration == 0f)
        animSideDuration =
            if (widthDiff == 0) ctx.resources.getDimension(R.dimen.anim_side_duration) else widthDiff.toFloat()
    prevDiff = widthDiff
    return animSideDuration
}

fun String.findFileExtension(): String {
    var ext = ""
    try {
        val uri = Uri.parse(this)
        val lastSegment = uri.lastPathSegment
        ext = lastSegment?.substring(lastSegment.lastIndexOf(".") + 1).toString()

    } catch (e: Exception) {
    }
    return ext
}

fun String.isImageFile(): Boolean {
    return this.contentEquals("jpg")
            || this.contentEquals("png")
            || this.contentEquals("jpeg")
}

fun String.isAudioFile(): Boolean {
    return this.contentEquals("pcm")
            || this.contentEquals("wav")
            || this.contentEquals("aac")
            || this.contentEquals("3gp")
}

fun String.isVideoFile(): Boolean {
    return this.contentEquals("mp4")
            || this.contentEquals("mov")
            || this.contentEquals("wmv")
            || this.contentEquals("avi")
            || this.contentEquals("flv")
            || this.contentEquals("mkv")
}

fun String.isNeitherImageNorVideoFile(): Boolean {
    return !this.isImageFile() && !this.isVideoFile()
}

fun changeLang(context: Context, lang_code: String): ContextWrapper? {
    var context = context
    val sysLocale: Locale
    val rs = context.resources
    val config: Configuration = rs.configuration
    sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.locales.get(0)
    } else {
        config.locale
    }
    if (lang_code != "" && !sysLocale.language.equals(lang_code)) {
        val locale = Locale(lang_code)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        context = context.createConfigurationContext(config)
    }
    return ContextWrapper(context)
}


public fun fetchLanguages(): ArrayList<LanguageModel> {
    val langList = ArrayList<LanguageModel>()

    langList.add(LanguageModel(1, "en", "English", "en-US", R.drawable.english, "English"))
    //   langList.add(LanguageModel(1,"af", "Afrikaans", "af-ZA", R.drawable.afrikaans, "Afrikaans"))
    langList.add(LanguageModel(26, "sq", "Albanian", "al-SQ", R.drawable.albanian, "shqiptar"))
//    langList.add(LanguageModel(1,"am", "Amharic", "am-ET", R.drawable.amharic, "አማርኛ"))
    langList.add(LanguageModel(7, "ar", "Arabic", "ar-SA", R.drawable.arabic, "العربية"))
    langList.add(LanguageModel(28, "hy", "Armenian", "hy-AM", R.drawable.armenian, "հայերեն"))
    langList.add(
        LanguageModel(
            5,
            "az",
            "Azeerbaijani",
            "az-AZ",
            R.drawable.azeerbaijani,
            "Azərbaycan"
        )
    )
//    langList.add(LanguageModel(1,"eu", "Basque", "eu-ES", R.drawable.spanish, "Euskal"))
//    langList.add(LanguageModel(1,"be", "Belarusian", "ru-BG", R.drawable.belarusian, "Беларус"))
    langList.add(LanguageModel(36, "bn", "Bengali", "bn-BD", R.drawable.bengali, "বাংলা"))
    langList.add(LanguageModel(37, "bs", "Bosnian", "Bosnian", R.drawable.bosnian, "bosanski"))
    langList.add(LanguageModel(38, "bg", "Bulgarian", "bg-BG", R.drawable.catalan, "Български"))
    langList.add(LanguageModel(40, "ca", "Catalan", "ca-ES", R.drawable.catalan, "Català"))
//    langList.add(LanguageModel(1,"ceb", "Cebuano", "ceb-PHL", R.drawable.corsican, "Cebuano"))
    langList.add(LanguageModel(44, "zh", "Chinese", "yue-Hant-HK", R.drawable.chinese, "中文"))
//    langList.add(LanguageModel(1,"zh", "Chinese(Traditional)", "zh-Hant", R.drawable.chinese, "中文"))
//    langList.add(LanguageModel(1,"co", "Corsican", "fr-FR", R.drawable.corsican, "Corsu"))
//    langList.add(LanguageModel(1,"hr", "Croatian", "hr-HR", R.drawable.croatian, "Hrvatski"))
    langList.add(LanguageModel(21, "cs", "Czech", "cs-CZ", R.drawable.czech, "Čeština"))
    langList.add(LanguageModel(22, "da", "Danish", "da-DK", R.drawable.danish, "Dansk"))
    langList.add(LanguageModel(3, "nl", "Dutch", "nl-NL", R.drawable.dutch, "Nederlands"))
//    langList.add(LanguageModel(1,"eo", "Esperanto", "eo-DZ", R.drawable.esperanto, "Esperanto"))
//    langList.add(LganguageModel("et", "Estonian", "et-EST", R.drawable.estonian, "Eesti"))
//    langList.add(LanguageModel(1,"fi", "Finnish", "fi-FI", R.drawable.finnish, "Suomi"))
    langList.add(LanguageModel(2, "fr", "French", "fr-FR", R.drawable.french, "Français"))
//    langList.add(LanguageModel(1,"fy", "Frisian", "fy-DEU", R.drawable.frisian, "Frysk"))
    langList.add(LanguageModel(42, "gl", "Galician", "gl-ES", R.drawable.galician, "Galego"))
    langList.add(LanguageModel(43, "ka", "Georgian", "ka-GE", R.drawable.georgian, "ქართული"))
    langList.add(LanguageModel(4, "de", "German", "de-DE", R.drawable.germany, "Deutsch"))
    langList.add(LanguageModel(13, "el", "Greek", "el-GR", R.drawable.greek, "Ελληνικά"))
//    langList.add(LanguageModel(1,"gu", "Gujarati", "gu-IN", R.drawable.gujarati, "ગુજરાતી"))
//    langList.add(
//        LanguageModel(
//            "ht",
//            "Haitian",
//            "ht-HTI",
//            R.drawable.haitiancreole,
//            "Haitian Creole"
//        )
//    )
//    langList.add(LanguageModel(1,"ha", "Hausa", "ha-HUA", R.drawable.hausa, "Hausa"))
//    langList.add(LanguageModel(1,"haw", "Hawaiian", "haw-US", R.drawable.hawaiian, "ʻ .lelo Hawaiʻi"))
    langList.add(LanguageModel(8, "iw", "Hebrew", "he-HEB", R.drawable.hebrew, "עברית"))
//    langList.add(LanguageModel(1,"hmn", "Hmong", "hmn-Hm", R.drawable.hmong, "Hmong"))
//    langList.add(LanguageModel(1,"ig", "Igbo", "Igbo", R.drawable.igbo, "Ndi Igbo"))
    langList.add(LanguageModel(6, "it", "Italian", "it-IT", R.drawable.itallian, "Italiano"))
    langList.add(LanguageModel(21, "ja", "Japanese", "ja-JP", R.drawable.japanese, "日本語"))
//    langList.add(LanguageModel(1,"jw", "Javanese", "jv-ID", R.drawable.javanese, "Basa jawa"))
//    langList.add(LanguageModel(1,"kn", "Kannada", "kn-IN", R.drawable.kannada, "ಕನ್ನಡ"))
//    langList.add(LanguageModel(1,"kk", "Kazakh", "Kazakh", R.drawable.kazakh, "Қазақ"))
//    langList.add(LanguageModel(1,"km", "Khmer", "km-KH", R.drawable.khmer, "ខខ្មែរ"))
    langList.add(LanguageModel(15, "ko", "Korean", "ko-KR", R.drawable.korean, "한국어"))
//    langList.add(LanguageModel(1,"ku", "Kurdish", "ku-KUR", R.drawable.kurdish, "Kurdî"))
//    langList.add(LanguageModel(1,"ky", "Kyrgyz", "ky-KGZ", R.drawable.kyrgyz, "Кыргызча"))
//    langList.add(LanguageModel(1,"lo", "Lao", "lo-LA", R.drawable.lao, "ລາວ"))
//    langList.add(LanguageModel(1,"la", "Latin", "Latin", R.drawable.latin, "Latine"))
    langList.add(LanguageModel(30, "lv", "Latvian", "lv-LV", R.drawable.khmer, "Latviešu valoda"))
//    langList.add(LanguageModel(1,"lt", "Lithuanian", "lt-LT", R.drawable.lithuanian, "Lietuvių"))
//    langList.add(
//        LanguageModel(
//            "lb",
//            "Luxembourgish",
//            "mk-MDK",
//            R.drawable.luxembourgish,
//            "Lëtzebuergesch"
//        )
//    )
//    langList.add(LanguageModel(1,"mg", "Malagasy", "Malay", R.drawable.malagasy, "Malagasy"))
//    langList.add(LanguageModel(1,"ms", "Malay", "ms-MY", R.drawable.malay, "Bahasa Melayu"))
//    langList.add(LanguageModel(1,"ml", "Malayalam", "ml-IN", R.drawable.malayalam, "മലയാളം"))
//    langList.add(LanguageModel(1,"mt", "Maltese", "mt-MLT", R.drawable.maltese, "Il-Malti"))
//    langList.add(LanguageModel(1,"mi", "Maori", "mi-MIO", R.drawable.maori, "Maori"))
//    langList.add(LanguageModel(1,"mr", "Marathi", "mr-IN", R.drawable.marathi, "मराठी"))
//    langList.add(LanguageModel(1,"my", "Myanmar", "my-MMR", R.drawable.myanmar, "မြန်မာ"))
    langList.add(LanguageModel(18, "no", "Norwegian", "nb-NO", R.drawable.norwegian, "Norsk"))
//    langList.add(LanguageModel(1,"ny", "Nyanja", "Nyanja", R.drawable.nyanja, "Nyanja"))
//    langList.add(LanguageModel(1,"ps", "Pashto", "ps-PK", R.drawable.pashto, "پښتو"))
    langList.add(LanguageModel(11, "fa", "Persian", "fa-IR", R.drawable.persian, "فارسی"))
    langList.add(LanguageModel(16, "pl", "Polish", "pl-POL", R.drawable.polish, "Polski"))
    langList.add(LanguageModel(27, "pt", "Portuguese", "pt-PT", R.drawable.portuguese, "Português"))
//    langList.add(LanguageModel(1,"pa", "Punjabi", "pa-IN", R.drawable.punjabi, "ਪੰਜਾਬੀ"))
    langList.add(LanguageModel(10, "ru", "Russian", "ru-RU", R.drawable.russian, "Pусский"))

//    langList.add(LanguageModel(1,"smo", "Samoan", "sm-WSM", R.drawable.samoan, "Sāmoa"))
//    langList.add(LanguageModel(1,"nso", "Sesotho", "st-SOT", R.drawable.sesotho, "Sesotho"))
//    langList.add(LanguageModel(1,"sin", "Sinhala", "si-LK", R.drawable.sinhala, "සිංහල"))


//    langList.add(LanguageModel(1,"gd", "Scots", "gd-GBR", R.drawable.scotsgaelic, "Albannaich"))
//    langList.add(LanguageModel(1,"sn", "Shona", "sn-SNA", R.drawable.shona, "Shona"))
//    langList.add(LanguageModel(1,"sd", "Sindhi", "sd-Pk", R.drawable.sindhi, "سنڌي"))
//    langList.add(LanguageModel(1,"sl", "Slovenian", "sl-SI", R.drawable.slovak, "Slovenščina"))
//    langList.add(LanguageModel(1,"so", "Somali", "so-SOM", R.drawable.somali, "Soomaali"))
    langList.add(LanguageModel(12, "es", "Spanish", "es-ES", R.drawable.spanish, "Español"))
//    langList.add(LanguageModel(1,"su", "Sundanese", "su-ID", R.drawable.samoan, "Urang Sunda"))
    langList.add(LanguageModel(5, "sw", "Swahili", "sw-TZ", R.drawable.swahili, "Kiswahili"))
    langList.add(LanguageModel(19, "sv", "Swedish", "sv-SE", R.drawable.swedish, "Svenska"))

//    langList.add(LanguageModel(1,"tgl", "Tagalog", "tl-TGL", R.drawable.tagalog))


//    langList.add(LanguageModel(1,"te", "Telugu", "te-IN", R.drawable.telugu, "తెలుగు"))
    langList.add(LanguageModel(32, "th", "Thai", "th-TH", R.drawable.thai, "ไทย"))

    langList.add(LanguageModel(33, "uk", "Ukrainian", "uk-UA", R.drawable.ukrainian, "Українська"))
//    langList.add(LanguageModel(1,"vi", "Vietnamese", "vi-VN", R.drawable.vietnamese, "Tiếng Việt"))
//    langList.add(LanguageModel(1,"cy", "Welsh", "cy-GBR", R.drawable.welsh, "Cymraeg"))
//    langList.add(LanguageModel(1,"xh", "Xhosa", "xh-XHO", R.drawable.xhosa, "isiXhosa"))
//    langList.add(LanguageModel(1,"yo", "Yoruba", "Yoruba-yo", R.drawable.yoruba, "Yoruba"))


    //Commented by Janak on 27 Mar 2023

    /*langList.add(LanguageModel(1,"ur", "Urdu", "ur-PK", R.drawable.urdu, "اردو"))
    langList.add(LanguageModel(1,"hi", "Hindi", "hi-IN", R.drawable.hindi, "हिंदी"))
    langList.add(LanguageModel(1,"hu", "Hungarian", "hu-HU", R.drawable.hungarian, "Magyar"))
    langList.add(LanguageModel(1,"is", "Icelandic", "is-IS", R.drawable.icelandic, "Íslensku"))
    langList.add(LanguageModel(1,"id", "Indonesian", "id-ID", R.drawable.indonesian, "Indonesia"))
    langList.add(LanguageModel(1,"ga", "Irish", "Irish", R.drawable.irish, "Gaeilge"))
    langList.add(LanguageModel(1,"mk", "Macedonian", "mg-MGD", R.drawable.macedonian, "Македонски"))
    langList.add(LanguageModel(1,"mn", "Mongolian", "mn-MNG", R.drawable.mongolian, "Монгол"))
    langList.add(LanguageModel(1,"ne", "Nepali", "ne-NP", R.drawable.nepali, "नेपाली"))
    langList.add(LanguageModel(1,"ro", "Romanian", "ro-RO", R.drawable.romanian, "Română"))
    langList.add(LanguageModel(1,"sr", "Serbian", "sr-RS", R.drawable.serbian, "Српски"))
    langList.add(LanguageModel(1,"sk", "Slovak", "sk-SK", R.drawable.slovak, "Slovenský"))
    langList.add(LanguageModel(1,"tg", "Tajik", "tg-TJK", R.drawable.uzbek, "Точик"))
    langList.add(LanguageModel(1,"ta", "Tamil", "ta-IN", R.drawable.tamil, "தமிழ்"))
    langList.add(LanguageModel(1,"tr", "Turkish", "tr-TR", R.drawable.turkish, "Türk"))
    langList.add(LanguageModel(1,"uz", "Uzbek", "uz-UZB", R.drawable.uzbek, "O'zbek"))*/

    langList.sortWith(Comparator { o1, o2 ->
        o1.languageName.compareTo(
            o2.languageName,
            ignoreCase = true
        )
    })

    return langList
}


fun doRestart(c: Context?) {
    try {
        //check if the context is given
        if (c != null) {
            //fetch the packagemanager so we can get the default launch activity
            // (you can replace this intent with any other activity if you want
            val pm = c.packageManager
            //check if we got the PackageManager
            if (pm != null) {
                //create the intent with the default start activity for your application
                val mStartActivity = pm.getLaunchIntentForPackage(
                    c.packageName
                )
                if (mStartActivity != null) {
                    mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    //create a pending intent so the application is restarted after System.exit(0) was called.
                    // We use an AlarmManager to call this intent in 100ms
                    val mPendingIntentId = 223344
                    val mPendingIntent = PendingIntent
                        .getActivity(
                            c, mPendingIntentId, mStartActivity,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                    val mgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] =
                        mPendingIntent
                    //kill the application
                    System.exit(0)
                } else {
//                    Log.e(TAG, "Was not able to restart application, mStartActivity null")
                }
            } else {
//                Log.e(TAG, "Was not able to restart application, PM null")
            }
        } else {
//            Log.e(TAG, "Was not able to restart application, Context null")
        }
    } catch (ex: java.lang.Exception) {
//        Log.e(TAG, "Was not able to restart application")
    }
}

fun triggerRebirth(context: Context) {
    val packageManager = context.packageManager
    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
    val componentName = intent!!.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

fun copyToClipboard(message: String, ctx: Context) {
    val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(android.R.attr.label.toString(), message)
    clipboard!!.setPrimaryClip(clip)
}


private fun getAllChildren(v: View): ArrayList<View>? {
    if (v !is ViewGroup) {
        val viewArrayList = ArrayList<View>()
        viewArrayList.add(v)
        return viewArrayList
    }
    val result = ArrayList<View>()
    val vg = v
    for (i in 0 until vg.childCount) {
        val child = vg.getChildAt(i)
        val viewArrayList = ArrayList<View>()
        viewArrayList.add(v)
        viewArrayList.addAll(getAllChildren(child)!!)
        result.addAll(viewArrayList)
    }
    return result
}


fun TextView.attributedString(
    forText: String,
    foregroundColor: Int? = null,
    style: StyleSpan? = null
) {
    val spannable: Spannable = SpannableString(text)

    // check if the text we're highlighting is empty to abort
    if (forText.isEmpty()) {
        return
    }

    // compute the start and end indices from the text
    val startIdx = text.indexOf(forText)
    val endIdx = startIdx + forText.length

    // if the indices are out of bounds, abort as well
    if (startIdx < 0 || endIdx > text.length) {
        return
    }

    // check if we can apply the foreground color
    foregroundColor?.let {
        spannable.setSpan(
            ForegroundColorSpan(it),
            startIdx,
            endIdx,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    // check if we have a stylespan
    style?.let {
        spannable.setSpan(
            style,
            startIdx,
            endIdx,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    // apply it
    text = spannable
}


fun getEncodedApiKey(plainApiKey: String) {
    val encodedApiKey = String(
        Base64.encode(
            Base64.encode(
                plainApiKey.toByteArray(),
                Base64.DEFAULT
            ),
            Base64.DEFAULT
        )
    )
    Log.e("TAG", "getEncodedApiKey: $encodedApiKey")
}

fun getDecodedApiKey(encodeString: String): String {
    if (encodeString.startsWith("UVV")) {
        return String(
            Base64.decode(
                Base64.decode(
                    encodeString,
                    Base64.DEFAULT
                ),
                Base64.DEFAULT
            )
        )
    }
    return encodeString

}




