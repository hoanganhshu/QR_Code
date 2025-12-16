package com.example.qrscan.adapter

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
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getItemLayout(),
            parent,
            false
        )

        binding.root.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
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

        holder.bind(list[position])
        itemClickListener?.onItemClick(list[position], position = position)
    }


    override fun getItemCount(): Int {
       if (list.isNotEmpty()){
            return  list.size
        }
        return 0
    }
    fun submitData(newData: List<T>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
    interface OnItemClickListener<T> {
        fun onItemClick(item: T, position: Int)
    }
}

