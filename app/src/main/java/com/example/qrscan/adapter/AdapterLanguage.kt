package com.example.qrscan.adapter

import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.databinding.ItemLanguageBinding
import java.util.Locale

class AdapterLanguage(
    private var selectedLanguage: String? = null
) : BaseAdapter<String>() {
    var middleVisiblePosition: Int = Int.MAX_VALUE / 2
        set(value) {
            val oldValue = field
            field = value
            if (oldValue != value && list.isNotEmpty()) {
                notifyItemChanged(oldValue)
                notifyItemChanged(value)
            }
        }

    var hasScrolled: Boolean = false
        set(value) {
            field = value
            if (list.isNotEmpty()) {
                notifyDataSetChanged()
            }
        }


    override fun getItemLayout(): Int {
        return R.layout.item_language
    }
    override fun getItemCount(): Int {
        return if (list.isEmpty()) 0 else Int.MAX_VALUE
    }

    override fun setData(
        binding: ViewDataBinding,
        item: String,
        position: Int
    ) {
        val mBinding = binding as ItemLanguageBinding


        mBinding.tvLanguage.text = item
        val isHighlighted = if (!hasScrolled) {
            // Highlight currentLanguage khi mới mở
            item.trim().equals(selectedLanguage?.trim(), ignoreCase = true)
        } else {
            // Highlight item ở giữa màn hình khi scroll
            position == middleVisiblePosition
        }



        
        mBinding.tvLanguage.setTextColor(
            if (isHighlighted)
                Color.parseColor("#3C52F5")
            else
                Color.parseColor("#B9CBE2")
        )

        mBinding.root.isClickable = false
        mBinding.root.isFocusable = false
    }


    fun getMiddleItem(): String? {
        return if (list.isNotEmpty()) {
            list[middleVisiblePosition % list.size]
        } else null
    }

    override fun onBindViewHolder(holder: BaseAdapter<String>.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val actualPosition = position % list.size
        val item = list[actualPosition]
        holder.bind(item)
    }
    
    fun setSelectedLanguage(language: String?) {
        selectedLanguage = language
        notifyDataSetChanged()
    }
}