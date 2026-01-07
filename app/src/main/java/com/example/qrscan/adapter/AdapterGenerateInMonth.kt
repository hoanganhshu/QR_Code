package com.example.qrscan.adapter


import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.database.data.DataGenerateInMonth
import com.example.qrscan.databinding.ItemGenerateInDayBinding

interface Callback{
    fun onEdit(item : DataGenerateInMonth)
    fun onDelete(item : DataGenerateInMonth)
    fun onShare(item : DataGenerateInMonth)

    fun onSelectionMode(isLongPressed: Boolean)
    fun onSelectedIdsChanged(ids: List<Int>)

    fun onEnableSelectionModeForAll()


}
class AdapterGenerateInMonth(private val callback: Callback) : BaseAdapter<DataGenerateInMonth>() {
    private val selectedItems = mutableListOf<Int>()
    private var isLongPressed = false

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

        mBinding.layoutActions.findViewById<View>(R.id.threedot)?.setOnClickListener {
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
        mBinding.root.setOnLongClickListener {
            isLongPressed = true
            selectedItems.clear()
            selectedItems.add(item.id)
            callback.onSelectionMode(true)
            callback.onSelectedIdsChanged(selectedItems)
            callback.onEnableSelectionModeForAll()


            notifyDataSetChanged()
            true
        }

        if (isLongPressed) {
            mBinding.threedot.isEnabled=false
            mBinding.circle.visibility=View.VISIBLE

            if (selectedItems.contains(item.id)) {
                mBinding.select.setImageResource(R.drawable.image_selectvector)
                mBinding.circleImage.setBackgroundColor(Color.parseColor("#3C52F5"))
                mBinding.cardContainer.strokeColor =
                    ContextCompat.getColor(mBinding.root.context, R.color.base)

            }
            else {
                mBinding.circleImage.setBackgroundColor(Color.TRANSPARENT)
                mBinding.select.setImageResource(0)
                mBinding.cardContainer.strokeColor =Color.parseColor("#CDD0E3")

            }
        } else {
            mBinding.cardContainer.strokeColor =Color.parseColor("#CDD0E3")
            mBinding.threedot.isEnabled=true
            mBinding.circle.visibility=View.GONE

        }
        mBinding.circle.setOnClickListener {
            if (isLongPressed) {
                toggleItem(item.id)
            }
        }

    }
    private fun toggleItem(id: Int) {
        if (selectedItems.contains(id)) {
            selectedItems.remove(id)
        } else {
            selectedItems.add(id)
        }

        callback.onSelectedIdsChanged(selectedItems)

        if (selectedItems.isEmpty()) {
            clearSelectionMode()
        }

        notifyDataSetChanged()
    }
    fun clearSelectionMode() {
        isLongPressed = false
        selectedItems.clear()
        notifyDataSetChanged()
        Log.d("HISTORY", "userScan size = no action")

    }
    fun enableSelectionMode() {
        if (!isLongPressed) {
            isLongPressed = true
            notifyDataSetChanged()
        }
    }
    fun getSelectedIds(): List<Int> {
        return selectedItems.toList()
    }




}