package com.zerir.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zerir.notification.ui.theme.NotificationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Button(
                        onClick = {
                            createAndFireNotification()
                        },
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(text = "Notify Me")
                    }
                }
            }
        }

        createChannel()
    }

    private fun createChannel() = NotificationsBuilder.createChannelForAndroidOorHigher(
        this,
        NotificationsConstants.ChannelsId.CHANNEL_ID_APP,
        NotificationsConstants.ChannelsName.CHANNEL_NAME_APP
    )

    private fun createAndFireNotification() {
        val notification = NotificationsBuilder.createNotification(
            this,
            NotificationsConstants.ChannelsId.CHANNEL_ID_APP,
            contentTitle = "Dummy Title",
            contentText = "Dummy notification text",
            destination = MainActivity::class.java,
        )
        NotificationsBuilder.fireNotification(
            this,
            notification,
            NotificationsConstants.NotificationsId.NOTIFICATION_ID_APP
        )
    }
}

private object NotificationsBuilder {

    fun createChannelForAndroidOorHigher(
        context: Context,
        channelId: String,
        channelName: String,
        lightColor: Int = Color.TRANSPARENT,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lightColor = lightColor
            channel.enableLights(lightColor != Color.TRANSPARENT)
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        context: Context,
        channelId: String,
        contentTitle: String,
        contentText: String,
        smallIcon: Int = R.drawable.ic_notification,
        priority: Int = NotificationCompat.PRIORITY_HIGH,
        destination: Class<*>? = null
    ): Notification {
        val pendingIntent: PendingIntent? = destination?.let {
            val intent = Intent(context, destination)
            TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(smallIcon)
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun fireNotification(
        context: Context,
        notification: Notification,
        notificationId: Int,
    ) {
        NotificationManagerCompat.from(context).apply {
            notify(notificationId, notification)
        }
    }
}

object NotificationsConstants {

    object ChannelsId {
        const val CHANNEL_ID_APP = "channel-id-app"
    }

    object ChannelsName {
        const val CHANNEL_NAME_APP = "channel-name-app"
    }

    object NotificationsId {
        const val NOTIFICATION_ID_APP = 0
    }

}