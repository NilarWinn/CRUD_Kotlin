package com.example.bulletinboard.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import java.io.Serializable

fun TextInputLayout.getString() = this.editText?.text.toString()

fun Context.showToast(message: String, isLengthLong: Boolean = true) {
    Toast.makeText(this, message,
        if (isLengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

fun Context.showToast(stringId: Int, isLengthLong: Boolean = true) {
    Toast.makeText(this, stringId,
        if (isLengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

fun Context.launchActivity(className: Class<*>, bundle: Bundle? = null) {
    val intent = Intent(this, className)
    bundle?.let { intent.putExtras(it) }
    startActivity(intent)
}

fun Context.launchActivity(className: Class<*>, data: Serializable) {
    val intent = Intent(this, className).putExtra("data", data)
    startActivity(intent)
}

fun Activity.hideKeyboard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }
}

fun Fragment.launchActivity(
    className: Class<*>, bundle: Bundle? = null,
) {
    val intent = Intent(requireContext(), className)
    bundle?.let { intent.putExtras(it) }
    startActivity(intent)
}

fun Fragment.launchActivity(className: Class<*>, data: Serializable) {
    val intent = Intent(requireContext(), className).putExtra("data", data)
    startActivity(intent)
}

fun Fragment.showToast(message: String, isLengthLong: Boolean = true) {
    Toast.makeText(context, message,
        if (isLengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.showToast(stringId: Int, isLengthLong: Boolean = true) {
    Toast.makeText(requireContext(), stringId,
        if (isLengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}