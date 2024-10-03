package com.example.little_prince

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.little_prince.ui.theme.Little_princeTheme
import com.example.little_prince.R

class MainActivity : ComponentActivity() {
    private val channelId = "le_petit_prince_notifications"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
            } else {
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        checkAndRequestNotificationPermission()

        setContent {
            Little_princeTheme {
                MainScreen(
                    onLocationChange = { location ->
                        sendNotification(location)
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Le Petit Prince Notifications"
            val descriptionText = "Channel for location notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun sendNotification(location: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val (icon, title, text) = when (location) {
            "morning" -> Triple(R.drawable.morning, "Утро", "Привести в порядок свою планету")
            "day" -> Triple(R.drawable.day, "День", "Полить розу")
            "evening" -> Triple(R.drawable.evening, "Вечер", "Закрыть розу ширмой")
            "night" -> Triple(R.drawable.night, "Ночь", "Полюбоваться закатом")
            else -> Triple(R.drawable.ic_launcher_foreground, "Локация", "Неизвестная локация")
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(location.hashCode(), builder.build())
        }
    }
}

@Composable
fun MainScreen(onLocationChange: (String) -> Unit) {
    var currentLocation by remember { mutableStateOf("morning") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentLocation) {
                "morning" -> Image(painter = painterResource(id = R.drawable.morning), contentDescription = "Morning", modifier = Modifier.size(300.dp))
                "day" -> Image(painter = painterResource(id = R.drawable.day), contentDescription = "Day", modifier = Modifier.size(300.dp))
                "evening" -> Image(painter = painterResource(id = R.drawable.evening), contentDescription = "Evening", modifier = Modifier.size(300.dp))
                "night" -> Image(painter = painterResource(id = R.drawable.night), contentDescription = "Night", modifier = Modifier.size(300.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    currentLocation = "morning"
                    onLocationChange("morning")
                }) {
                    Text("Утро")
                }
                Button(onClick = {
                    currentLocation = "day"
                    onLocationChange("day")
                }) {
                    Text("День")
                }
                Button(onClick = {
                    currentLocation = "evening"
                    onLocationChange("evening")
                }) {
                    Text("Вечер")
                }
                Button(onClick = {
                    currentLocation = "night"
                    onLocationChange("night")
                }) {
                    Text("Ночь")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Little_princeTheme {
        MainScreen(onLocationChange = {})
    }
}
