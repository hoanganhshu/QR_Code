package com.example.qrscan.adapter

import android.R.attr.onClick
import android.graphics.Color
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemLanguageBinding

class AdapterLanguage(private var selectedLanguage: String? = null,
                       private val onClick: (String) -> Unit) : BaseAdapter<String>() {
    override fun getItemLayout(): Int {
        return R.layout.item_language
    }

    override fun setData(
        binding: ViewDataBinding,
        item: String,
        position: Int
    ) {
        val mBinding = binding as ItemLanguageBinding
        mBinding.tvLanguage.setText(item)
        val isSelected = selectedLanguage != null && item.trim() == selectedLanguage?.trim()
        if (isSelected) {


            mBinding.tvLanguage.setTextColor(Color.parseColor("#3C52F5"))
        } else {

            mBinding.tvLanguage.setTextColor(Color.parseColor("#B9CBE2"))
        }

        mBinding.root.setOnClickListener {
            onClick(item)
        }

    }
    fun setSelectedLanguage(language: String?) {
        selectedLanguage = language
        notifyDataSetChanged()
    }
}