package com.unibo.cyberopoli.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.unibo.cyberopoli.data.repositories.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import org.koin.android.ext.android.inject

class GuestCleanUpService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    private val authRepo by inject<AuthRepository>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            val guestId = authRepo.getSavedGuestId()
            if (guestId != null) {
                authRepo.deleteAnonymousUserAndSignOut(guestId)
                    .firstOrNull()

                Log.d("GuestCleanUpService", "Guest user ENTER $guestId deleted")
            }
            stopSelf()
        }
        return START_NOT_STICKY
    }
}
