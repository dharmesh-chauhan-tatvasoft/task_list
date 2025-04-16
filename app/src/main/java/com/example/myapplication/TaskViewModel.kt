package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    val allTasks: LiveData<PagingData<Task>> = Pager(
        PagingConfig(pageSize = 20)
    ) {
        taskDao.getAllTasks()
    }.flow
        .cachedIn(viewModelScope)
        .asLiveData()

    fun insert(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
            scheduleReminder(task)
        }
    }

    fun update(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
            scheduleReminder(task)
        }
    }

    fun getTaskById(taskId: Long): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }

    private fun scheduleReminder(task: Task) {
        val delay = task.dateTime - System.currentTimeMillis()
        if (delay < 0) return

        val repeatMillis = when (task.repeatInterval) {
            Constants.ONE_HOUR -> TimeUnit.HOURS.toMillis(1)
            Constants.TWO_HOUR -> TimeUnit.HOURS.toMillis(2)
            Constants.THREE_HOUR -> TimeUnit.HOURS.toMillis(3)
            Constants.TWENTY_FOUR_HOUR -> TimeUnit.HOURS.toMillis(24)
            else -> 0L
        }

        val data = Data.Builder()
            .putString(Constants.TITLE, task.title)
            .putString(Constants.DESCRIPTION, task.description)
            .build()

        val requestBuilder = if (repeatMillis > 0) {
            PeriodicWorkRequestBuilder<TaskReminder>(
                repeatMillis, TimeUnit.MILLISECONDS
            ).setInitialDelay(delay, TimeUnit.MILLISECONDS)
        } else { OneTimeWorkRequestBuilder<TaskReminder>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        }

        val workRequest = requestBuilder.setInputData(data).build()
        WorkManager.getInstance(getApplication())
            .enqueue(workRequest)
    }

}