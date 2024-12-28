package com.example.bulletinboard.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.data.service.RetrofitService
import com.example.bulletinboard.databinding.ActivityProfileEditBinding
import com.example.bulletinboard.ui.base.activity.BaseRequestActivity
import com.example.bulletinboard.ui.custom.DatePickerDialog
import com.example.bulletinboard.ui.util.*
import java.io.File
import java.io.Serializable

class ProfileEditActivity : BaseRequestActivity<ActivityProfileEditBinding>(
    ActivityProfileEditBinding::inflate
) {

    private lateinit var datePickerDialog: DatePickerDialog

    private var _userToRequest: UserToRequest? = null
    private val userToRequest get() = _userToRequest

    private var selectedFile: File? = null

    override fun doOperation() {
        super.doOperation()

        loadData()

        binding.btnSubmit.setText(R.string.action_edit)
        binding.btnSubmit.setOnClickListener {
            if (checkInput()) goToConfirmActivity()
            else showToast(getString(R.string.error_unknown))
        }
        binding.btnClear.setOnClickListener { resetInput() }
        binding.btnBack.setOnClickListener { finish() }

        val showCalendar = {
            hideKeyboard()
            datePickerDialog.show()
        }

        binding.txtDob.editText!!.apply {
            datePickerDialog = DatePickerDialog(this@ProfileEditActivity, this)
            setOnFocusChangeListener { _, b -> if (b) showCalendar.invoke() }
            setOnClickListener { showCalendar.invoke() }
        }
        setupImageChooser()
        bindData()
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_profile_edit)
    }

    override fun checkInput(): Boolean {
        val binding = binding

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

        val addressStatus = Validator.checkInput(binding.txtAddress.getString())
        binding.txtAddress.error = when (addressStatus) {
            InputStatus.EMPTY -> getString(
                R.string.error_require_sth,
                getString(R.string.title_address)
            )
            else -> null
        }

        return Validator.isAllValid(
            nameStatus, emailStatus, phoneStatus, dobStatus, addressStatus,
        )
    }

    override fun resetInput() {
        bindData()
    }

    override fun goToConfirmActivity() {
        launchActivity(
            ProfileConfirmActivity::class.java,
            bundleOf(
                "user" to getInputData() as Serializable,
            )
        )
    }

    private fun loadData() {
        val bundle = intent.extras ?: return
        _userToRequest = bundle.getSerializable("data") as UserToRequest
    }

    @SuppressLint("Recycle")
    private fun setupImageChooser() {

        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (selectedFile != null) selectedFile!!.delete()
                selectedFile = FileUtil.getFile(applicationContext, it.data!!.data)
                Glide.with(this).load(selectedFile).into(binding.imgProfile)
            }
        }

        val intent = Intent.createChooser(with(Intent()) {
            type = "image/*"
            setAction(Intent.ACTION_GET_CONTENT)
        }, "Choose Image")

        binding.btnProfileImg.setOnClickListener { resultLauncher.launch(intent) }
    }

    private fun bindData() {
        binding.apply {
            txtName.editText!!.setText(userToRequest?.name)
            txtEmail.editText!!.setText(userToRequest?.email)
            txtType.editText!!.setText(userToRequest?.type)
            txtPhone.editText!!.setText(userToRequest?.phone)
            txtAddress.editText!!.setText(userToRequest?.address)
            txtDob.editText!!.setText(userToRequest?.dob)
        }

        if (userToRequest?.old_profile != null) {
            Glide.with(this)
                .load(RetrofitService.URL + "${userToRequest!!.old_profile}")
                .into(binding.imgProfile)
        }
    }

    private fun getInputData(): UserToRequest {
        return userToRequest!!.copy(
            name = binding.txtName.getString(),
            email = binding.txtEmail.getString(),
            phone = binding.txtPhone.getString(),
            address = binding.txtAddress.getString(),
            dob = binding.txtDob.getString(),
            profile = selectedFile,
        )
    }
}