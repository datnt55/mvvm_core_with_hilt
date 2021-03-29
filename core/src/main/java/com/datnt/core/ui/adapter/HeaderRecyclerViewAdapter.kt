package com.datnt.core.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class HeaderRecyclerViewAdapter<VH : RecyclerView.ViewHolder,H : RecyclerView.ViewHolder, T> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataSource: ArrayList<Any?> = arrayListOf()

    /**
     * Invokes onCreateHeaderViewHolder, onCreateItemViewHolder or onCreateFooterViewHolder methods
     * based on the view type param.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isHeaderType(viewType)) {
            onCreateHeaderViewHolder(parent, viewType)
        } else {
            onCreateItemViewHolder(parent, viewType)
        }
    }

    /**
     * If you don't need header feature, you can bypass overriding this method.
     */
    protected abstract fun onCreateHeaderViewHolder(parent: ViewGroup, viewType: Int): H

    protected abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * Invokes onBindHeaderViewHolder, onBindItemViewHolder or onBindFooterViewHOlder methods based
     * on the position param.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            isHeaderPosition(position) -> {
                onBindHeaderViewHolder(holder as H, position)
            }
            else -> {
                onBindItemViewHolder(holder as VH, position)
            }
        }
    }

    /**
     * If you don't need header feature, you can bypass overriding this method.
     */
    protected abstract fun onBindHeaderViewHolder(holder: H, position: Int)

    protected abstract fun onBindItemViewHolder(holder: VH, position: Int)

    /**
     * Invokes onHeaderViewRecycled, onItemViewRecycled or onFooterViewRecycled methods based
     * on the holder.getAdapterPosition()
     */
//    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
//        val position: Int = holder.adapterPosition
//        if (isHeaderPosition(position)) {
//            onHeaderViewRecycled(holder as H)
//        } else {
//            onItemViewRecycled(holder as VH)
//        }
//    }
//
//    protected fun onHeaderViewRecycled(holder: H) {}
//
//    protected fun onItemViewRecycled(holder: VH) {}

    /**
     * Returns the type associated to an item given a position passed as arguments. If the position
     * is related to a header item returns the constant TYPE_HEADER or TYPE_FOOTER if the position is
     * related to the footer, if not, returns TYPE_ITEM.
     *
     * If your application has to support different types override this method and provide your
     * implementation. Remember that TYPE_HEADER, TYPE_ITEM and TYPE_FOOTER are internal constants
     * can be used to identify an item given a position, try to use different values in your
     * application.
     */
    override fun getItemViewType(position: Int): Int {
        var viewType = TYPE_ITEM
        if (isHeaderPosition(position)) {
            viewType = TYPE_HEADER
        }
        return viewType
    }

    /**
     * Returns the items list size if there is no a header configured or the size taking into account
     * that if a header or a footer is configured the number of items returned is going to include
     * this elements.
     */
    override fun getItemCount(): Int {
        return dataSource.size
    }

    /**
     * Get item data in this adapter with the specified postion,
     * you should previously use [.setHeader]
     * in the adapter initialization code to set header data.
     *
     * @return item data in the specified postion
     */
    fun getItem(position: Int): Any? {
        return dataSource[position]
    }

    /**
     * You should set header data in the adapter initialization code.
     *
     * @param items item data list
     */
    fun setItems(items: List<Any>) {
        //validateItems(items)
        //this.items = items
        this.dataSource.clear()
        this.dataSource.addAll(items)
        notifyDataSetChanged()
    }

    fun clearDataSource(){
        this.dataSource.clear()
        notifyDataSetChanged()
    }

    fun append(item: Any?) {
        this.dataSource.add(item)
        notifyItemInserted(dataSource.size -1)
    }

    fun appendAll(item: List<Any>) {
        if (dataSource.isEmpty()) {
            setItems(item)
        }else {
            val lastPost = dataSource.size
            this.dataSource.addAll(item)
            notifyDataSetChanged()
        }
    }

    /**
     * Returns true if the position type parameter passed as argument is equals to 0 and the adapter
     * has a not null header already configured.
     */
     abstract fun isHeaderPosition(position: Int): Boolean

    /**
     * Returns true if the view type parameter passed as argument is equals to TYPE_HEADER.
     */
    protected fun isHeaderType(viewType: Int): Boolean {
        return viewType == TYPE_HEADER
    }

    /**
     * Returns true if the item configured is not empty.
     */
    private fun hasItems(): Boolean = dataSource.isNotEmpty()

    private fun validateItems(items: List<T>?) {
        requireNotNull(items) { "You can't use a null List<Item> instance." }
    }

    companion object {
        const val TYPE_FOOTER = -3
        const val TYPE_HEADER = -2
        const val TYPE_ITEM = -1
    }
}