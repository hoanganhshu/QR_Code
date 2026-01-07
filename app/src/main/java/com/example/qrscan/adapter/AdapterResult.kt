package com.example.qrscan.adapter

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemResultBinding

class AdapterResult(private val textColor : Int = Color.WHITE) : BaseAdapter<Map<String, String>>() {

    override fun getItemLayout(): Int = R.layout.item_result

    override fun setData(binding: ViewDataBinding, item: Map<String, String>, position: Int) {
        val mBinding = binding as ItemResultBinding
        val title = item["title"]
            ?.toString()
            ?.trim()
            ?.lowercase()
            ?.replaceFirstChar { it.uppercase() }
            ?: ""


        mBinding.tvTitle.text = title
        mBinding.tvSubtitle.text = item["subtitle"]
        mBinding.tvTitle.setTextColor(textColor)
        mBinding.tvSubtitle.setTextColor(textColor)
    }
}
