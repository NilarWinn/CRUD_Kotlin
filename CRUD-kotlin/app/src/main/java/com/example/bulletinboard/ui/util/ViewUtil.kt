package com.example.bulletinboard.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Checkable
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.ui.HomeActivity
import com.example.bulletinboard.ui.LoginActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineExceptionHandler
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext

class ViewUtil {

    companion object {

        fun resetViewInput(vararg view: View) {
            view.forEach {
                when (it) {
                    is TextInputLayout -> it.editText!!.text = null
                    is TextView -> it.text = null
                    is Checkable -> it.isChecked = false
                    is ImageView -> it.setImageResource(0)
                    else -> throw RuntimeException("Unsupported View")
                }
            }
        }

        fun <T> Context.handleResult(result: Result<T>, block: (Result.Success<T>) -> Unit) {
            when (result) {
                is Result.Success -> block.invoke(result)
                is Result.Fail,
                is Result.Error -> showToast(result.toString())
                is Result.Unauthorized -> {
                    goToLogin()
                    showToast(R.string.error_authorize)
                }
                else -> showToast(R.string.error_unknown)
            }
        }

        fun Context.goToHome() {
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
        }

        private fun Context.goToLogin() {
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
        }

        fun Fragment.getCoroutineExceptionHandler(block: () -> Unit = {}): CoroutineExceptionHandler {
            return getExceptionHandler { _, throwable ->
                when (throwable) {
                    is ConnectException -> showToast(R.string.error_connection)
                    is SocketTimeoutException -> showToast(getString(R.string.error_connection_timeout))
                    else -> showToast(R.string.error_unknown)
                }
                block.invoke()
            }
        }

        fun Activity.getCoroutineExceptionHandler(block: () -> Unit = {}): CoroutineExceptionHandler {
            return getExceptionHandler { _, throwable ->
                when (throwable) {
                    is ConnectException -> showToast(R.string.error_connection)
                    is SocketTimeoutException -> showToast(getString(R.string.error_connection_timeout))
                    else -> showToast(R.string.error_unknown)
                }
                block.invoke()
            }
        }

        private fun getExceptionHandler(block: (CoroutineContext, Throwable) -> Unit): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { coroutineContext, throwable ->
                block.invoke(coroutineContext, throwable)
            }
        }
    }
}