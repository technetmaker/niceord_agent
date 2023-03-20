package com.niceord.agent.firebase


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.niceord.agent.R
import com.niceord.agent.activities.OrderDetailsScreen
import com.niceord.agent.activities.OrdersScreen
import com.niceord.agent.utils.Utils


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        println("onNewToken $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        oreoNotification(remoteMessage)
    }

    private fun oreoNotification(remoteMessage: RemoteMessage){

        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val ii = Intent(applicationContext, OrdersScreen::class.java)
        ii.putExtra(Utils.comingFrom, "notification")
        ii.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
       // val pendingIntent = PendingIntent.getActivity(applicationContext, 0, ii, 0)
        val taskStackBuilder = TaskStackBuilder.create(applicationContext)
        taskStackBuilder.addNextIntentWithParentStack(ii)
        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            taskStackBuilder.getPendingIntent( 0, PendingIntent.FLAG_MUTABLE)
        } else {
            taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }



        val bigText = NotificationCompat.BigTextStyle()
        println("remoteMessage.notification?.body ${remoteMessage.data}")
        var orderId = remoteMessage.data["order_id"]
        println("remoteMessage.notification?.body ${orderId}")
        startActivity(
            Intent(
                this@MyFirebaseMessagingService,
                OrderDetailsScreen::class.java
            ).putExtra(Utils.consumerOrderId,orderId).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        bigText.bigText(remoteMessage.notification?.body)
       // bigText.setBigContentTitle("Today's Bible Verse")
        bigText.setSummaryText("New Order")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(com.niceord.agent.R.mipmap.ic_launcher)
        mBuilder.setSound(Uri.parse("android.resource://"+this.getPackageName()+"/"+ R.raw.sound))
        mBuilder.setContentTitle(remoteMessage.notification?.title)
        mBuilder.setContentText(remoteMessage.notification?.body)
        mBuilder.setStyle(bigText)

        val mNotificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mp.start()
        mNotificationManager.notify(73195, mBuilder.build())
// === Removed some obsoletes

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "NiceOrd"
            val channel = NotificationChannel(
                channelId,
                "NiceOrd App",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager.notify(0, mBuilder.build())
    }
}