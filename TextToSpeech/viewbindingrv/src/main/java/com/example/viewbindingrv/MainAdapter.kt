package com.example.viewbindingrv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewbindingrv.databinding.RvItemBinding

class MainAdapter(val taskList: List<Task>): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    inner class MainViewHolder(val itemBinding: RvItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
            fun bindItem(task: Task) {
                with (itemBinding) {
                    tvTitle.text = task.title
                    tvTime.text = task.time
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val task = taskList[position]
        holder.bindItem(task)
    }
}