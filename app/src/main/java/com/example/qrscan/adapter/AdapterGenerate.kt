package com.example.qrscan.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrscan.R
import com.example.qrscan.database.data.DataGenerateInMonth
import com.example.qrscan.databinding.ItemDayGenerateBinding


data class generateMonth(

    val createAt : String,
    val items: List<DataGenerateInMonth>
)

class AdapterGenerate(private val callback: Callback) : BaseAdapter<generateMonth>(){
    override fun getItemLayout(): Int {
        return R.layout.item_day_generate
    }

    override fun setData(
        binding: ViewDataBinding,
        item: generateMonth,
        position: Int
    ) {
       val mBinding = binding as ItemDayGenerateBinding
        mBinding.view1.setText(item.createAt)
        val childAdapter = AdapterGenerateInMonth(callback)
        val context = mBinding.recyclerview.context


        mBinding.recyclerview.adapter = childAdapter
        mBinding.recyclerview.layoutManager= LinearLayoutManager(context)


        childAdapter.submitData(item.items)

    }
}