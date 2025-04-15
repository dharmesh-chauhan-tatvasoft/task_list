package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentAddEditTaskBinding
import java.util.*

class AddEditTaskFragment : Fragment() {
    private lateinit var binding: FragmentAddEditTaskBinding
    private lateinit var viewModel: TaskViewModel
    private var taskId: Long? = null
    private var repeatIntervalList = listOf<String>()
    private var selectedCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_edit_task, container, false
        )
        viewModel = ViewModelProvider(this)[TaskViewModel:: class.java]

        setTaskData()

        return binding.root
    }

    private fun setTaskData() {
        repeatIntervalList = listOf(Constants.NO_REPEAT, Constants.ONE_HOUR, Constants.TWO_HOUR, Constants.THREE_HOUR, Constants.TWENTY_FOUR_HOUR)
        val repeatIntervalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeatIntervalList)
        repeatIntervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.repeatIntervalSpinner.adapter = repeatIntervalAdapter

        binding.dateSelectButton.setOnClickListener { showDatePicker() }
        binding.timeSelectButton.setOnClickListener { showTimePicker() }

        setEditTaskData()

        binding.saveButton.setOnClickListener {
            if(validateTaskData()) {
                handleAddTask()
            }
        }
    }

    private fun setEditTaskData() {
        taskId = arguments?.getLong(Constants.TASK_ID)
        if (taskId != null) {
            viewModel.getTaskById(taskId!!).observe(viewLifecycleOwner) { task ->
                binding.task = task

                selectedCalendar.timeInMillis = task.dateTime

                val intervalIndex = repeatIntervalList.indexOfFirst { it == task.repeatInterval }
                if (intervalIndex != -1) {
                    binding.repeatIntervalSpinner.setSelection(intervalIndex)
                }
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, day)
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hour)
                selectedCalendar.set(Calendar.MINUTE, minute)
                selectedCalendar.set(Calendar.SECOND, 0)
            },
            selectedCalendar.get(Calendar.HOUR_OF_DAY),
            selectedCalendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun handleAddTask() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val repeatInterval = binding.repeatIntervalSpinner.selectedItem.toString()
        val dateTimeInMillis = selectedCalendar.timeInMillis
        if (taskId == null) {
            val newTask = Task(title = title, description = description, dateTime = dateTimeInMillis, repeatInterval = repeatInterval)
            viewModel.insert(newTask)
        } else {
            val newTask = Task(id = taskId!!, title = title, description = description, dateTime = dateTimeInMillis, repeatInterval = repeatInterval)
            viewModel.update(newTask)
        }
        findNavController().navigate(R.id.taskListFragment)
    }

    private fun validateTaskData(): Boolean {
        if (binding.titleEditText.text.toString().isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.task_title_error), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.descriptionEditText.text.toString().isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.task_description_error), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.repeatIntervalSpinner.selectedItem == null) {
            Toast.makeText(requireContext(), getString(R.string.task_repeat_interval_error), Toast.LENGTH_SHORT).show()
            return false
        }
        return  true
    }
}