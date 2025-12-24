package com.example.qrscan.adapter


import android.R.attr.visibility
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.database.data.HistoryScan

import com.example.qrscan.database.data.QRCodeHistoryScanEntity
import com.example.qrscan.databinding.ItemHistoryInDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
interface Callbach{

    fun onDelete(item : QRCodeHistoryScanEntity)
    fun onShare(item : QRCodeHistoryScanEntity)
    fun onSelectionMode(isLongPressed: Boolean)
    fun onSelectedIdsChanged(ids: List<Int>)


}
class AdapterHistoryInMonth(private val callback: Callbach) : BaseAdapter<QRCodeHistoryScanEntity>() {


    private val selectedItems = mutableListOf<Int>()
    private var isLongPressed = false

    override fun getItemLayout(): Int {
        return R.layout.item_history_in_day
    }

    override fun setData(
        binding: ViewDataBinding,
        item: QRCodeHistoryScanEntity,
        position: Int
    ) {
        val mBinding = binding as ItemHistoryInDayBinding


        mBinding.iconPhoneFront.setImageResource(
            when(item.type.name.lowercase().replaceFirstChar { it.uppercase() }) {
                "Email" -> R.drawable.email
                "Phone" -> R.drawable.icon_phone
                "Contact" -> R.drawable.contacts
                "Sms" -> R.drawable.sms
                "Url" -> R.drawable.url
                "Text" -> R.drawable.text
                "Wifi" -> R.drawable.wifi
                "Calendar" -> R.drawable.calendar
                "Location" -> R.drawable.location
                else -> R.drawable.icon_phone
            }
        )



        if (isLongPressed) {






            if (selectedItems.contains(item.id)) {
                mBinding.select.visibility=View.VISIBLE
                mBinding.select.setImageResource(R.drawable.image_select)
                mBinding.circleImage.setImageResource(R.drawable.circle)
                mBinding.circleImage.setBackgroundColor(Color.parseColor("#3C52F5"))
            }
        } else {
            mBinding.circleImage.background=null
            mBinding.select.visibility=View.INVISIBLE
            mBinding.circleImage.setImageResource(R.drawable.two_dot)



        }






        mBinding.twoDots.setOnClickListener {
            if (isLongPressed) {
                toggleItem(item.id)


            } else {
                mBinding.viewSwitcher.showNext()
            }
        }

        mBinding.share.setOnClickListener {
            callback.onShare(item)
        }

        mBinding.delete.setOnClickListener {
            callback.onDelete(item)
        }
        mBinding.root.setOnLongClickListener {
            isLongPressed = true
            selectedItems.clear()
            selectedItems.add(item.id)
            callback.onSelectionMode(true)
            callback.onSelectedIdsChanged(selectedItems)


            notifyDataSetChanged()
            true
        }

        mBinding.title.setText(item.type.name.lowercase().replaceFirstChar { it.uppercase() })
        mBinding.createIn.setText(formatWithSuffix(item.createdAt))
    }


    fun deleteSelectedItems() {

        selectedItems.clear()
        notifyDataSetChanged()
    }



    fun formatWithSuffix(createdAt: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = createdAt
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)

        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val locale = appLocales.get(0) ?: Locale("en", "US")


        val month = SimpleDateFormat("MMM", locale)
            .format(calendar.time)

        return "$day$suffix $month, $year"
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


        callback.onSelectionMode(false)
        callback.onSelectedIdsChanged(emptyList())

    }

}
