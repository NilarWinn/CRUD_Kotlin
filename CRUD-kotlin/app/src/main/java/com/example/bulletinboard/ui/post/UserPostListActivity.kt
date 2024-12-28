package com.example.bulletinboard.ui.post

import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.Post
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.data.dto.ResponseDataList
import com.example.bulletinboard.ui.base.activity.BaseListActivity
import com.example.bulletinboard.ui.util.launchActivity

class UserPostListActivity: BaseListActivity<Post>(PostListAdapter()), PostViewHolderTouchListener {

    init {
        (adapter as PostListAdapter).touchListener  = this
    }

    override fun doOperations() {
        (adapter as PostListAdapter).context = this
        super.doOperations()
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_user_posts)
    }

    override suspend fun getDataResult(page: Int): Result<ResponseDataList<Post>> {
        return repository.getUserPosts(page)
    }

    override fun onTouch(post: Post, position: Int) {
        android.util.Log.d("aaa", post.id)
        launchActivity(
            PostEditActivity::class.java,
            PostToRequest(post.title, post.description, post.id, post.status)
        )
    }
}