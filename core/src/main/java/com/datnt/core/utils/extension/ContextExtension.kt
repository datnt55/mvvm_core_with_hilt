@file:Suppress("DEPRECATION")

package com.datnt.core.utils.extension

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.datnt.core.R
import com.datnt.core.utils.Constants.Companion.ANIMATION_FADE
import com.datnt.core.utils.Constants.Companion.ANIMATION_SLIDE_LEF_TO_RIGHT
import com.datnt.core.utils.Constants.Companion.ANIMATION_SLIDE_RIGHT_TO_LEFT
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun Activity.makeStatusBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun Context.showMessage(@StringRes message : Int){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showMessage(message : String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.changeStatusBarColor(color: Int, lighStatus: Boolean? = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val startColor = this.window.statusBarColor
        val endColor = ContextCompat.getColor(this, color)
        ObjectAnimator.ofArgb(this.window, "statusBarColor", startColor, endColor).start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && lighStatus!!) {
            this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

inline fun <reified T : Activity> Context.openActivity(extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

inline fun <reified T : Any> Activity.extra(key: String, default: T? = null) = lazy {
    val value = intent?.extras?.get(key)
    if (value is T) value else default
}

fun AppCompatActivity.loadFragment(
    currentFragment: Fragment?,
    fragment: Fragment?,
    tag: String = "",
    replace: Boolean = false
): Fragment? {
    fragment?.let {
        var animationId = 0
        currentFragment?.let {
            animationId = if (it.tag!!.toInt() > tag.toInt())
                ANIMATION_SLIDE_LEF_TO_RIGHT
            else
                ANIMATION_SLIDE_RIGHT_TO_LEFT
        }
        //animationId = ANIMATION_FADE
        val transaction = getCustomFragmentTransaction(animationId)
        if (supportFragmentManager.fragments.contains(fragment)) {
            currentFragment?.let {
                transaction.hide(it)
            }
            transaction.show(fragment)
            transaction.commitAllowingStateLoss()
        } else {
            val removeFrag = supportFragmentManager.fragments.firstOrNull { x -> x.javaClass == fragment.javaClass }
            removeFrag?.let {
                if (replace)
                    transaction.remove(removeFrag)
            }
            transaction.add(R.id.container, fragment, tag)
            currentFragment?.let {
                transaction.hide(it)
            }
            transaction.commit()
        }

        return fragment
    }
    return null
}

fun Context.checkPermissions(
    vararg permissions: String,
    onGranted: () -> Unit,
    onDeny: () -> Unit
) {
    val neededPermissionsCheck = permissions.filter {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
    }.toTypedArray()

    if (neededPermissionsCheck.isNotEmpty()) {
        val listener = object : PermissionListener {
            override fun onPermissionGranted() {
                onGranted.invoke()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                onDeny.invoke()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(listener)
            .setPermissions(*neededPermissionsCheck)
            .check()
    } else {
        onGranted.invoke()
    }
}

fun Context.getWidthScreen(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.getHeightScreen(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Context.hasInternetConnection(): Boolean {
    var result = false
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}

fun Context.createImageFile(): File? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "img_" + timeStamp + "_"
    val storageDir = File(this.getExternalFilesDir(null).toString() + File.separator + "F99")
    if (!storageDir.exists()) storageDir.mkdirs()
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}

fun Context.rotateImage(uri: Uri): Uri? {
    var ei: ExifInterface? = null
    var rotationAngle = 0
    try {
        ei = ExifInterface(uri.toString())
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotationAngle = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> rotationAngle = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> rotationAngle = 270
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return rotate(uri, rotationAngle.toFloat(), this)
}

private fun rotate(photoURI: Uri, angle: Float, context: Context): Uri? {
    val source: Bitmap
    try {
        source = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, photoURI)
        } else {
            val photo = ImageDecoder.createSource(context.contentResolver, photoURI)
            ImageDecoder.decodeBitmap(photo)
        }
        val matrix = Matrix()
        matrix.postRotate(angle)
        val des = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        val bytes = ByteArrayOutputStream()
        des.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, des,
            "ThisIsImageTitleString" + " - " + (Calendar.getInstance().getTime()),
            null
        )
        return Uri.parse(path)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun Activity.setStatusBarColor(color: Int, visibility: Int) {
    window.statusBarColor = ContextCompat.getColor(this, color)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or visibility
}

fun AppCompatActivity.getCustomFragmentTransaction(animationId: Int): FragmentTransaction {
    val transaction = supportFragmentManager.beginTransaction()
    when (animationId) {
        ANIMATION_SLIDE_RIGHT_TO_LEFT -> {
            transaction.setCustomAnimations(
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left,
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right
            )
        }
        ANIMATION_SLIDE_LEF_TO_RIGHT -> {
            transaction.setCustomAnimations(
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right,
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left
            )
        }
        ANIMATION_FADE -> {
            transaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
        }
    }
    return transaction
}

fun Activity.setLanguage(language: String?) {
    val res = resources
    val dm = res.displayMetrics
    val config = res.configuration
    val locale = Locale(language)
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocale(locale)
            config.setLocales(localeList)
            createConfigurationContext(config)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            config.setLocale(locale)
        }
        else -> {
            config.locale = locale
        }
    }
    resources.updateConfiguration(config, dm)
}

fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

fun Context.convertPxToDp(px: Float): Float {
    return px / resources.displayMetrics.density
}


fun Activity.detectKeyboardShow(
    onShow: ((height: Int) -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
// ContentView is the root view of the layout of this activity/fragment
    var isKeyboardShowing = false
    val contentView = findViewById<View>(android.R.id.content)
    contentView.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = contentView.rootView.height
        val keypadHeight: Int = screenHeight - rect.bottom
        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            // keyboard is opened
            if (!isKeyboardShowing) {
                isKeyboardShowing = true
                onShow?.invoke(keypadHeight)
                //contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        } else {
            // keyboard is closed
            if (isKeyboardShowing) {
                isKeyboardShowing = false

            }
        }
    }
}

fun Activity.is189ScreenAbove(): Boolean {
    val activity = this
    val metrics = DisplayMetrics().apply {
        activity.windowManager.defaultDisplay.getMetrics(this)
    }
    val ratio = metrics.heightPixels.toFloat() / metrics.widthPixels.toFloat()

    return ratio > (17 / 9)
}

interface RequestPermissionListener {
    /**
     * Callback on permission previously denied
     * should show permission rationale and continue permission request
     */
    fun onPermissionRationaleShouldBeShown(requestPermission: () -> Unit)

    /**
     * Callback on permission "Never show again" checked and denied
     * should show message and open app setting
     */
    fun onPermissionPermanentlyDenied(openAppSetting: () -> Unit)

    /**
     * Callback on permission granted
     */
    fun onPermissionGranted()
}

fun shouldRequestRuntimePermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

fun Context.shouldAskPermissions(permissions: String): Boolean {
    if (shouldRequestRuntimePermission()) {

        if (ContextCompat.checkSelfPermission(this, permissions)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

    }
    return false
}

@RequiresApi(Build.VERSION_CODES.M)
fun shouldShowRequestPermissionsRationale(context: Context, permissions: String): Boolean {
    if ((context as Activity).shouldShowRequestPermissionRationale(permissions)) {
        return true
    }
//    var a = ContextCompat.checkSelfPermission(context, permissions)
//    if (ContextCompat.checkSelfPermission(context, permissions) == -1){
//        return true
//    }
    return false
}

fun Context.isFirstTimeAskingPermissions(permissions: String): Boolean {
    val sharedPreference: SharedPreferences? = getSharedPreferences(packageName, MODE_PRIVATE)
    if (sharedPreference?.getBoolean(permissions, true) == true) {
        return true
    }
    return false
}

fun Context.firstTimeAskingPermissions(permissions: String, isFirstTime: Boolean) {
    val sharedPreference: SharedPreferences? = getSharedPreferences(packageName, MODE_PRIVATE)
    sharedPreference?.edit()?.putBoolean(permissions, isFirstTime)?.apply()
}

fun Context.openAppDetailSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun Context.requestPermissions2(
    context: Context,
    permissions: String,
    permissionRequestCode: Int,
    requestPermissionListener: RequestPermissionListener
) {
    // permissions is not granted
    if (shouldAskPermissions(permissions)) {
        // permissions denied previously
        var a = shouldShowRequestPermissionsRationale(context, permissions)
        if (a) {
            requestPermissionListener.onPermissionRationaleShouldBeShown {
//                requestPermissions2(
//                    context,
//                    permissions,
//                    permissionRequestCode,
//                    requestPermissionListener
//                )
            }
        } else {
            // Permission denied or first time requested
            var b = context.isFirstTimeAskingPermissions(permissions)
            if (b) {
                context.firstTimeAskingPermissions(permissions, false)
                // request permissions
                ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99
                )
//                requestPermissions2(
//                    context,
//                    permissions,
//                    permissionRequestCode,
//                    requestPermissionListener
//                )

            } else {
                // permission disabled
                // Handle the feature without permission or ask user to manually allow permission
                requestPermissionListener.onPermissionPermanentlyDenied {
                }
            }
        }
    } else {
        // permission granted
        requestPermissionListener.onPermissionGranted()
    }
}