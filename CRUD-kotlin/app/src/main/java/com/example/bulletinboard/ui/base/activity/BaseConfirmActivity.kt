package com.example.bulletinboard.ui.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.ui.custom.ProgressDialog

abstract class BaseConfirmActivity<T: ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> T
) : AppCompatActivity() {

    internal lateinit var repository: MainRepository
    internal lateinit var binding: T
    internal lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        repository = (application as MainApplication).mainRepository
        progressDialog = ProgressDialog(this)
        doOperation()
    }

    open fun doOperation() {
        setTitle()
        loadData()
    }

    abstract fun setTitle()
    abstract fun loadData()
    abstract fun bindData()
    abstract fun confirmToRequest()
    abstract suspend fun getRequestResult(): Result<Any>
}