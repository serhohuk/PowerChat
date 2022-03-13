package com.serhohuk.powerchat.application

import android.app.Application
import com.serhohuk.powerchat.di.appModule
import com.serhohuk.powerchat.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ChatApplication : Application() {

    companion object {
        lateinit var instance: ChatApplication
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@ChatApplication)
            modules(listOf(appModule, dataModule))
        }
        instance = this
    }

}