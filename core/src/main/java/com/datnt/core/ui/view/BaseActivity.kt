package com.datnt.core.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.datnt.core.R
import com.qbicles.core.view.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> : AppCompatActivity() {

    protected lateinit var binding: T

    abstract val mViewModel: V

    abstract fun getBindingVariable(): Int

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initComponent()

    abstract fun subscribeData()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        performDataBinding()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(false)
            window.statusBarColor = Color.TRANSPARENT
        }
        initComponent()
        subscribeData()
    }

    private fun setWindowFlag(on: Boolean) {
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }


    protected fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility // get current flag
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
            window.decorView.systemUiVisibility = flags
        }
    }

    protected fun clearLightStatusBar() {
        val view = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            view.systemUiVisibility = flags
        }
    }


    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.setVariable(getBindingVariable(), mViewModel)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}