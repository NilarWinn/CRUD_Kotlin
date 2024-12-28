package com.example.bulletinboard.ui.post

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.example.bulletinboard.R
import com.example.bulletinboard.data.dto.Post
import com.example.bulletinboard.databinding.ItemPostBinding
import com.example.bulletinboard.ui.base.BaseViewHolder
import com.example.bulletinboard.ui.base.BaseViewHolderListener
import com.example.bulletinboard.ui.base.PagingAdapter

class PostListAdapter: PagingAdapter<Post>(R.layout.item_post) {

    var context: Context? = null
    var menuListener: PostViewHolderMenuListener? = null
    var touchListener: PostViewHolderTouchListener? = null

    override fun getViewHolder(view: View): BaseViewHolder<Post> =
        PostViewHolder(view, context!!, menuListener, touchListener)
}

class PostViewHolder(
    itemView: View,
    private val context: Context,
    private val menuListener: PostViewHolderMenuListener?,
    private val touchListener: PostViewHolderTouchListener?,
) : BaseViewHolder<Post>(itemView) {

    private val binding = ItemPostBinding.bind(itemView)

    override fun bind(item: Post, position: Int) {
        binding.apply {
            lblTitle.text = item.title
            lblDescription.text = item.description
            lblUserName.text = item.posted_by.name
            lblDate.text = item.posted_date
            if (item.isOwned) {
                setUpPopUpMenu(item)
                touchListener?.let {
                    binding.root.setOnClickListener { touchListener.onTouch(item, position) }
                }
            }
        }
    }

    private fun setUpPopUpMenu(item: Post) {

        if (menuListener == null) return

        binding.imgMoreOptions.isVisible = true
        PopupMenu(context, binding.imgMoreOptions, Gravity.END).apply {
            binding.imgMoreOptions.setOnClickListener { show() }
            menuInflater.inflate(R.menu.menu_edit_delete, menu)
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_edit -> menuListener.onEditMenuClick(item, position)
                    R.id.action_delete -> menuListener.onDeleteMenuClick(item, position)
                }
                true
            }
        }
    }
}

interface PostViewHolderMenuListener: BaseViewHolderListener {
    fun onEditMenuClick(post: Post, position: Int)
    fun onDeleteMenuClick(post: Post, position: Int)
}

interface PostViewHolderTouchListener: BaseViewHolderListener {
    fun onTouch(post: Post, position: Int)
}