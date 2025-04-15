package com.example.myapplication

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TaskReminder(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(Constants.TITLE) ?: return Result.failure()
        val description = inputData.getString(Constants.DESCRIPTION) ?: ""
        NotificationUtility.showNotification(applicationContext, title, description)
        return Result.success()
    }
}
