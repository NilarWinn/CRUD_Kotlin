package com.example.bulletinboard.ui.user

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.data.service.RetrofitService
import com.example.bulletinboard.ui.util.getString
import com.example.bulletinboard.ui.util.launchActivity

class UserEditActivity : UserCreateActivity() {

    private var _userToRequest: UserToRequest? = null
    private val userToRequest get() = _userToRequest!!

    override fun doOperation() {
        fetchData()
        super.doOperation()
        bindData()
        binding.userForm.txtPassword.isVisible = false
        binding.btnSubmit.text = getString(R.string.action_edit)
        binding.btnClear.text = getString(R.string.action_reset)
        (binding.userForm.txtType.editText as AutoCompleteTextView).setAdapter(
            ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("User", "Admin")
            )
        )
    }

    override fun setTitle() {
        binding.toolBar.lblTitle.text = getString(R.string.title_user_edit)
    }

    override fun resetInput() {
        bindData()
    }

    override fun goToConfirmActivity() {
        binding.userForm.apply {
            launchActivity(UserEditConfirmActivity::class.java, getInputData())
        }
    }

    private fun fetchData() {
        val bundle = intent.extras ?: return
        _userToRequest = UserToRequest(
            user_id = bundle.getString("id"),
            name = bundle.getString("name"),
            email = bundle.getString("email"),
            type = with(bundle.getString("type")) {
              if (this == "0") "Admin" else "User"
            },
            phone = bundle.getString("phone"),
            dob = bundle.getString("dob"),
            address = bundle.getString("address"),
            old_profile = bundle.getString("profile")
        )
    }

    private fun bindData() {

        binding.userForm.apply {

            txtName.editText!!.setText(userToRequest.name)
            txtEmail.editText!!.setText(userToRequest.email)
            txtPassword.editText!!.setText(userToRequest.password)
            txtType.editText!!.setText(userToRequest.type)
            txtPhone.editText!!.setText(userToRequest.phone)
            txtAddress.editText!!.setText(userToRequest.address)

            datePickerDialog.setDate(userToRequest.dob)
            txtDob.editText!!.setText(datePickerDialog.toString())

            // Load Image
            if (_userToRequest!!.old_profile == null) return
            Glide.with(this@UserEditActivity)
                .load(RetrofitService.URL+"/${userToRequest.old_profile}")
                .into(imgProfile)
        }
    }

    override fun getInputData(): UserToRequest {
        val binding = binding.userForm
        return userToRequest.copy(
            name = binding.txtName.getString(),
            email = binding.txtEmail.getString(),
            type = binding.txtType.getString(),
            phone = binding.txtPhone.getString(),
            dob = binding.txtDob.getString(),
            address = binding.txtAddress.getString(),
            profile = selectedFile,
        )
    }
}