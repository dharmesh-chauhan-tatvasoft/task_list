package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ListItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val onClick: (Task) -> Unit) : PagingDataAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, onClick)
    }

    class TaskViewHolder(private val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task?, onClick: (Task) -> Unit) {
            binding.task = task
            if (task != null) {
                val simpleFormatDateTime = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
                val formattedDateTime = simpleFormatDateTime.format(Date(task.dateTime))
                binding.taskDateTime.text = formattedDateTime
            }
            binding.root.setOnClickListener {
                task?.let { onClick(it) }
            }
            binding.executePendingBindings()
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}