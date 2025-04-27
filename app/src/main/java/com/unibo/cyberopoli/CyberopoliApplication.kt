package com.unibo.cyberopoli

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.unibo.cyberopoli.data.repositories.AuthRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CyberopoliApplication : Application(), LifecycleObserver {

    private val authRepo: AuthRepository by inject()

    @OptIn(DelicateCoroutinesApi::class)
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

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val guestId = authRepo.getSavedGuestId()
                        if (!guestId.isNullOrBlank()) {
                            authRepo.deleteAnonymousUserAndSignOut(guestId).firstOrNull()
                        }
                    }
                }
            })
    }
}