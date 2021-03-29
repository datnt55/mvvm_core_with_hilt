package com.datnt.core.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseAdapter<V, VH : BaseHolder<V>>() : ListAdapter<V, VH>(DiffCallback<V>()) {
    var dataSource: ArrayList<V> = ArrayList()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.sizeItem = dataSource.size
        holder.adapter = this
        holder.bind(getItem(position), position)
        holder.event()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }


    override fun getItem(position: Int): V {
        return dataSource[position]

    }

    override fun getItemCount() = dataSource.size

    open fun setDataSource(dataSource: List<V>) {
        val newList = ArrayList(dataSource)
        submit(newList)
    }

    fun refreshData(dataSource: ArrayList<V>) {
//        submit(dataSource)
//        notifyDataSetChanged()
    }

    fun clearAll() {
        submit(arrayListOf())
    }

    fun clearDataSource() {
        this.dataSource.clear()
        notifyDataSetChanged()
    }

    fun getDataSource(): List<V> {
        return dataSource
    }

    fun appendItem(item: V) {

        this.dataSource.add(item)
        notifyItemInserted(itemCount)
    }

    fun appendItemPosition(item: V, position: Int) {
        if (this.dataSource.isEmpty() || position < 0 || position > this.dataSource.size) {
            return
        }
        this.dataSource.add(position, item)
        notifyItemInserted(position)
    }

    fun updateItem(position: Int, item: V) {
        if (this.dataSource.size > position) {
            dataSource[position] = item
            notifyItemChanged(position)
        }
    }

    fun setDataSourceChange(value: List<V>) {
        this.dataSource.clear()
        this.dataSource.addAll(value)
        notifyDataSetChanged()
    }


    fun removeAtPosition(position: Int) {
        if (position >= 0 && this.dataSource.size > position) {
            dataSource.removeAt(position)
            notifyItemRangeRemoved(position, 1)
        }
    }

    fun removeItem(item: V) {
        val position = dataSource.indexOf(item)
        if (position > -1) {
            dataSource.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun appendItems(items: List<V>) {
        if (dataSource.isEmpty()) {
            setDataSource(items)
        } else {
            dataSource.addAll(items)
            notifyItemRangeInserted(itemCount, items.size)
        }
    }


    protected fun submit(newList: ArrayList<V>) {
        autoNotify(this.dataSource, newList)
    }


    fun autoNotify(old: List<V>, new: List<V>) {
        this@BaseAdapter.dataSource.clear()
        this@BaseAdapter.dataSource.addAll(new)
    }

    class DiffCallback<V> :  DiffUtil.ItemCallback<V>(){
        override fun areItemsTheSame(oldItem: V, newItem: V): Boolean {
            return if (oldItem is RecyclerItemComparator && newItem is RecyclerItemComparator) {
                oldItem.isSameItem(newItem)
            } else oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: V, newItem: V): Boolean {
            return if (oldItem is RecyclerItemComparator && newItem is RecyclerItemComparator) {
                oldItem.isSameContent(newItem)
            } else
                false
        }

    }

    interface RecyclerItemComparator {
        fun isSameItem(other: Any): Boolean
        fun isSameContent(other: Any): Boolean
    }
}