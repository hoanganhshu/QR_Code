package com.example.qrscan.view

import android.R.attr.data
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterResult
import com.example.qrscan.database.data.QRCodeEntity
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
        (activity as? MainActivity)?.showBottomNav(false)

        val adapter = AdapterResult()
        mBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        mBinding.recyclerview.addItemDecoration(divider)

        mBinding.recyclerview.adapter = adapter
        (activity as? MainActivity)?.showBottomNav(false)

        val listData = viewModel.userInput.map { (key, value) ->
            mapOf(
                "title" to key,
                "subtitle" to value
            )
        }
        mBinding.btnback.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem = next.currentItem -1
        }
        val opt = viewModel.createOption.value
        mBinding.title.setText(opt)
        mBinding.subtitle.setText(getTodayFormatted())
        when (opt) {


            "Email" -> {
                mBinding.imagetitle.setImageResource(R.drawable.email)


            }

            "Phone" -> {
                mBinding.imagetitle.setImageResource(R.drawable.icon_phone)

            }

            "Location" -> {
                mBinding.imagetitle.setImageResource(R.drawable.location)
            }

            "Sms" -> {
                mBinding.imagetitle.setImageResource(R.drawable.sms)
            }


            "Contact" -> {
                mBinding.imagetitle.setImageResource(R.drawable.contacts)

            }

            "Url" -> {
                mBinding.imagetitle.setImageResource(R.drawable.url)

            }

            "Wifi" -> {
                mBinding.imagetitle.setImageResource(R.drawable.wifi)

            }

            "Text" -> {
                mBinding.imagetitle.setImageResource(R.drawable.text)
            }

            "Calendar" -> {
                mBinding.imagetitle.setImageResource(R.drawable.calendar)

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
            val next=requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem-=1

        }
        mBinding.save.setOnClickListener {

            val typeString = viewModel.createOption.value ?: return@setOnClickListener

            val type = when (typeString) {
                "Text" -> QRType.TEXT
                "Url" -> QRType.URL
                "Email" -> QRType.EMAIL
                "Phone" -> QRType.PHONE
                "Sms" -> QRType.SMS
                "Wifi" -> QRType.WIFI
                "Contact" -> QRType.CONTACT
                "Location" -> QRType.LOCATION
                "Calendar" -> QRType.EVENT
                else -> QRType.TEXT
            }

            val content = viewModel.content ?: return@setOnClickListener
            val input = viewModel.userInput
            val image = viewModel.byteQR ?: ByteArray(0)

            val qrid = viewModel.itemIdCreate

            if (qrid == 0) {

                viewModel.insertQRCode(
                    QRCodeEntity(
                        id = 0,
                        type = type,
                        content = content,
                        createdAt = System.currentTimeMillis(),
                        data = input,
                        image = image
                    )
                )
            } else {

                viewModel.updateQRCode(
                    QRCodeEntity(
                        id = qrid,
                        type = type,
                        content = content,
                        createdAt = System.currentTimeMillis(),
                        data = input,
                        image = image
                    )
                )
            }

            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
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


}