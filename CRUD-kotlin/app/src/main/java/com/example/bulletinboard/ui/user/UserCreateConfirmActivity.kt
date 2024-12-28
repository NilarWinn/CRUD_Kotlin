package com.example.bulletinboard.ui.user

import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.ResponseResult
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.data.service.RetrofitService
import com.example.bulletinboard.databinding.ActivityUserCreateConfirmBinding
import com.example.bulletinboard.ui.base.activity.BaseConfirmActivity
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.goToHome
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

open class UserCreateConfirmActivity : BaseConfirmActivity<ActivityUserCreateConfirmBinding>(
    ActivityUserCreateConfirmBinding::inflate
) {
    private var _userToConfirm: UserToRequest? = null
    internal val userToConfirm get() = _userToConfirm!!

    override fun doOperation() {
        super.doOperation()
        binding.btnSubmit.setOnClickListener { confirmToRequest() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
        bindData()
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_user_create_confirm)
    }

    override fun loadData() {
        _userToConfirm = intent.getSerializableExtra("data") as UserToRequest
    }

    override fun bindData() {
        binding.txtName.setText(userToConfirm.name)
        binding.txtEmail.setText(userToConfirm.email)
        binding.txtPassword.setText(userToConfirm.password)
        binding.txtType.setText(userToConfirm.type)
        binding.txtPhone.setText(userToConfirm.phone)
        binding.txtDob.setText(userToConfirm.dob)
        binding.txtAddress.setText(userToConfirm.address)

        if (userToConfirm.profile == null) {
            Glide.with(this)
                .load("${RetrofitService.URL}${userToConfirm.old_profile}")
                .into(binding.imgProfile)
        } else {
            Glide.with(this)
                .load(userToConfirm.profile)
                .into(binding.imgProfile)
        }
    }

    override fun confirmToRequest() {
        userToConfirm.type =
            if (userToConfirm.type == "Admin" || userToConfirm.type == "0") "0" else "1"
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.show() }) {
            progressDialog.show()
            handleResult(getRequestResult()) {
                showToast(R.string.success_save)
                goToHome()
            }
            progressDialog.hide()
        }
    }

    override suspend fun getRequestResult(): Result<ResponseResult> {
        return repository.createUser(userToConfirm)
    }
}