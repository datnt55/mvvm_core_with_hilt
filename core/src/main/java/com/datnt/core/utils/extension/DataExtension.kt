package com.datnt.core.utils.extension

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.Normalizer


inline fun <reified T> fromJson(json: String): T {
    return Gson().fromJson(json, object : TypeToken<T>() {}.type)
}

fun <T1, T2, T3, R> zip3(
    first: Flow<T1>,
    second: Flow<T2>,
    third: Flow<T3>,
    transform: suspend (T1, T2, T3) -> R
): Flow<R> =
    first.zip(second) { a, b -> a to b }
        .zip(third) { (a, b), c ->
            transform(a, b, c)
        }

// xóa dấu tiếng việt
fun String.unaccent(): String {
    val regexA = Regex("[^\\p{ASCII}]")
    return Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace(regexA, "")
}

fun String.deleteAccentsDeleteSpacesLowercaseLetters():String {
    val c = this.unaccent()
    val a = c.replace(" ", "")
    val b = a.toLowerCase()
    return b
}

fun String.isPhoneNumber() : Boolean{
    val actualPhone = if (this.subSequence(0, 1) == "0") this.substring(1) else this
    val valid =  Patterns.PHONE.matcher(this).matches()
    return valid && this.length == 9
}

fun String.toPhoneFormat(): String {
    return if (this.subSequence(0, 1) == "0") this.substring(1) else this
}

fun String.isEmail() : Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun Int.getDayCount(): String {
    val lastDigit =  when (if (this < 20) this else this % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }

    return "$this$lastDigit"
}

@Throws(IOException::class)
fun Uri.readContentToFile(context: Context): File {
    val file: File = File(context.cacheDir, getDisplayName(context,this))
    context.contentResolver.openInputStream(this).use { `in` ->
        FileOutputStream(file, false).use { out ->
            val buffer = ByteArray(1024)
            var len: Int
            while (`in`!!.read(buffer).also { len = it } != -1) {
                out.write(buffer, 0, len)
            }
            return file
        }
    }
}

private fun getDisplayName(context: Context, uri: Uri): String {
    val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
    context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
        cursor?.let {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
    }
    // If the display name is not found for any reason, use the Uri path as a fallback.
    return uri.path.toString()
}

infix fun <T> Collection<T>.sameContentWith(collection: Collection<T>)
        = collection.let { this.size == it.size && this.containsAll(it) }