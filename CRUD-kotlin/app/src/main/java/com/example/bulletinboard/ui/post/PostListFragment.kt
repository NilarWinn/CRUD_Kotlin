package com.example.bulletinboard.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.MainApplication
import com.example.bulletinboard.R
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.dto.Post
import com.example.bulletinboard.data.dto.PostToRequest
import com.example.bulletinboard.databinding.ContentListBinding
import com.example.bulletinboard.ui.custom.ProgressDialog
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.launchActivity
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

class PostListFragment : Fragment(), PostViewHolderMenuListener {

    private var _binding: ContentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MainRepository
    private lateinit var progressDialog: ProgressDialog
    private var adapter = PostListAdapter()

    init {
        adapter.menuListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ContentListBinding.inflate(inflater, container, false)
        repository = (requireActivity().application as MainApplication).mainRepository
        progressDialog = ProgressDialog(requireContext())
        adapter.context = requireContext()
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOperations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEditMenuClick(post: Post, position: Int) {
        launchActivity(
            PostEditActivity::class.java,
            PostToRequest(post.title, post.description, post.id, post.status)
        )
    }

    override fun onDeleteMenuClick(post: Post, position: Int) {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            requireContext().handleResult(
                repository.deletePost(PostToRequest(null, null, post.id))
            ) {
                adapter.deleteItem(position)
                showToast(R.string.success_delete)
            }
            progressDialog.hide()
        }
    }

    private fun doOperations() {
        loadData()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
        binding.nestedScroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if ((scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight))
                    if (!adapter.isCompletedList && !adapter.isLocked) doPaging()
            }
        )
    }

    private fun loadData() {
        lifecycleScope.launch(getCoroutineExceptionHandler {
            binding.swipeRefresh.isRefreshing = false
            checkData()
        }) {
            binding.swipeRefresh.isRefreshing = true
            adapter.reset()
            requireContext().handleResult(repository.getPosts(adapter.offset)) {
                adapter.updateDataList(it.data.data)
            }
            checkData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun doPaging() {
        lifecycleScope.launch(getCoroutineExceptionHandler {
            binding.progressCircular.isVisible = false
            adapter.isLocked = false
        }) {
            binding.progressCircular.isVisible = true
            adapter.isLocked = true
            requireContext().handleResult(repository.getPosts(adapter.offset)) {
                adapter.insertDataList(it.data.data)
            }
            adapter.isLocked = false
            binding.progressCircular.isVisible = false
        }
    }

    private fun checkData() {
        (adapter.itemCount == 0).let {
            binding.groupMsg.isVisible = it
            binding.recyclerView.isVisible = !it
            binding.txtMsgTitle.text = if (it) getString(R.string.error_result_empty) else null
            binding.txtMsg.text = if (it) getString(R.string.msg_try_again) else null
        }
    }
}