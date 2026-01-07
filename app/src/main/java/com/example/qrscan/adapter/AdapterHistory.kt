package com.example.qrscan.adapter


import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import com.example.qrscan.R
import com.example.qrscan.database.data.HistoryScan
import com.example.qrscan.databinding.ItemDayHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterHistory(private val callbach: Callbach): BaseAdapter<HistoryScan>() {
    private val childAdapters = mutableListOf<AdapterHistoryInMonth>()
    override fun getItemLayout(): Int {
        return R.layout.item_day_history
    }

    override fun setData(
        binding: ViewDataBinding,
        item: HistoryScan,
        position: Int
    ) {
       val mBinding =binding as ItemDayHistoryBinding

        mBinding.createin.setText(formatMonthYear(item.createAt))
        val childAdapterHistory = AdapterHistoryInMonth(callbach)
        mBinding.listItemHistoryInDay.adapter=childAdapterHistory
        childAdapterHistory.submitData(item.item)
        childAdapters.add(childAdapterHistory)
    }
    fun formatMonthYear(createdAt: Long): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val locale = appLocales.get(0) ?: Locale("en", "US")
        val sdf = SimpleDateFormat("MMM yyyy", locale)
        return sdf.format(Date(createdAt))
    }
    fun clearAllSelections() {
        childAdapters.forEach {
            it.clearSelectionMode()
        }
    }
    fun enableSelectionModeForAll() {
        childAdapters.forEach {
            it.enableSelectionMode()
        }
    }
    fun getAllSelectedIds(): List<Int> {
        val allSelectedIds = mutableListOf<Int>()
        childAdapters.forEach { adapter ->
            allSelectedIds.addAll(adapter.getSelectedIds())
        }
        return allSelectedIds.distinct()
    }

}