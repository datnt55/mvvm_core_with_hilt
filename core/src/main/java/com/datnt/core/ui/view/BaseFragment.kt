package com.datnt.core.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.qbicles.core.view.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment() {

    protected var baseActivity: BaseActivity<*, *>? = null

    private var mRootView: View? = null

    protected lateinit var binding: T

    abstract val viewModel: V

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun bindingVariable(): Int

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun initComponent()

    abstract fun subscribeData()

    override fun onCreate(savedInstanceState: Bundle?) {
        //performDependencyInjection()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        mRootView = binding.root
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity<*, *>
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(bindingVariable(), viewModel)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        initComponent()
        subscribeData()
    }

    fun hideKeyboard() {
        baseActivity?.hideKeyboard()
    }

    fun isInitialized(): Boolean = this::binding.isInitialized
}