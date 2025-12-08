package com.example.taskmaster.worker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.taskmaster.data.AppDatabase
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class TaskReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val today = LocalDate.now().toString()
        val tasksDueToday = database.taskDao().getTasksDueOn(today)
        if (tasksDueToday.isNotEmpty()) showNotification(tasksDueToday.size)
        return Result.success()
    }
    private fun showNotification(count: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("TaskMaster Reminder")
            .setContentText("You have $count tasks due today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(1, notification)
    }
}
object NotificationScheduler {
    fun scheduleDailyReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<TaskReminderWorker>(24, TimeUnit.HOURS).setInitialDelay(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("daily_reminder", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
