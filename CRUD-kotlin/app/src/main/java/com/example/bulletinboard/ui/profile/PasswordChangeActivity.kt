package com.example.bulletinboard.ui.profile

import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.databinding.ActivityPasswordChangeBinding
import com.example.bulletinboard.ui.base.activity.BaseRequestActivity
import com.example.bulletinboard.ui.util.*
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.goToHome
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import kotlinx.coroutines.launch

class PasswordChangeActivity : BaseRequestActivity<ActivityPasswordChangeBinding>(
    ActivityPasswordChangeBinding::inflate
) {

    private lateinit var repository: MainRepository

    override fun doOperation() {
        super.doOperation()
        repository = (application as MainApplication).mainRepository
        binding.btnUpdate.setOnClickListener {
            if (checkInput()) goToConfirmActivity()
        }
        binding.btnClear.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_password_change)
    }

    override fun checkInput(): Boolean {

        val oldPasswordText = getString(R.string.title_password_old)
        val oldPasswordStatus = Validator.checkInput(binding.txtPasswordOld.getString())
        binding.txtPasswordOld.error = when (oldPasswordStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, oldPasswordText)
            else -> null
        }

        val (password1Status, password2Status ) = Validator.checkConfirmedPasswords(
            binding.txtPasswordNew.getString(), binding.txtPasswordConfirm.getString()
        )

        val newPasswordText = getString(R.string.title_password_new)
        binding.txtPasswordNew.error = when (password1Status) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, newPasswordText)
            InputStatus.INVALID ->
                getString(R.string.error_invalid_sth_with_msg, newPasswordText,
                getString(R.string.msg_password_invalid)
            )
            else -> null
        }

        val confirmPasswordText = getString(R.string.title_password_confirm)
        binding.txtPasswordConfirm.error = when (password2Status) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, confirmPasswordText)
            InputStatus.INVALID -> getString(
                R.string.error_invalid_sth_with_msg, confirmPasswordText,
                getString(R.string.msg_password_invalid)
            )
            InputStatus.DIFFERENCE -> getString(R.string.error_password_confirm)
            else -> null
        }

        return Validator.isAllValid(password1Status, password2Status)
    }

    override fun goToConfirmActivity() {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(repository.updatePassword(
                binding.txtPasswordOld.getString(),
                binding.txtPasswordConfirm.getString(),
            )) {
                showToast(R.string.success_save)
                goToHome()
            }
            progressDialog.hide()
        }
    }

    override fun resetInput() {
        ViewUtil.resetViewInput(binding.txtPasswordNew, binding.txtPasswordConfirm,)
    }
}