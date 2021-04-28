package com.example.mtaafe.notifications

import android.util.Log
import com.example.mtaafe.data.repositories.AuthRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseSessionManager {

    companion object {
        fun enableFCM() {
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        }

        fun disableFCM() {
            FirebaseMessaging.getInstance().isAutoInitEnabled = false
        }
    }
}