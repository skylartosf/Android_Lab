package com.example.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recycler_view.databinding.ItemRvBinding

class Adapter(val dataList: ArrayList<DataModel>):
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    inner class ViewHolder(val itemRvBinding: ItemRvBinding)
        : RecyclerView.ViewHolder(itemRvBinding.root) {
        fun bind(item: DataModel) {
            with (itemRvBinding) {
                tvTitle.text = item.title
                tvTime.text = item.time
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        return ViewHolder(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}