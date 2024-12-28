package com.example.bulletinboard.ui.base

open class PagingAdapter<T>(
    resId: Int,
    private val limit: Int = 10
) : BaseListAdapter<T>(resId) {

    var isLocked: Boolean = false

    private var _offset: Int = 0
    val offset get() = _offset

    private var _isCompletedList: Boolean = false
    val isCompletedList get() = _isCompletedList

    override fun updateDataList(newList: List<T>) {
        super.updateDataList(newList)
        checkList(newList)
    }

    override fun insertDataList(newList: List<T>) {
        super.insertDataList(newList)
        checkList(newList)
    }

    fun reset() {
        _offset = 0
        clearDataList()
    }

    private fun checkList(newList: List<T>) {
        _isCompletedList = (newList.size < limit)
        _offset += 1
    }
}