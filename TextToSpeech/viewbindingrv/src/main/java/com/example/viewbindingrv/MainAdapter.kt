package com.example.viewbindingrv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewbindingrv.databinding.RvItemBinding

class MainAdapter(private val taskList: List<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /*
    inner class MainViewHolder(val itemBinding: RvItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
            fun bindItem(task: Task) {
                with (itemBinding) {
                    tvTitle.text = task.title
                    tvTime.text = task.time
                }
            }
    }
    */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MainViewHolder(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MainViewHolder).bind(taskList[position])
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}