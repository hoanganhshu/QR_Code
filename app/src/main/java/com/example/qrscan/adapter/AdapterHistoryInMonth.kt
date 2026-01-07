package com.example.qrscan.adapter


import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.database.data.QRCodeHistoryScanEntity
import com.example.qrscan.databinding.ItemHistoryInDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface Callbach{

    fun onDelete(item : QRCodeHistoryScanEntity)
    fun onShare(item : QRCodeHistoryScanEntity)
    fun onSelectionMode(isLongPressed: Boolean)
    fun onSelectedIdsChanged(ids: List<Int>)

    fun onEnableSelectionModeForAll()


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
                "Email" -> R.drawable.emailvector
                "Phone" -> R.drawable.icon_phonevector
                "Contact" -> R.drawable.contactsvector
                "Sms" -> R.drawable.smsvector
                "Url" -> R.drawable.urlvector
                "Text" -> R.drawable.textvector
                "Wifi" -> R.drawable.wifivector
                "Calendar" -> R.drawable.calendarvector
                "Location" -> R.drawable.locationvector
                else -> R.drawable.icon_phonevector
            }
        )



        if (isLongPressed) {


            if (selectedItems.contains(item.id)) {
                mBinding.select.visibility=View.VISIBLE
                mBinding.select.setImageResource(R.drawable.image_selectvector)
                mBinding.circleImage.setImageResource(R.drawable.circle)
                mBinding.circleImage.setBackgroundColor(Color.parseColor("#3C52F5"))
                mBinding.cardprevious.strokeColor =
                    ContextCompat.getColor(mBinding.root.context, R.color.base)

            }
            else{ mBinding.select.visibility=View.INVISIBLE
                mBinding.select.setImageResource(0)
                mBinding.circleImage.setImageResource(R.drawable.circle)
                mBinding.circleImage.setBackgroundColor(Color.WHITE)
                mBinding.cardprevious.strokeColor = Color.parseColor("#CDD0E3")



            }
        } else {

            mBinding.cardprevious.strokeColor = Color.parseColor("#CDD0E3")
            mBinding.circleImage.background=null
            mBinding.select.visibility=View.INVISIBLE
            mBinding.circleImage.setImageResource(R.drawable.two_dot)



        }






        mBinding.twoDots.setOnClickListener {
            if (isLongPressed) {
                toggleItem(item.id)


            } else {
                mBinding.viewSwitcher.showNext()
                mBinding.tvTitleShowNext.setText(item.type.name.lowercase().replaceFirstChar { it.uppercase() })

                mBinding.iconPhoneBack.setImageResource(
                    when(item.type.name.lowercase().replaceFirstChar { it.uppercase() }) {
                        "Email" -> R.drawable.emailvector
                        "Phone" -> R.drawable.icon_phonevector
                        "Contact" -> R.drawable.contactsvector
                        "Sms" -> R.drawable.smsvector
                        "Url" -> R.drawable.urlvector
                        "Text" -> R.drawable.textvector
                        "Wifi" -> R.drawable.wifivector
                        "Calendar" -> R.drawable.calendarvector
                        "Location" -> R.drawable.locationvector
                        else -> R.drawable.icon_phonevector
                    }
                )

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
            callback.onEnableSelectionModeForAll()


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
