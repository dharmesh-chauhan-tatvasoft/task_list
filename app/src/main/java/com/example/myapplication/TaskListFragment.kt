package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_task_list, container, false
        )
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        setListView()

        return binding.root
    }

    private fun setListView() {
        adapter = TaskAdapter { task ->
            val params = Bundle()
            params.putLong(Constants.TASK_ID, task.id)
            findNavController().navigate(R.id.addEditTaskFragment, params)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.allTasks.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.addEditTaskFragment)
        }
    }
}