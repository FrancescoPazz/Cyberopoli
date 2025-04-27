package com.unibo.cyberopoli

import android.app.Application
import android.content.Intent
import com.unibo.cyberopoli.service.GuestCleanUpService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CyberopoliApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CyberopoliApplication)
            modules(
                supabaseModule,
                appModule
            )
        }

        startService(Intent(this, GuestCleanUpService::class.java))
    }
}