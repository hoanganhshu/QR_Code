package com.example.qrscan.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterResult
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentResultBinding
import com.example.qrscan.viewmodel.ScanViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : BaseFragment<FragmentResultBinding>(){
    private val viewModel : ScanViewModel by activityViewModels()
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResultBinding {
        return FragmentResultBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()
            .findViewById<View>(R.id.bottomSelect)
            ?.visibility = View.GONE

        (activity as? BottomNavController)?.requestBottomNav(false)
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false

        val adapter = AdapterResult()
        mBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.divider_white
            )!!
        )
        mBinding.recyclerview.addItemDecoration(divider)

        mBinding.recyclerview.adapter = adapter
        val qrType = viewModel.createOption.value ?: return

        (activity as? BottomNavController)?.requestBottomNav(false)

        val listData = viewModel.userInput.map { (key, value) ->
            mapOf(
                "title" to key,
                "subtitle" to value
            )
        }
        mBinding.btnback.setOnClickListener {
            val adapter = mBinding.recyclerview.adapter as? AdapterResult
            adapter?.submitData(emptyList())

            viewModel.userInput = emptyMap()
            (activity as? MainActivity)?.navigateMain(CreateDetailFragment())

        }
        qrType?.let { type ->

            mBinding.title.text = getTitle(type)
            mBinding.subtitle.text = getTodayFormatted()

            when (type) {
                QRType.EMAIL -> mBinding.imagetitle.setImageResource(R.drawable.emailvector)
                QRType.PHONE -> mBinding.imagetitle.setImageResource(R.drawable.icon_phonevector)
                QRType.LOCATION -> mBinding.imagetitle.setImageResource(R.drawable.locationvector)
                QRType.SMS -> mBinding.imagetitle.setImageResource(R.drawable.smsvector)
                QRType.CONTACT -> mBinding.imagetitle.setImageResource(R.drawable.contactsvector)
                QRType.URL -> mBinding.imagetitle.setImageResource(R.drawable.urlvector)
                QRType.WIFI -> mBinding.imagetitle.setImageResource(R.drawable.wifivector)
                QRType.TEXT -> mBinding.imagetitle.setImageResource(R.drawable.textvector)
                QRType.EVENT -> mBinding.imagetitle.setImageResource(R.drawable.calendarvector)
                else -> {}
            }
        }


        val bytes = viewModel.byteQR

        if (bytes != null && bytes.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            mBinding.imageview.setImageBitmap(bitmap)
        } else {
            mBinding.imageview.setImageResource(R.drawable.error)
        }

        adapter.submitData(listData)
        mBinding.cancel.setOnClickListener {
            val adapter = mBinding.recyclerview.adapter as? AdapterResult
            adapter?.submitData(emptyList())

            viewModel.userInput = emptyMap()
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            (activity as? MainActivity)?.navigateMain(CreateDetailFragment())


        }
        mBinding.save.setOnClickListener {

            val qrType = viewModel.createOption.value ?: return@setOnClickListener


            viewModel.userInput = emptyMap()

            viewModel.saveCustomQR(
                id = viewModel.itemIdCreate,
                type = qrType,
                content = viewModel.content ?: return@setOnClickListener,
                data = viewModel.userInput
            )

            Toast.makeText(requireContext(), getString(R.string.save), Toast.LENGTH_SHORT).show()
        }
    }
    fun getTodayFormatted(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val suffix = when {
            day % 10 == 1 && day != 11 -> "st"
            day % 10 == 2 && day != 12 -> "nd"
            day % 10 == 3 && day != 13 -> "rd"
            else -> "th"
        }

        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
        val monthStr = monthFormat.format(calendar.time)

        return "$day$suffix $monthStr, $year"
    }


    override fun onResume() {
        super.onResume()


        val bytes = viewModel.byteQR
        if (bytes != null && bytes.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            mBinding.imageview.setImageBitmap(bitmap)
        } else {
            mBinding.imageview.setImageResource(R.drawable.error)
        }
        val listData = viewModel.userInput.map { (key, value) ->
            mapOf(
                "title" to key,
                "subtitle" to value
            )
        }

        val adapter = mBinding.recyclerview.adapter as AdapterResult

        adapter.submitData(listData)
    }
private fun getTitle(type: QRType): String {
    return when (type) {
        QRType.EMAIL    -> getString(R.string.email)
        QRType.PHONE    -> getString(R.string.phone)
        QRType.TEXT     -> getString(R.string.text)
        QRType.SMS      -> getString(R.string.sms)
        QRType.URL      -> getString(R.string.url)
        QRType.WIFI     -> getString(R.string.wifi)
        QRType.CONTACT  -> getString(R.string.contact)
        QRType.LOCATION -> getString(R.string.location)
        QRType.EVENT    -> getString(R.string.calendar)
        else -> "No"
    }
}



}