package com.example.bulletinboard.ui.user

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.User
import com.example.bulletinboard.databinding.ItemUserBinding
import com.example.bulletinboard.ui.base.BaseViewHolder
import com.example.bulletinboard.ui.base.BaseViewHolderListener
import com.example.bulletinboard.ui.base.PagingAdapter
import com.example.bulletinboard.ui.util.TimeUtil

class UserListAdapter : PagingAdapter<User>(R.layout.item_user) {

    var listener: UserViewHolderListener? = null
    var context: Context? = null

    override fun getViewHolder(view: View): BaseViewHolder<User> {
        return UserViewHolder(view, context!!, listener!!)
    }
}

class UserViewHolder(
    itemView: View,
    private val context: Context,
    private val listener: UserViewHolderListener,
) : BaseViewHolder<User>(itemView) {

    private val binding = ItemUserBinding.bind(itemView)

    override fun bind(item: User, position: Int) {
        binding.apply {
            lblUserName.text = item.name
            lblEmail.text = item.email
            lblPhone.text = item.phone
            lblDob.text =
                TimeUtil.changeDateFormat(item.dob, formatToChange = TimeUtil.DATE_FORMAT_MMDDYY)
            lblAddress.text = item.address
            PopupMenu(context, imgMoreOptions, Gravity.END).apply {
                imgMoreOptions.setOnClickListener { show() }
                menuInflater.inflate(R.menu.menu_edit_delete, menu)
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.action_edit -> listener.onEditMenuClick(item, position)
                        R.id.action_delete -> listener.onDeleteMenuClick(item, position)
                    }
                    true
                }
            }
        }
    }
}

interface UserViewHolderListener : BaseViewHolderListener {
    fun onEditMenuClick(user: User, position: Int)
    fun onDeleteMenuClick(user: User, position: Int)
}