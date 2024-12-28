package com.example.bulletinboard.ui.post

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.data.dto.ResponseResult
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.goToHome
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

class PostEditConfirmActivity : PostCreateConfirmActivity() {

    override fun doOperation() {
        super.doOperation()
        binding.switchStatus.isVisible = true
    }

    override fun loadData() {
        val bundle: Bundle = intent.extras ?: return
        _postToConfirm = bundle.getSerializable("data") as PostToRequest
        bindData(postToConfirm!!)
    }

    override fun bindData(post: PostToRequest) {
        super.bindData(post)
        binding.switchStatus.isChecked = (post.status == "1")
    }

    override fun confirmToRequest() {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(getResponseResult()) {
                showToast(R.string.success_update)
                goToHome()
            }
            progressDialog.hide()
        }
    }

    override suspend fun getResponseResult(): Result<ResponseResult> {
        return repository.updatePost(postToConfirm!!)
    }
}