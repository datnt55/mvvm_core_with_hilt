package com.qbicles.core.utils.keyboard

import android.app.Activity

class KeyboardObserver(activity: Activity) : BaseKeyboardObserver(activity) {

    fun listen(onKeyboardListener: OnKeyboardListener) {
        internalListen(onKeyboardListener)
    }

    fun listen(action: (Boolean, Int) -> Unit) {
        internalListen(object : OnKeyboardListener {
            override fun onKeyboardChange(isShow: Boolean, keyboardHeight: Int) {
                action(isShow, keyboardHeight)
            }
        })
    }
}