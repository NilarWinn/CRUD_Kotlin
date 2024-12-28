package com.example.bulletinboard.data

import com.example.bulletinboard.data.dto.*
import com.example.bulletinboard.data.service.PreferenceService
import com.example.bulletinboard.data.service.RetrofitService
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class MainRepository(
    private val preferenceService: PreferenceService,
    private val retrofitService: RetrofitService,
) {

    val loginData get() = preferenceService.loginData
    private val token get() = with(loginData?.token) {
            if (this != null) "Bearer $this" else null
        }

    /*** Local ***/
    fun saveLoginData(data: LoginData) { preferenceService.setLoginData(data) }
    fun clearLoginData() { preferenceService.clear() }

    /*** Remote API ***/
    suspend fun login(jsonObject: JsonObject) = getResult(retrofitService.login(jsonObject))
    suspend fun logout() = getResult(retrofitService.logout(token))

    /** Post **/
    suspend fun createPost(post: PostToRequest) = getResult(retrofitService.createPost(token, post))
    suspend fun updatePost(post: PostToRequest) = getResult(retrofitService.updatePost(token, post))
    suspend fun deletePost(post: PostToRequest) = getResult(retrofitService.deletePost(token, post))
    suspend fun getPosts(page: Int): Result<ResponseDataList<Post>> = getResult(retrofitService.getPosts(token, page)).withOwnerCheck()
    suspend fun searchPosts(key: String?, page: Int) = getResult(retrofitService.searchPosts(token, key, page)).withOwnerCheck()
    suspend fun getUserPosts(page: Int, key: String? = null) = getResult(retrofitService.getUserPosts(token, page, key)).withOwnerCheck()
    suspend fun uploadCsv(file: File) = getResult(retrofitService.uploadCsv(token, getPart("data_file", file)!!))
    suspend fun downloadCsv() = getResult(retrofitService.downloadCsv(token))

    /** User **/
    suspend fun createUser(user: UserToRequest) = getResult(
        retrofitService.createUser(token,
        getPart("name", user.name)!!,
        getPart("email", user.email)!!,
        getPart("password", user.password)!!,
        getPart("type", user.type)!!,
        getPart("phone", user.phone)!!,
        getPart("dob", user.dob)!!,
        getPart("address", user.address)!!,
        getPart("profile", user.profile),
    ))

    suspend fun updateUser(user: UserToRequest) = getResult(retrofitService.updateUser(token,
        getPart("id", user.user_id)!!,
        getPart("name", user.name)!!,
        getPart("email", user.email)!!,
        getPart("type", user.type)!!,
        getPart("phone", user.phone)!!,
        getPart("dob", user.dob)!!,
        getPart("address", user.address)!!,
        getPart("profile", user.profile),
    ))

    suspend fun deleteUser(user: UserToRequest) = getResult(retrofitService.deleteUser(token, user))
    suspend fun getUsers(page: Int) = getResult(retrofitService.getUsers(token, page))
    suspend fun searchUsers(key: String?, page: Int) = getResult(retrofitService.searchUsers(token, key, page))

    /** Profile **/
    suspend fun getProfile() = getResult(retrofitService.getProfile(token))
    suspend fun updateProfile(user: UserToRequest) = getResult(
        retrofitService.updateProfile(token,
            getPart("name", user.name)!!,
            getPart("email", user.email)!!,
            getPart("phone", user.phone)!!,
            getPart("dob", user.dob)!!,
            getPart("address", user.address)!!,
            getPart("profile", user.profile),
        )
    )

    suspend fun updatePassword(password1: String, password2: String) = getResult(
        retrofitService.updatePassword(token, password1, password2,)
    )

    /**********/

    private suspend fun <T> getResult(response: Response<T>): Result<T> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                when (response.code()) {
                    200 -> Result.Success(response.body()!!)
                    401 -> {
                        clearLoginData()
                        Result.Unauthorized
                    }
                    400, in 402..499 -> {
                        with (Gson().fromJson(response.errorBody()!!.string(), ResponseResult::class.java)) {
                            Result.Error(this.error ?: this.message)
                        }
                    }
                    else -> Result.Error("Error Code ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Fail(e.message.toString())
            }
        }

    private fun getPart(name: String, value: String?): MultipartBody.Part? {
        if (value == null) return null
        return MultipartBody.Part.createFormData(name, value)
    }

    private fun getPart(name: String, file: File?): MultipartBody.Part? {
        if (file == null) return null
        return MultipartBody.Part.createFormData(
            name, file.name, RequestBody.create(MediaType.parse("*/*"), file)
        )
    }

    private fun Result<ResponseDataList<Post>>.withOwnerCheck() =
        when (this) {
            is Result.Success -> {
                this.data.data.forEach {
                    if (it.posted_by.name == loginData!!.name) it.isOwned = true
                }
                this
            }
            else -> this
        }
}
