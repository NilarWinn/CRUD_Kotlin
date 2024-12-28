package com.example.bulletinboard.data.service

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.bulletinboard.data.dto.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface RetrofitService {

    companion object {

//        const val URL = "https://bulletinboard-nextjs.vercel.app"
//        const val URL = "http://192.168.181.43:3000"
        const val URL = "http://172.20.10.67:3000"

        private const val IS_DEBUG_MODE = true
        private const val API = "$URL/api/"
        private const val AUTH = "Authorization"
        private const val PAGE = "page"

        private var instance: RetrofitService? = null

        fun getInstance(context: Context): RetrofitService {
            if (instance == null) {
                instance = with(Retrofit.Builder()) {
                    baseUrl(API)
                    addConverterFactory(ScalarsConverterFactory.create())
                    addConverterFactory(GsonConverterFactory.create())
                    if (IS_DEBUG_MODE) client(getClient(context))
                    build().create(RetrofitService::class.java)
                }
            }
            return instance!!
        }

        private fun getClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor(
                with(ChuckerInterceptor.Builder(context)) {
                    collector(ChuckerCollector(context))
                    maxContentLength(250000L)
                    redactHeaders(emptySet())
                    alwaysReadResponseBody(false)
                    build()
                }
            ).build()
        }
    }

    @POST("login") // Login
    @Headers("Content-Type: application/text")
    suspend fun login(@Body jsonObject: JsonObject): Response<LoginData>

    @POST("logout") // Logout
    suspend fun logout(@Header(AUTH) token: String?,): Response<ResponseResult>

      /***************************/
     /********** User ***********/
    /***************************/

    @GET("user") // List
    suspend fun getUsers(
        @Header(AUTH) token: String?,
        @Query(PAGE) page: Int,
    ): Response<ResponseDataList<User>>

    @POST("user/create") // Create
    @Multipart
    suspend fun createUser(
        @Header(AUTH) token: String?,
        @Part name: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part phone: MultipartBody.Part,
        @Part dob: MultipartBody.Part,
        @Part address: MultipartBody.Part,
        @Part profile: MultipartBody.Part?,
    ): Response<ResponseResult>

    @POST("user/update") // Update
    @Multipart
    suspend fun updateUser(
        @Header(AUTH) token: String?,
        @Part id: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part phone: MultipartBody.Part,
        @Part dob: MultipartBody.Part,
        @Part address: MultipartBody.Part,
        @Part profile: MultipartBody.Part? = null,
    ): Response<ResponseResult>

    @GET("user/profile") // Profile
    suspend fun getProfile(@Header(AUTH) token: String?): Response<ResponseData<User>>

    @POST("user/profile") // Profile Update
    @Multipart
    suspend fun updateProfile(
        @Header(AUTH) token: String?,
        @Part name: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part phone: MultipartBody.Part,
        @Part dob: MultipartBody.Part,
        @Part address: MultipartBody.Part,
        @Part profile: MultipartBody.Part?,
    ): Response<ResponseResult>

    @POST("user/delete") // User Delete
    suspend fun deleteUser(
        @Header(AUTH) token: String?,
        @Body body: UserToRequest,
    ): Response<ResponseResult>

    @POST("user/update/password") // Password Change
    @FormUrlEncoded
    suspend fun updatePassword(
        @Header(AUTH) token: String?,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
    ): Response<ResponseResult>

    @GET("user/search") // Search
    suspend fun searchUsers(
        @Header(AUTH) token: String?,
        @Query("key") key: String?,
        @Query("page") page: Int,
    ): Response<ResponseDataList<User>>

      /***************************/
     /********** Post ***********/
    /***************************/

    @GET("user/profile/post") // My Post
    suspend fun getUserPosts(
        @Header(AUTH) token: String?,
        @Query("page") page: Int,
        @Query("key") key: String?,
    ): Response<ResponseDataList<Post>>

    @GET("post") // List
    suspend fun getPosts(
        @Header(AUTH) token: String?,
        @Query(PAGE) page: Int,
    ): Response<ResponseDataList<Post>>

    @GET("post/search") // Search
    suspend fun searchPosts(
        @Header(AUTH) token: String?,
        @Query("key") key: String?,
        @Query("page") page: Int,
    ): Response<ResponseDataList<Post>>

    @POST("post/create") // Create
    suspend fun createPost(
        @Header(AUTH) token: String?,
        @Body body: PostToRequest,
    ): Response<ResponseResult>

    @PUT("post/update") // Update
    suspend fun updatePost(
        @Header(AUTH) token: String?,
        @Body body: PostToRequest,
    ): Response<ResponseResult>

    @POST("post/delete") // Delete
    suspend fun deletePost(
        @Header(AUTH) token: String?,
        @Body body: PostToRequest,
    ): Response<ResponseResult>

    @POST("post/upload") // CSV upload
    @Multipart
    suspend fun uploadCsv(
        @Header(AUTH) token: String?,
        @Part file: MultipartBody.Part,
    ): Response<ResponseResult>

    @GET("post/download") // Download
    suspend fun downloadCsv(
        @Header(AUTH) token: String?,
    ): Response<String>
}
