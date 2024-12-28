package com.example.bulletinboard.data.dto

data class LoginData(
    val name: String?,
    val type: Int?,
    val token: String?,
    val profile: String?,
    val expired_at: String?,
) {
    override fun toString(): String {
        return "[name:$name, type:$type, token:$token, " +
                "profile:$profile, expired_at: $expired_at]"
    }
}
