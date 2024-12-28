package com.example.bulletinboard.ui.post

import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.databinding.ActivityPostCreateBinding
import com.example.bulletinboard.ui.base.activity.BaseRequestActivity
import com.example.bulletinboard.ui.util.*

open class PostCreateActivity: BaseRequestActivity<ActivityPostCreateBinding>(
    ActivityPostCreateBinding::inflate
) {

    override fun doOperation() {
        super.doOperation()
        binding.btnSubmit.setOnClickListener {
            if (checkInput()) { goToConfirmActivity() }
            else { showToast(getString(R.string.error_unknown)) }
        }
        binding.btnClear.setOnClickListener { resetInput() }
        binding.layoutToolbar.btnBack.setOnClickListener { finish() }
    }


    override fun setTitle() {
        binding.layoutToolbar.lblTitle.text = getString(R.string.title_post_add)
    }

    override fun checkInput(): Boolean {

        val title = getString(R.string.title)
        val titleStatus = Validator.checkInput(binding.txtTitle.getString())
        binding.txtTitle.error = when (titleStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, title)
            else -> null
        }

        val description = getString(R.string.title_description)
        val descriptionStatus = Validator.checkInput(binding.txtDescription.getString())
        binding.txtDescription.error = when (descriptionStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, description)
            else -> null
        }

        return Validator.isAllValid(titleStatus, descriptionStatus)
    }

    override fun resetInput() {
        ViewUtil.resetViewInput(
            binding.txtTitle,
            binding.txtDescription
        )
    }

    override fun goToConfirmActivity() {
        launchActivity(PostCreateConfirmActivity::class.java, getInputData())
    }

    internal open fun getInputData(): PostToRequest {
        return PostToRequest(
            binding.txtTitle.getString(),
            binding.txtDescription.getString(),
        )
    }
}