package com.example.qrscan.adapter


import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrscan.R
import com.example.qrscan.database.data.HistoryScan
import com.example.qrscan.databinding.ItemDayHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterHistory(private val callbach: Callbach): BaseAdapter<HistoryScan>() {
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
    }
    fun formatMonthYear(createdAt: Long): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val locale = appLocales.get(0) ?: Locale("en", "US")
        val sdf = SimpleDateFormat("MMM yyyy", locale)
        return sdf.format(Date(createdAt))
    }

}