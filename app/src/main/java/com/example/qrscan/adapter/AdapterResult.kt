package com.example.qrscan.adapter

import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemResultBinding

class AdapterResult : BaseAdapter<Map<String, String>>() {

    override fun getItemLayout(): Int = R.layout.item_result

    override fun setData(binding: ViewDataBinding, item: Map<String, String>, position: Int) {
        val mBinding = binding as ItemResultBinding

        mBinding.tvTitle.text = item["title"]
        mBinding.tvSubtitle.text = item["subtitle"]
    }
}
