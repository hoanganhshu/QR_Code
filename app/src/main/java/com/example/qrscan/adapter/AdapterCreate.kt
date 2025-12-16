package com.example.qrscan.adapter

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemCreateBinding

class AdapterCreate : BaseAdapter<Map<String, Any?>>() {

    private val inputMap = mutableMapOf<String, String>()

    override fun getItemLayout(): Int = R.layout.item_create

    override fun setData(binding: ViewDataBinding, item: Map<String, Any?>, position: Int) {
        val mBinding = binding as ItemCreateBinding


        val key = item["key"]?.toString() ?: ""
        val title = item["title"]?.toString() ?: ""
        val hint = item["subtitle"]?.toString() ?: ""


        mBinding.title.text = title
        mBinding.subtitle.hint = hint


        if (inputMap.containsKey(key)) {
            mBinding.subtitle.setText(inputMap[key])
        }

        mBinding.subtitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                inputMap[key] = s?.toString() ?: ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        if (!inputMap.containsKey(key)) {
            inputMap[key] = ""
        }
    }

    fun getUserInput(): Map<String, String> = inputMap.toMap()
}
