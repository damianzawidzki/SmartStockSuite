package com.example.smartstocksuitemobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstocksuitemobile.databinding.ItemPickingTaskBinding
import com.example.smartstocksuitemobile.models.PickingTaskDemo

class PickingTaskAdapter(
    private val tasks: List<PickingTaskDemo>,
    private val onTaskClick: (PickingTaskDemo) -> Unit
) : RecyclerView.Adapter<PickingTaskAdapter.PickingTaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingTaskViewHolder {
        val binding = ItemPickingTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PickingTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PickingTaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class PickingTaskViewHolder(
        private val binding: ItemPickingTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: PickingTaskDemo) {
            binding.tvItemTaskId.text = task.taskId
            binding.tvItemProduct.text = "Product: ${task.productName}"
            binding.tvItemLocation.text = "Location: ${task.location}"
            binding.tvItemQuantity.text = "Quantity to pick: ${task.quantityToPick}"
            binding.tvItemStatus.text = "Status: ${task.status}"

            binding.root.setOnClickListener {
                onTaskClick(task)
            }
        }
    }
}