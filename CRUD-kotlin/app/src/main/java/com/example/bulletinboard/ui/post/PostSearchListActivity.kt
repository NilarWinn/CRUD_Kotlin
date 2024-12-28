package com.example.bulletinboard.ui.post

import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.Post
import com.example.bulletinboard.data.dto.ResponseDataList

class PostSearchListActivity: PostListActivity() {

    private var _searchKey: String? = null
    private val searchKey get() = _searchKey

    override fun doOperations() {
        _searchKey = intent.getStringExtra("searchKey")
        super.doOperations()
    }

    override suspend fun getDataResult(page: Int): Result<ResponseDataList<Post>> {
        return repository.searchPosts(searchKey, page)
    }

    override fun doSearchOperation(query: String) {
        _searchKey = query
        loadData()
    }
}