package com.example.qrscan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T> :  RecyclerView.Adapter<BaseAdapter<T>.ViewHolder>(){

    abstract fun getItemLayout(): Int

    val list : MutableList<T> =mutableListOf()

    private var itemClickListener: OnItemClickListener<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<T>.ViewHolder {
        Log.d("BASE_ADAPTER", "onCreateViewHolder called")
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getItemLayout(),
            parent,
            false
        )

        return ViewHolder(binding)
    }
    abstract fun setData(binding : ViewDataBinding,item : T,position : Int)

    inner class ViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : T){
            setData(binding,item,layoutPosition)
        }

    }

    override fun onBindViewHolder(holder: BaseAdapter<T>.ViewHolder, position: Int) {
        Log.d("BASE_ADAPTER", "onBindViewHolder called for position $position, list size = ${list.size}")
        if (position < list.size) {
            holder.bind(list[position])
            itemClickListener?.onItemClick(list[position], position = position)
        }
    }


    override fun getItemCount(): Int {
        val count = if (list.isNotEmpty()) {
            list.size
        } else {
            0
        }
        Log.d("BASE_ADAPTER", "getItemCount() = $count, list.size = ${list.size}")
        return count
    }
    fun submitData(newData: List<T>) {
        list.clear()
        list.addAll(newData)

        notifyDataSetChanged()
        Log.d("BASE_ADAPTER", "notifyDataSetChanged() called")
    }
    interface OnItemClickListener<T> {
        fun onItemClick(item: T, position: Int)
    }
}

