package com.edwardhuerta.githubchallenge.application

import android.app.Application
import timber.log.Timber

// used in the AndroidManifest.xml
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}