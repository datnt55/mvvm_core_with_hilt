package com.datnt.core.utils.extension

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


fun Int.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun String.toInt(): Int {
    return Integer.parseInt(this)
}

fun createTimeStampFile(extension: String): String {
    val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
    return "IMG_${sdf.format(Date())}.$extension"
}


fun String.checkPasswordInValid(): String {
    var pattern: Pattern
    var matcher: Matcher

    // RequireNonLetterOrDigit
    val letterPattern = "([0-9])"

    pattern = Pattern.compile(letterPattern)
    matcher = pattern.matcher(this)

    if  (!matcher.find())
        return "Password must contain at least a digit"

    //RequireLowercase
    val lowerCasePattern = "([a-z])"
    pattern = Pattern.compile(lowerCasePattern)
    matcher = pattern.matcher(this)

    if  (!matcher.find())
        return "Password must contain at least a lowercase character"

    //RequireLowercase
    val upperCasePattern = "([A-Z])"
    pattern = Pattern.compile(upperCasePattern)
    matcher = pattern.matcher(this)

    if  (!matcher.find())
        return "Password must contain at least a uppercase character"

    val SPECIAL_REGEX_CHARS = "[^(A-Za-z0-9 )]"
    pattern = Pattern.compile(SPECIAL_REGEX_CHARS)
    matcher = pattern.matcher(this)

    if  (!matcher.find())
        return "Password must contain at least a special character"

    return ""
}

