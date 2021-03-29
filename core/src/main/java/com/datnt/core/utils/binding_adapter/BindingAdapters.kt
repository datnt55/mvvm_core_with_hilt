/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datnt.core.utils.binding_adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url)
        .fitCenter()
        .centerCrop()
//        .placeholder(R.drawable.ic_loading_image)
//        .error(R.drawable.ic_loading_image)
        .into(imageView)
}

@BindingAdapter("imageSrc")
fun setImageSrc(imageView: ImageView, url: Int?) {
    url?.let { imageView.setImageResource(url) }
}


@BindingAdapter("imageFile")
fun setImageFile(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView.context).load(File(url)).into(imageView)
    }
}

@BindingAdapter("selected")
fun setSelected(imageView: ImageView, selected: Boolean) {
    imageView.isSelected = selected
}

@BindingAdapter("nullGoneTrueVisible")
fun View.setVisibleOrGone(current: Boolean?) {
    visibility = when {
        current == null -> {
            View.GONE
        }
        current -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("nullGoneTrueGone")
fun View.setViewGoneOrVisible(current: Boolean?) {
    if (current == null){
        visibility = View.GONE
    }else
        visibility = if (current) View.GONE else View.VISIBLE
}


@BindingAdapter("visibleOrInvisible")
fun View.setVisibleOrInvisible(current: Any?) {
    visibility = if (current == null) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("objectNullGone")
fun View.setGoneOrVisible(current: Any?) {
    visibility = if (current != null) View.VISIBLE else View.GONE
}

@BindingAdapter("objectNullVisible")
fun View.setVisibleOrGone(current: Any?) {
    visibility = if (current == null) View.VISIBLE else View.GONE
}


@BindingAdapter("string_visible")
fun View.setStringVisible(current: String?) {
    current?.let {
        visibility = if (it.trim().isNotEmpty()) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("phone_number")
fun TextView.setPhoneNumber(phone: String?) {
    phone?.let {
        val realphone = if (phone.substring(0, 1) != "0") "0$phone" else phone
        text = realphone
    }
}