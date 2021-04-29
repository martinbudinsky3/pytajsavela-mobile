package com.example.mtaafe.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mtaafe.R
import com.example.mtaafe.views.activities.QuestionDetailActivity
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // if application sends multiple type of messages, some flag data should be sent along message
        val title = remoteMessage.data["title"]!!
        val body = remoteMessage.data["body"]!!
        val questionId = remoteMessage.data["question_id"]!!.toLong()
        val answerId = remoteMessage.data["answer_id"]!!.toLong()

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, QuestionDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        intent.putExtra("question_id", questionId)
        intent.putExtra("answer_id", answerId)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, "101")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    override fun onNewToken(token: String) {
        Log.d("Token", "Refreshed token: $token")
        // TODO post token on server
    }
}