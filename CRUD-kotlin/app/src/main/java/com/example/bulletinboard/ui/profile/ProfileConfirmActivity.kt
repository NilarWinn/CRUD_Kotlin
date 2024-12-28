package com.example.bulletinboard.ui.profile

import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.data.service.RetrofitService
import com.example.bulletinboard.databinding.ActivityProfileEditConfirmBinding
import com.example.bulletinboard.ui.base.activity.BaseConfirmActivity
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.goToHome
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

class ProfileConfirmActivity : BaseConfirmActivity<ActivityProfileEditConfirmBinding>(
    ActivityProfileEditConfirmBinding::inflate
) {

    private var _userToConfirm: UserToRequest? = null
    private val userToConfirm get() = _userToConfirm!!

    override fun doOperation() {
        super.doOperation()
        binding.apply {
            btnSubmit.setOnClickListener { confirmToRequest() }
            btnCancel.setOnClickListener { finish() }
            btnBack.setOnClickListener { finish() }
        }
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_profile_edit_confirm)
    }

    override fun loadData() {
        _userToConfirm = intent.getSerializableExtra("user") as UserToRequest
        bindData()
    }

    override fun bindData() {

        binding.apply {

            txtName.setText(userToConfirm.name)
            txtEmail.setText(userToConfirm.email)
            txtAddress.setText(userToConfirm.address)
            txtDob.setText(userToConfirm.dob)
            txtPhone.setText(userToConfirm.phone)

            // Bind Image
            if (userToConfirm.profile != null) {
                Glide.with(this@ProfileConfirmActivity)
                    .load(userToConfirm.profile)
                    .error(R.drawable.ic_user)
                    .into(binding.imgProfile)
            } else {
                Glide.with(this@ProfileConfirmActivity)
                    .load("${RetrofitService.URL}${userToConfirm.old_profile}")
                    .error(R.drawable.ic_user)
                    .into(binding.imgProfile)
            }
        }
    }

    override fun confirmToRequest() {
        userToConfirm.type =
            if (userToConfirm.type == "Admin" || userToConfirm.type == "0") "0" else "1"
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(getRequestResult()) {
                showToast(R.string.success_save)
                goToHome()
            }
            progressDialog.hide()
        }
    }

    override suspend fun getRequestResult(): Result<Any> {
        return repository.updateProfile(userToConfirm)
    }
}
