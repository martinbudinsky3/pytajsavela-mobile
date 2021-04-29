package com.example.mtaafe.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.R
import com.example.mtaafe.data.models.FcmToken
import com.example.mtaafe.data.models.NewAnswer
import com.example.mtaafe.data.repositories.AuthRepository
import com.example.mtaafe.utils.SessionManager
import com.example.mtaafe.views.activities.QuestionDetailActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private var authRepository: AuthRepository? = null
    private var sessionManager: SessionManager? = null

    init {
        authRepository = AuthRepository()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // if application sends multiple type of messages, some flag data should be sent along message
        val title = remoteMessage.data["title"]!!
        val body = remoteMessage.data["body"]!!
        val questionId = remoteMessage.data["question_id"]!!.toLong()
        val answerId = remoteMessage.data["answer_id"]!!.toLong()

        // if current activity is QuestionDetailActivity update UI
        if(newAnswerId.hasActiveObservers()) {
            newAnswerId.postValue(NewAnswer(answerId, questionId))
        }

        val intent = Intent(this, QuestionDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("question_id", questionId)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        showNotification(title, body, pendingIntent)
    }

    override fun onNewToken(token: String) {
        if(sessionManager == null) {
            sessionManager = SessionManager(this)
        }

        CoroutineScope(Dispatchers.IO).launch {
            authRepository?.postFcmToken(sessionManager?.fetchApiToken()!!, FcmToken(token))
        }
    }

    private fun showNotification(title: String, body: String, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(this, "101")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    companion object {
        val newAnswerId = MutableLiveData<NewAnswer>()
    }
}