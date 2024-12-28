package com.example.bulletinboard.ui.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.databinding.ActivityUserCreateBinding
import com.example.bulletinboard.ui.base.activity.BaseRequestActivity
import com.example.bulletinboard.ui.custom.DatePickerDialog
import com.example.bulletinboard.ui.util.*
import java.io.File

open class UserCreateActivity : BaseRequestActivity<ActivityUserCreateBinding>(
    ActivityUserCreateBinding::inflate
) {
    internal lateinit var datePickerDialog: DatePickerDialog

    private var _selectedFile: File? = null
    internal val selectedFile get() = _selectedFile

    override fun doOperation() {
        super.doOperation()

        binding.btnSubmit.setOnClickListener {
            if (checkInput()) goToConfirmActivity()
            else showToast(getString(R.string.error_unknown))
        }

        binding.btnClear.setOnClickListener { resetInput() }
        binding.toolBar.btnBack.setOnClickListener { finish() }

        val showCalendar = {
            hideKeyboard()
            datePickerDialog.show()
        }

        binding.userForm.txtDob.editText!!.apply {
            datePickerDialog = DatePickerDialog(
                this@UserCreateActivity, this,
                maxDate = TimeUtil.getCurrentDate()
            )
            setOnClickListener { showCalendar.invoke() }
        }

        (binding.userForm.txtType.editText as AutoCompleteTextView).setAdapter(
            ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("User", "Admin")
            )
        )

        setupImageChooser()
    }

    override fun setTitle() {
        binding.toolBar.lblTitle.text = getString(R.string.title_user_add)
    }

    override fun checkInput(): Boolean {
        val binding = binding.userForm

        val nameStatus = Validator.checkInput(binding.txtName.getString())
        binding.txtName.error = when (nameStatus) {
            InputStatus.EMPTY -> getString(
                R.string.error_require_sth,
                getString(R.string.title_name)
            )
            else -> null
        }

        val emailText = getString(R.string.title_email)
        val emailStatus = Validator.checkEmail(binding.txtEmail.getString())
        binding.txtEmail.error = when (emailStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, emailText)
            InputStatus.INVALID -> getString(R.string.error_invalid_sth, emailText)
            else -> null
        }

        val passwordText = getString(R.string.title_password_confirm)
        val passwordStatus = Validator.checkInput(binding.txtPassword.getString())
        binding.txtPassword.error = when (passwordStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, passwordText)
            InputStatus.INVALID -> getString(
                R.string.error_invalid_sth_with_msg, passwordText,
                getString(R.string.msg_password_invalid)
            )
            else -> null
        }

        val typeStatus = Validator.checkInput(binding.txtType.getString())
        binding.txtType.error = when (typeStatus) {
            InputStatus.EMPTY -> getString(
                R.string.error_require_sth,
                getString(R.string.title_type)
            )
            else -> null
        }

        val phoneStatus = Validator.checkInput(binding.txtPhone.getString())
        binding.txtPhone.error = when (phoneStatus) {
            InputStatus.EMPTY -> getString(
                R.string.error_require_sth,
                getString(R.string.title_phone)
            )
            else -> null
        }

        val dobStatus = Validator.checkInput(binding.txtDob.getString())
        binding.txtDob.error = when (dobStatus) {
            InputStatus.EMPTY -> getString(
                R.string.error_require_sth,
                getString(R.string.title_dob)
            )
            else -> null
        }

        val addressStatus = Validator.checkInput(binding.txtAddress.getString(), true)
        binding.txtAddress.error = when (addressStatus) {
            InputStatus.EMPTY -> getString(
                R.string.error_require_sth,
                getString(R.string.title_address)
            )
            else -> null
        }

        return Validator.isAllValid(
            nameStatus, emailStatus, typeStatus,
            phoneStatus, dobStatus, addressStatus,
        )
    }

    override fun resetInput() {
        binding.userForm.apply {
            ViewUtil.resetViewInput(
                txtName, txtEmail, txtType, txtPhone,
                txtDob, txtAddress, txtPassword, imgProfile
            )
        }
    }

    override fun goToConfirmActivity() {
        binding.userForm.apply{
            launchActivity(UserCreateConfirmActivity::class.java, getInputData())
        }
    }

    internal open fun getInputData(): UserToRequest {
        val binding = binding.userForm
        return UserToRequest(
            name = binding.txtName.getString(),
            email = binding.txtEmail.getString(),
            password = binding.txtPassword.getString(),
            type = binding.txtType.getString(),
            phone = binding.txtPhone.getString(),
            dob = binding.txtDob.getString(),
            address = binding.txtAddress.getString(),
            profile = selectedFile
        )
    }

    @SuppressLint("Recycle")
    private fun setupImageChooser() {

        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (selectedFile != null) selectedFile!!.delete()
                _selectedFile = FileUtil.getFile(applicationContext, it.data!!.data)
                Glide.with(this).load(selectedFile).into(binding.userForm.imgProfile)
            }
        }

        val intent = Intent.createChooser(with(Intent()) {
            type = "image/*"
            setAction(Intent.ACTION_GET_CONTENT)
        }, "Choose Image")

        binding.userForm.btnProfileImg.setOnClickListener { resultLauncher.launch(intent) }
    }
}