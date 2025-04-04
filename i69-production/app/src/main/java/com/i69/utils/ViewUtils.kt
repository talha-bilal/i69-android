package com.i69.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.i69.R
import com.i69.data.models.IdWithValue
import com.i69.ui.views.CheckableImageButton
import kotlin.math.roundToInt


enum class AnimationTypes {
    DEFAULT, SLIDE_ANIM, FADE_SLIDE_ANIM
}

fun NavController.navigate(
    @IdRes destinationId: Int,
    @IdRes popUpFragId: Int? = null,
    inclusive: Boolean = true,
    args: Bundle? = null,
    animType: AnimationTypes? = AnimationTypes.SLIDE_ANIM
) {
    val navOptionsBuilder = NavOptions.Builder()

    when (animType) {
        AnimationTypes.DEFAULT -> navOptionsBuilder.setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

        AnimationTypes.SLIDE_ANIM -> navOptionsBuilder.setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)

        AnimationTypes.FADE_SLIDE_ANIM -> navOptionsBuilder.setEnterAnim(R.anim.slide_in)
            .setExitAnim(R.anim.fade_out).setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.slide_out)

        else -> {}
    }

    popUpFragId?.let {
        navOptionsBuilder.setPopUpTo(it, inclusive)
    }
    val navOptions = navOptionsBuilder.build()

    navigate(destinationId, args, navOptions)
}

var dialog: AlertDialog? = null

fun Activity.showDialog(message: String) {
    val builder = AlertDialog.Builder(this)
    val inflater = LayoutInflater.from(this)
    val dialogView: View = inflater.inflate(R.layout.dialog_snackbar, null)
    builder.setView(dialogView)
    builder.setCancelable(false)

    val snackMsg = dialogView.findViewById<TextView>(R.id.snackMsg)
    val snackOk = dialogView.findViewById<TextView>(R.id.snackOk)
    snackMsg.text = message

    dialog = builder.create()
    dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    snackOk.setOnClickListener {
        dialog?.dismiss()
    }

    dialog?.show()
}

fun Activity.showDialogWithBackground(message: String, callback: (() -> Unit)? = null) {
    val builder = AlertDialog.Builder(this)
    val inflater = LayoutInflater.from(this)
    val dialogView: View = inflater.inflate(R.layout.dialog_snackbar, null)
    builder.setView(dialogView)
    builder.setCancelable(false)

    val snackMsg = dialogView.findViewById<TextView>(R.id.snackMsg)
    val snackOk = dialogView.findViewById<TextView>(R.id.snackOk)
    snackMsg.text = message

    dialog = builder.create()
    dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    Handler(Looper.getMainLooper()).postDelayed({
        if (dialog?.isShowing == true) dialog?.dismiss()
    }, 15000)

    snackOk.setOnClickListener {
        dialog?.dismiss()
        callback?.invoke()
    }

    dialog?.show()
}

fun setViewMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
    if (view.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = view.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        view.requestLayout()
    }
}

fun convertPXtoDP(pixel: Int, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, pixel.toFloat(), context.resources
            .displayMetrics
    ).toInt()
}

fun View.snackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    callback: (() -> Unit)? = null
) {
    Snackbar.make(this, message, duration).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
            callback?.invoke()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        val view = snackbar.view

        if (view.layoutParams is FrameLayout.LayoutParams) {
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER
            view.layoutParams = params
        } else {
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            view.layoutParams = params
        }
//            val params = view.layoutParams as FrameLayout.LayoutParams

        view.background =
            ContextCompat.getDrawable(this.context, R.drawable.rounded_yellow_btn_snackbar)
        val px16 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24f,
            this.resources.displayMetrics
        )
        view.updatePadding(
            top = 12,
            bottom = 12,
            left = px16.roundToInt(),
            right = px16.roundToInt()
        )

        val actionBtn =
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        actionBtn.background =
            ContextCompat.getDrawable(this.context, R.drawable.snackbar_background)

        val text = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        text.isSingleLine = false
        text.setTextColor(ContextCompat.getColor(context, R.color.white))
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }.show()
}

fun View.snackbarOnTop(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    callback: (() -> Unit)? = null
) {
    Snackbar.make(this, message, duration).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
            callback?.invoke()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        val view = snackbar.view

        if (view.layoutParams is FrameLayout.LayoutParams) {
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
        } else {
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
        }
//            val params = view.layoutParams as FrameLayout.LayoutParams

        view.background =
            ContextCompat.getDrawable(this.context, R.drawable.rounded_yellow_btn_snackbar)
        val px16 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24f,
            this.resources.displayMetrics
        )
        view.updatePadding(
            top = 12,
            bottom = 12,
            left = px16.roundToInt(),
            right = px16.roundToInt()
        )

        val actionBtn =
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        actionBtn.background =
            ContextCompat.getDrawable(this.context, R.drawable.snackbar_background)

        val text = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        text.isSingleLine = false
        text.setTextColor(ContextCompat.getColor(context, R.color.white))
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }.show()
}

fun View.autoSnackbarOnTop(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    callback: (() -> Unit)? = null
) {
    Snackbar.make(this, message, duration).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
            callback?.invoke()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        val view = snackbar.view

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT) {
                    callback?.invoke()
                }
            }

            override fun onShown(snackbar: Snackbar) {}
        })

        if (view.layoutParams is FrameLayout.LayoutParams) {
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
        } else {
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
        }
//            val params = view.layoutParams as FrameLayout.LayoutParams

        view.background =
            ContextCompat.getDrawable(this.context, R.drawable.rounded_yellow_btn_snackbar)
        val px16 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24f,
            this.resources.displayMetrics
        )
        view.updatePadding(
            top = 12,
            bottom = 12,
            left = px16.roundToInt(),
            right = px16.roundToInt()
        )

        val actionBtn =
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        actionBtn.background =
            ContextCompat.getDrawable(this.context, R.drawable.snackbar_background)

        val text = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        text.isSingleLine = false
        text.setTextColor(ContextCompat.getColor(context, R.color.white))
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }.show()
}

fun View.hideKeyboard() {/*val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)*/
}

fun View.showKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager!!.toggleSoftInputFromWindow(
        this.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY, 0
    )
}

fun CheckableImageButton.setButtonState(isChecked: Boolean) {
    this.isChecked = isChecked
}

fun List<IdWithValue>.getSelectedValueFromDefaultPicker(id: Int?): String {
    this.forEach { idWithValue ->
        if (idWithValue.id == id) return if (isCurrentLanguageFrench()) idWithValue.valueFr else idWithValue.value
    }
    return ""
}

fun spannedFromHtml(text: String): Spanned? {
    return if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION") Html.fromHtml(text)
    }
}


fun DrawerLayout.closeNavigationDrawer() {
    enableNavigationDrawer()
    closeDrawer(GravityCompat.START)
}

fun DrawerLayout.drawerSwitchState() {
    if (isDrawerOpen(GravityCompat.START)) {
        closeNavigationDrawer()
    } else {
        openDrawer(GravityCompat.START)
        enableNavigationDrawer()
    }
}

fun DrawerLayout.disableNavigationDrawer() {
    setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
}

fun DrawerLayout.enableNavigationDrawer() {
    setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
}

fun ProgressBar.smoothProgress(percent: Int) {
    val animation = ObjectAnimator.ofInt(this, "progress", percent)
    animation.duration = 400
    animation.interpolator = DecelerateInterpolator()
    animation.start()
}