package com.example.bulletinboard.ui.user

import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.example.bulletinboard.R
import com.example.bulletinboard.data.Result
import com.example.bulletinboard.data.dto.ResponseDataList
import com.example.bulletinboard.data.dto.User
import com.example.bulletinboard.data.dto.UserToRequest
import com.example.bulletinboard.ui.base.activity.BaseListAndSearchActivity
import com.example.bulletinboard.ui.util.ViewUtil.Companion.getCoroutineExceptionHandler
import com.example.bulletinboard.ui.util.ViewUtil.Companion.handleResult
import com.example.bulletinboard.ui.util.launchActivity
import com.example.bulletinboard.ui.util.showToast
import kotlinx.coroutines.launch

open class UserListActivity : BaseListAndSearchActivity<User>(UserListAdapter()), UserViewHolderListener {

    init {
        (adapter as UserListAdapter).context = this
        adapter.listener = this
    }

    override fun setTitle() {
        binding.lblTitle.text = getString(R.string.title_user_list)
    }

    override suspend fun getDataResult(page: Int): Result<ResponseDataList<User>> {
        return repository.getUsers(page)
    }

    override fun onEditMenuClick(user: User, position: Int) {
        launchActivity(
            UserEditActivity::class.java, bundleOf(
                "id" to user._id,
                "name" to user.name,
                "email" to user.email,
                "profile" to user.profile,
                "type" to user.type,
                "phone" to user.phone,
                "address" to user.address,
                "dob" to user.dob,
            )
        )
    }

    override fun onDeleteMenuClick(user: User, position: Int) {
        lifecycleScope.launch(getCoroutineExceptionHandler { progressDialog.hide() }) {
            progressDialog.show()
            handleResult(repository.deleteUser(UserToRequest(user_id = user._id))) {
                adapter.deleteItem(position)
                showToast(R.string.success_delete)
            }
            progressDialog.hide()
        }
    }

    override fun doSearchOperation(query: String) {
        launchActivity(
            UserSearchActivity::class.java, bundleOf("searchKey" to query)
        )
    }
}