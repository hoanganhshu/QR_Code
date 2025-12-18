package com.example.qrscan.adapter


import android.view.View
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.database.data.DataGenerateInMonth
import com.example.qrscan.databinding.ItemGenerateInDayBinding

interface Callback{
    fun onEdit(item : DataGenerateInMonth)
    fun onDelete(item : DataGenerateInMonth)
    fun onShare(item : DataGenerateInMonth)


}

class AdapterGenerateInMonth(private val callback: Callback) : BaseAdapter<DataGenerateInMonth>() {

    override fun getItemLayout(): Int {
        return R.layout.item_generate_in_day
    }

    override fun setData(
        binding: ViewDataBinding,
        item: DataGenerateInMonth,
        position: Int
    ) {
       val mBinding = binding as ItemGenerateInDayBinding
        mBinding.imageicon.setImageResource(item.image)
        mBinding.title.setText(item.title)
        mBinding.subtitle.setText(item.subtitle)
        binding.threedot.setOnClickListener {
            binding.viewSwitcher.showNext()
        }

        binding.layoutActions.findViewById<View>(R.id.threedot)?.setOnClickListener {
            binding.viewSwitcher.showPrevious()
        }
        binding.close.setOnClickListener {
            binding.viewSwitcher.showNext()

        }
        binding.edit.setOnClickListener {
            callback.onEdit(item)

        }
        binding.share.setOnClickListener {
            callback.onShare(item)

        }
        binding.delete.setOnClickListener {
            callback.onDelete(item)
        }



    }

}