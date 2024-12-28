package com.example.bulletinboard.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class BaseListAdapter<T>(private val resId: Int) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private val list: MutableList<T> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return getViewHolder(view) as BaseViewHolder<T>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {  return list.size }

    internal open fun getViewHolder(view: View): BaseViewHolder<T>? = null

    open fun updateDataList(newList: List<T>) {
        list.addAll(newList)
        notifyItemRangeInserted(0, newList.size)
    }

    open fun insertDataList(newList: List<T>) {

        val newListSize = (newList.size)
        if (newListSize == 0) return

        val initialSize = itemCount
        list.addAll(newList)
        notifyItemRangeInserted(initialSize, newListSize)
    }

    fun deleteItem(position: Int) {
        if (position < 0) return
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(0, list.size)
    }

    internal fun clearDataList() {
        val initialSize = itemCount
        if (initialSize == 0) return
        list.clear()
        notifyItemRangeRemoved(0, initialSize)
    }
}
