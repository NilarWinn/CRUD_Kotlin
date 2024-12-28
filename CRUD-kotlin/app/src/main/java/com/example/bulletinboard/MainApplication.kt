package com.example.bulletinboard

import android.app.Application
import com.example.bulletinboard.data.MainRepository
import com.example.bulletinboard.data.service.PreferenceService
import com.example.bulletinboard.data.service.RetrofitService

class MainApplication : Application() {
    private val preferenceService by lazy { PreferenceService(super.getApplicationContext()) }
    private val retrofitService by lazy { RetrofitService.getInstance(super.getApplicationContext()) }
    val mainRepository by lazy { MainRepository(preferenceService, retrofitService) }
}
