package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM ${Constants.TASK_TABLE} ORDER BY id ASC")
    fun getAllTasks(): PagingSource<Int, Task>

    @Query("SELECT * FROM ${Constants.TASK_TABLE} WHERE id = :id LIMIT 1")
    fun getTaskById(id: Long): LiveData<Task>
}