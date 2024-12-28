package com.example.bulletinboard.ui.post

import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.Post
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.data.dto.ResponseDataList
import com.example.bulletinboard.ui.base.activity.BaseListAndSearchActivity
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.launchActivity
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

open class PostListActivity : BaseListAndSearchActivity<Post>(PostListAdapter()), PostViewHolderMenuListener {

    init { (adapter as PostListAdapter).menuListener = this }

    override fun doOperations() {
        (adapter as PostListAdapter).context = this
        super.doOperations()
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_post_list)
    }

    override suspend fun getDataResult(page: Int): Result<ResponseDataList<Post>> {
        return repository.getPosts(page)
    }

    override fun onEditMenuClick(post: Post, position: Int) {
        launchActivity(
            PostEditActivity::class.java,
            PostToRequest(post.title, post.description, post.id, post.status)
        )
    }

    override fun onDeleteMenuClick(post: Post, position: Int) {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() } ) {
            progressDialog.show()
            when (val result = repository.deletePost(PostToRequest(null, null, post.id))) {
                is Result.Success -> {
                    adapter.deleteItem(position)
                    showToast(R.string.success_delete)
                }
                else -> showToast(result.toString())
            }
            progressDialog.hide()
        }
    }


    override fun doSearchOperation(query: String) {
        launchActivity(
            PostSearchListActivity::class.java, bundleOf("searchKey" to query)
        )
    }
}