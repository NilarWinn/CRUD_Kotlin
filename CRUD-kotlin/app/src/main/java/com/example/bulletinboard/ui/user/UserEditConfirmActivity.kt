package com.example.bulletinboard.ui.user

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.ResponseResult
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.goToHome
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

class UserEditConfirmActivity: UserCreateConfirmActivity() {

    override fun doOperation() {
        super.doOperation()
        binding.txtPassword.isVisible = false
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_user_edit_confirm)
    }

    override fun confirmToRequest() {
        userToConfirm.type = if (userToConfirm.type == "Admin" || userToConfirm.type == "0") "0" else "1"
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(getRequestResult()) {
                showToast(R.string.success_update)
                goToHome()
            }
            progressDialog.hide()
        }
    }

    override suspend fun getRequestResult(): Result<ResponseResult> {
        return repository.updateUser(userToConfirm)
    }
}