package com.datnt.core.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


abstract class BaseDialog<T : ViewDataBinding, V>(private val item : V? = null, context: Context) : Dialog(context) {

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getBindingVariable(): Int

    protected lateinit var binding: T

    abstract fun bindView(viewBinding : T)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), null, false)

        if(item != null){
            binding.setVariable(getBindingVariable(), item)
            binding.executePendingBindings()
        }

        setContentView(binding.root)

        bindView(binding)
    }





}