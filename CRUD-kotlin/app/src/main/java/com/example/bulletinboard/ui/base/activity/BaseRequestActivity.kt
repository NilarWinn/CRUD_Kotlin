package com.example.bulletinboard.ui.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.bulletinboard.ui.custom.ProgressDialog

abstract class BaseRequestActivity<T : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> T
) : AppCompatActivity() {

    internal lateinit var binding: T
    internal lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater.invoke(layoutInflater)
        progressDialog = ProgressDialog(this)
        setContentView(binding.root)
        doOperation()
    }

    open fun doOperation() {
        setTitle()
    }

    abstract fun setTitle()
    abstract fun checkInput(): Boolean
    abstract fun resetInput()
    abstract fun goToConfirmActivity()
}