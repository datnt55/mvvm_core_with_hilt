package com.datnt.core.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

open class BaseHolder<V>(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    open fun bind(data: V, position: Int){}
    open fun unbind(){}
    open fun baseBind(data: V, position: Int) {}
    open fun event() {}
    var sizeItem = 0
    var adapter: BaseAdapter<V, *>? = null
    override val containerView: View?
        get() = itemView
}