package com.niceord.agent.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class MainApplication : Application() {

    companion object {
        var mInstance: MainApplication? = null
        fun getInstance(): MainApplication? {
            checkNotNull(mInstance)
            return mInstance
        }

        fun getApplicationInstance(): Context? {
            return mInstance
        }

    }
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}