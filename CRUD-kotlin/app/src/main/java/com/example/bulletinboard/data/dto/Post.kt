package com.example.bulletinboard.data.dto

import java.io.Serializable

data class Post(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val posted_by: PostedBy,
    val posted_date: String,
    val can_delete: Boolean,
    var isOwned: Boolean = false,
)

data class PostedBy(
    val name: String,
    val type: Int,
)

data class PostToRequest(
    val title: String?,
    val description: String?,
    var post_id: String? = null, // for Update & Delete
    var status: String? = null,
): Serializable {
    override fun toString(): String {
        return "[title: $title, description: $description," +
                "post_id: $post_id, status: $status]"
    }
}