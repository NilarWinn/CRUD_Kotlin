package com.example.bulletinboard.ui.post

import androidx.core.view.isVisible
import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.ui.util.launchActivity

class PostEditActivity : PostCreateActivity() {

    private var _prevPost: PostToRequest? = null
    private val prevPost get() = _prevPost!!

    override fun doOperation() {
        loadData()
        binding.switchStatus.isVisible = true
        binding.btnSubmit.text = getString(R.string.action_edit)
        binding.btnClear.text = getString(R.string.action_reset)
        super.doOperation()
    }

    override fun setTitle() {
        binding.layoutToolbar.lblTitle.text = getString(R.string.title_post_edit)
    }

    override fun resetInput() {
        bindData()
    }

    override fun goToConfirmActivity() {
        launchActivity(PostEditConfirmActivity::class.java, getInputData())
    }

    override fun getInputData(): PostToRequest {
        return super.getInputData().apply {
            post_id = prevPost.post_id
            status = if (binding.switchStatus.isChecked) "1" else "0"
        }
    }

    private fun loadData() {
        val bundle = intent.extras ?: return
        _prevPost = bundle.getSerializable("data") as PostToRequest
        bindData()
    }

    private fun bindData() {
        binding.apply {
            txtTitle.editText!!.setText(prevPost.title)
            txtDescription.editText!!.setText(prevPost.description)
            switchStatus.isChecked = (prevPost.status == "1")
        }
    }
}