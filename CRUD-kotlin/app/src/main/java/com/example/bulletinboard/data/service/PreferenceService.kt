package com.example.bulletinboard.data.service

import android.content.Context
import android.content.SharedPreferences
import com.example.bulletinboard.data.dto.LoginData

class PreferenceService(context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "bulletin"
        private const val USER_NAME = "name"
        private const val USER_TYPE = "type"
        private const val TOKEN = "token"
        private const val IMAGE_PATH = "image"
        private const val EXPIRED_AT = "expired_at"
    }

    private val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private var _loginData: LoginData? = null
    val loginData get() = _loginData

    init {
        fetchLoginData()
    }

    private fun fetchLoginData() {
        val token = preference.getString(TOKEN, null)
        _loginData = if (token != null)
            LoginData(
                preference.getString(USER_NAME, null),
                preference.getInt(USER_TYPE, 0), token,
                preference.getString(IMAGE_PATH, null),
                preference.getString(EXPIRED_AT, null),
            ) else null
    }

    fun setLoginData(data: LoginData) {
        _loginData = data
        preference.edit().apply {
            putString(USER_NAME, data.name)
            putInt(USER_TYPE, data.type!!)
            putString(TOKEN, data.token)
            putString(IMAGE_PATH, data.profile)
            putString(EXPIRED_AT, data.expired_at)
            apply()
        }
    }

    fun clear() {
        _loginData = null
        preference.edit().clear().apply()
    }
}
