package com.example.bulletinboard.ui.user

import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.ResponseDataList
import com.example.bulletinboard.data.dto.User

class UserSearchActivity: UserListActivity() {

    private var _searchKey: String? = null
    private val searchKey get() = _searchKey

    override fun doOperations() {
        _searchKey = intent.getStringExtra("searchKey")
        super.doOperations()
    }

    override suspend fun getDataResult(page: Int): Result<ResponseDataList<User>> {
        return repository.searchUsers(searchKey, page)
    }

    override fun doSearchOperation(query: String) {
        _searchKey = query
        loadData()
    }
}