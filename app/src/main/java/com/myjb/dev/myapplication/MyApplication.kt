package com.myjb.dev.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.palaima.debugdrawer.timber.data.LumberYard
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            enableDebugDrawerTimber()
        }
    }

    private fun enableDebugDrawerTimber() {
        val lumberYard = LumberYard.getInstance(this)
        lumberYard.cleanUp()

        Timber.plant(lumberYard.tree())
        Timber.plant(Timber.DebugTree())
    }
}