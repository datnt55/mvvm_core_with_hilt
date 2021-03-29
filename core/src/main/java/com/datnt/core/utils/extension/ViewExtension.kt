package com.datnt.core.utils.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.SystemClock
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.datnt.core.R
import com.datnt.core.utils.Constants.Companion.DEFAULT_INTERVAL
import com.google.android.material.tabs.TabLayout
import java.text.Normalizer

fun AppCompatImageView.setTint(@ColorRes color: Int) {
    setColorFilter(ContextCompat.getColor(context, color))
}

fun View.gone() {
    if (this.visibility != View.GONE) this.visibility = View.GONE
}

fun View.visible() {
    if (this.visibility != View.VISIBLE) this.visibility = View.VISIBLE
}

fun View.invisible() {
    if (this.visibility != View.INVISIBLE) this.visibility = View.INVISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.getParentActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun View.setOnSafeClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener { onSafeClick(it) }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(private val onSafeCLick: (View) -> Unit) : View.OnClickListener {
    companion object {
        var lastTimeClicked: Long = 0
    }

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < DEFAULT_INTERVAL) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun CharSequence.unAccent(): String {

    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return regex.replace(temp, "")
        .replace('đ', 'd').replace('Đ', 'd')
}

fun View.setMarginTop(marginTop: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, marginTop, 0, 0)
    this.layoutParams = menuLayoutParams
}

val ViewPager.lastItem: Int?
    get() = adapter?.count?.minus(1)

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#1ABE60")
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.colorTextOfString(colorString: String, vararg links: Pair<String, String>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.color = Color.parseColor(colorString)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun View.setBackgroundTint(@ColorRes color: Int){
    this.backgroundTintList = ContextCompat.getColorStateList(context,color)
}

fun RecyclerView.betterSmoothScrollToPosition(targetItem: Int) {
    layoutManager?.apply {
        val maxScroll = 10
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - targetItem
                val anchorItem = when {
                    distance > maxScroll -> targetItem + maxScroll
                    distance < -maxScroll -> targetItem - maxScroll
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post {
                    smoothScrollToPosition(targetItem)
                }
            }
            else -> smoothScrollToPosition(targetItem)
        }
    }
}

fun ImageView.loadImage(@RawRes @DrawableRes drawableResDefault: Int, imagePath: String?) {
    if (imagePath.isNullOrEmpty()) {
        Glide.with(context).load(drawableResDefault).into(this)
        return
    }
    Glide.with(context).load(imagePath)
        .fitCenter()
        .placeholder(drawableResDefault)
        .error(drawableResDefault)
        .into(this)
}

fun TabLayout.wrapTabIndicatorToTitle(externalMargin: Int, internalMargin: Int) {
    val tabStrip = this.getChildAt(0)
    if (tabStrip is ViewGroup) {
        val childCount = tabStrip.childCount
        for (i in 0 until childCount) {
            val tabView = tabStrip.getChildAt(i)
            //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
            tabView.minimumWidth = 0
            // set padding to 0 for wrapping indicator as title
            tabView.setPadding(0, tabView.paddingTop, 0, tabView.paddingBottom)
            // setting custom margin between tabs
            if (tabView.layoutParams is ViewGroup.MarginLayoutParams) {
                val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
                when (i) {
                    0 -> {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin)
                    }
                    childCount - 1 -> {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin)
                    }
                    else -> {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin)
                    }
                }
            }
        }
        this.requestLayout()
    }
}

private fun settingMargin(layoutParams: ViewGroup.MarginLayoutParams, start: Int, end: Int) {
    layoutParams.marginStart = start
    layoutParams.marginEnd = end
    layoutParams.leftMargin = start
    layoutParams.rightMargin = end
}

fun View.setMarginBottom(bottomMargin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin)
    layoutParams = params
}

fun View.setHeight(height: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.height = height
    layoutParams = params
}

fun Activity.detectKeyboardShow(contentView: View, onShow: (() -> Unit)? = null) {
// ContentView is the root view of the layout of this activity/fragment
    var isKeyboardShowing = false
    contentView.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = contentView.rootView.height
        val keypadHeight: Int = screenHeight - rect.bottom
        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            // keyboard is opened
            if (!isKeyboardShowing) {
                isKeyboardShowing = true
                onShow?.invoke()
            }
        } else {
            // keyboard is closed
            if (isKeyboardShowing) {
                isKeyboardShowing = false
            }
        }
    }
}

fun RecyclerView.smoothScrollTo(position: Int) {
    val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}


fun View.setMargin(marginTop: Int, marginBot: Int, marginLeft: Int, marginRight: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBot)
    this.layoutParams = menuLayoutParams
}

fun ImageView.loadImageCircle(imagePath: String, @DrawableRes drawableDefault: Int) {
    if (context is Activity) {
        val activity = context as Activity?
        if (activity == null || activity.isDestroyed) {
            return
        }
    }
    if (imagePath.isNullOrEmpty()) {
        Glide.with(context).load(drawableDefault).into(this)
        return
    }
    Glide.with(context).load(imagePath)
        .error(drawableDefault)
        .into(this)
}

fun RecyclerView.setLoadMore(loadMore: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            layoutManager?.itemCount?.let {
                val totalItemCount = it
                val lastVisible =
                    (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                val endHasBeenReached: Boolean = lastVisible + 1 >= totalItemCount

                if (totalItemCount > 0 && endHasBeenReached) {
                    loadMore.invoke()
                }
            }
        }
    })
}

fun View.setMarginTopBottom(margrinTop: Int, margrinBottom: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, margrinTop, 0, margrinBottom)
    this.layoutParams = menuLayoutParams
}

fun TextView.setCustomTextAppearance(resId: Int) {
    if (Build.VERSION.SDK_INT < 23) {
        this.setTextAppearance(context, resId)
    } else {
        this.setTextAppearance(resId)
    }
}

fun View.getSizeHeight(onFinish: (width: Int, height: Int) -> Unit) {
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                onFinish.invoke(width, height)
            }
        })
    }
}

inline fun <reified T : Fragment>  AppCompatActivity.addFragment( fragment: T){
    val tag = fragment.javaClass.simpleName
    val fragmentManager = supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
        .add(R.id.container, fragment, tag)
    fragmentManager.addToBackStack(null).commit()
}

fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.7f
    wm.updateViewLayout(container, p)
}
