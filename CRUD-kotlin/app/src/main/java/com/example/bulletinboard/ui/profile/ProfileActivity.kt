package com.example.bulletinboard.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.databinding.ActivityProfileBinding
import com.example.bulletinboard.ui.custom.ProgressDialog
import com.example.bulletinboard.ui.util.TimeUtil
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.launchActivity
import kotlinx.coroutines.launch

class ProfileActivity: AppCompatActivity() {

    internal lateinit var repository: MainRepository
    private lateinit var binding: ActivityProfileBinding
    internal lateinit var progressDialog: ProgressDialog

    private var _user: UserToRequest? = null
    private val user get() = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = (application as MainApplication).mainRepository
        progressDialog = ProgressDialog(this)
        doOperation()
    }

    private fun doOperation() {

        setTitle()
        fetchData()

        binding.btnBack.setOnClickListener { finish() }
        binding.toolBar.apply {
            inflateMenu(R.menu.menu_edit)
            setOnMenuItemClickListener{
                if (it.itemId == R.id.action_edit) goToEditActivity()
                true
            }
        }

        binding.btnChangePassword.setOnClickListener {
            launchActivity(PasswordChangeActivity::class.java)
        }
    }

    private fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_user_profile)
    }

    private fun fetchData() {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() } ) {
            progressDialog.show()
            handleResult(repository.getProfile()) {
                val data = it.data.data
                _user = UserToRequest(
                    name = data.name,
                    email = data.email,
                    type = if (data.type == "0") "Admin" else "User",
                    phone = data.phone,
                    address = data.address,
                    dob = TimeUtil.changeDateFormat(data.dob),
                    old_profile = data.profile,
                )
                bindData()
            }
            progressDialog.hide()
        }
    }

    private fun bindData() {
        binding.apply {
            txtName.setText(user?.name)
            txtEmail.setText(user?.email)
            txtType.setText(user?.type)
            txtPhone.setText(user?.phone)
            txtDob.setText(user?.dob)
            txtAddress.setText(user?.address)
        }
    }

    private fun goToEditActivity() {
        if (user == null) return
        else launchActivity( ProfileEditActivity::class.java, user!!)
    }
}