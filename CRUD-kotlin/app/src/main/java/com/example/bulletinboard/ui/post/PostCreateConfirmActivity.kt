package com.example.bulletinboard.ui.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.data.dto.ResponseResult
import com.example.bulletinboard.databinding.ActivityPostCreateConfirmBinding
import com.example.bulletinboard.ui.custom.ProgressDialog
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.goToHome
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

open class PostCreateConfirmActivity : AppCompatActivity() {

    internal lateinit var repository: MainRepository
    internal lateinit var binding: ActivityPostCreateConfirmBinding
    internal lateinit var progressDialog: ProgressDialog

    open var _postToConfirm: PostToRequest? = null
    internal val postToConfirm get() = _postToConfirm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = (application as MainApplication).mainRepository
        progressDialog = ProgressDialog(this)
        binding = ActivityPostCreateConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doOperation()
    }

    open fun doOperation() {
        binding.toolBar.lblTitle.text = getString(R.string.title_post_create_confirm)
        loadData()
        binding.apply {
            btnSubmit.setOnClickListener { confirmToRequest() }
            btnCancel.setOnClickListener { finish() }
            toolBar.btnBack.setOnClickListener { finish() }
        }
    }

    open fun loadData() {
        fetchData()
        bindData(postToConfirm!!)
    }

    open fun fetchData() {
        val bundle: Bundle = intent.extras ?: return
        _postToConfirm = bundle.getSerializable("data") as PostToRequest
    }

    internal open fun bindData(post: PostToRequest) {
        binding.txtTitle.setText(post.title)
        binding.txtDescription.setText(post.description)
    }

    open fun confirmToRequest() {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(getResponseResult()) {
                showToast(R.string.success_save)
                goToHome()
            }
            progressDialog.hide()
        }
    }

    open suspend fun getResponseResult(): Result<ResponseResult> {
        return repository.createPost(postToConfirm!!)
    }
}