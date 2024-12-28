package com.example.bulletinboard.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.databinding.ActivityLoginBinding
import com.example.bulletinboard.ui.custom.ProgressDialog
import com.example.bulletinboard.ui.util.*
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var repository: MainRepository
    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        repository = (application as MainApplication).mainRepository
        doOperation()
    }

    private fun checkInput(): Boolean {

        val emailTitle = getString(R.string.title_email)
        val emailStatus = Validator.checkInput(binding.txtEmail.getString())
        binding.txtEmail.error = when (emailStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, emailTitle)
            InputStatus.INVALID -> getString(R.string.error_invalid_sth, emailTitle)
            else -> null
        }

        val passwordTitle = getString(R.string.title_password)
        val passwordStatus = Validator.checkInput(binding.txtPassword.getString())
        binding.txtPassword.error = when (passwordStatus) {
            InputStatus.EMPTY -> getString(R.string.error_require_sth, passwordTitle)
            else -> null
        }

        return Validator.isAllValid(emailStatus, passwordStatus)
    }

    private fun doOperation() {

        if (repository.loginData != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener { if (checkInput()) login() }
    }

    private fun login() {

        lifecycleScope.launch(getCoroutineExceptionHandler()) {
            progressDialog.show()

            val jsonObject = JsonObject().apply {
                addProperty("email", binding.txtEmail.getString())
                addProperty("password", binding.txtPassword.getString())
            }

            when (val result = repository.login(jsonObject)) {
                is Result.Success -> {
                    repository.saveLoginData(result.data)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
                else -> showToast(result.toString())
            }

            progressDialog.hide()
        }
    }
}
