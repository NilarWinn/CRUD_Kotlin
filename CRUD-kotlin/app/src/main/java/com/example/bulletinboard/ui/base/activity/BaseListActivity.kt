package com.example.bulletinboard.ui.base.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.ResponseDataList
import com.example.bulletinboard.databinding.ActivityListBinding
import com.example.bulletinboard.ui.base.PagingAdapter
import com.example.bulletinboard.ui.custom.ProgressDialog
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import kotlinx.coroutines.launch

abstract class BaseListActivity<T>(
    internal val adapter: PagingAdapter<T>
) : AppCompatActivity() {

    internal lateinit var repository: MainRepository
    internal lateinit var binding: ActivityListBinding
    internal lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = (application as MainApplication).mainRepository
        binding = ActivityListBinding.inflate(layoutInflater)
        progressDialog = ProgressDialog(this)
        setContentView(binding.root)
        doOperations()
    }

    open fun doOperations() {
        setTitle()
        loadData()
        binding.layoutListContent.apply {
            recyclerView.adapter = adapter
            swipeRefresh.setOnRefreshListener { loadData() }
            nestedScroll.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                    if  ((scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight))
                        if (!adapter.isCompletedList && !adapter.isLocked) doPaging()
                }
            )
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    abstract fun setTitle()

    internal fun loadData() {
        val binding = binding.layoutListContent
        lifecycleScope.launch(getCoroutineExceptionHandler {
            binding.swipeRefresh.isRefreshing = false
            checkData()
        }) {
            binding.swipeRefresh.isRefreshing = true
            adapter.reset()
            handleResult(getDataResult(0)) {
                adapter.updateDataList(it.data.data)
            }
            checkData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun doPaging() {
        val binding = binding.layoutListContent
        lifecycleScope.launch(getCoroutineExceptionHandler {
            binding.progressCircular.isVisible = false
            adapter.isLocked = false
        }) {
            binding.progressCircular.visibility = View.VISIBLE
            adapter.isLocked = true
            handleResult(getDataResult(adapter.offset)) {
                adapter.insertDataList(it.data.data)
            }
            adapter.isLocked = false
            binding.progressCircular.visibility = View.GONE
        }
    }

    private fun checkData() {
        val binding = binding.layoutListContent
        (adapter.itemCount == 0).let {
            binding.groupMsg.isVisible = it
            binding.recyclerView.isVisible = !it
            binding.txtMsgTitle.text = if (it) getString(R.string.error_result_empty) else null
            binding.txtMsg.text = if (it) getString(R.string.msg_try_again) else null
        }
    }

    abstract suspend fun getDataResult(page: Int): Result<ResponseDataList<T>>
}