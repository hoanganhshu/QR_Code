package com.example.qrscan.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemCreateBinding

class AdapterCreate : BaseAdapter<Map<String, Any?>>() {

    private val inputMap = mutableMapOf<String, String>()
    private var isPrefilling = false

    override fun getItemLayout(): Int = R.layout.item_create

    override fun setData(binding: ViewDataBinding, item: Map<String, Any?>, position: Int) {
        val mBinding = binding as ItemCreateBinding

        val key = item["key"]?.toString() ?: return
        val title = item["title"]?.toString() ?: ""
        val hint = item["subtitle"]?.toString() ?: ""

        mBinding.title.text = title
        mBinding.subtitle.hint = hint
        val inputType = (item["inputType"] as? Int)
            ?: (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        mBinding.subtitle.inputType = inputType


        mBinding.subtitle.setText(inputMap[key] ?: "")

        mBinding.subtitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(
                    "TEXT_WATCH",
                    "key=$key value='${s?.toString()}' isPrefilling=$isPrefilling"
                )

                if (!isPrefilling) {
                    inputMap[key] = s?.toString() ?: ""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        if (!inputMap.containsKey(key)) {
            inputMap[key] = ""
        }
    }

    fun getUserInput(): Map<String, String> = inputMap.toMap()

    fun prefillInput(values: Map<String, String>) {
        Log.d("PREFILL", "START prefill values=$values")
        if (values.isEmpty()) return

        isPrefilling = true
        inputMap.clear()
        inputMap.putAll(values)
        notifyDataSetChanged()
        isPrefilling = false

        Log.d("PREFILL", "END prefill inputMap=$inputMap")
    }

}


