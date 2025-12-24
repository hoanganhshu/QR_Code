package com.example.qrscan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentCreateOptionBinding
import com.example.qrscan.viewmodel.ScanViewModel

class CreateOptionFragment : BaseFragment<FragmentCreateOptionBinding>() {

    private val viewModel: ScanViewModel by activityViewModels()

    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateOptionBinding {
        return FragmentCreateOptionBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false
        (activity as? MainActivity)?.showBottomNav(false)
        setUpOptionCreate()
        mBinding.btnback.setOnClickListener {

            next.currentItem = next.currentItem - 2
        }

    }

    private fun setUpOptionCreate() {
        mBinding.phone.setOnClickListener {
            viewModel.setCreateOption(QRType.PHONE)
            actionCreate()
        }
        mBinding.email.setOnClickListener {
            viewModel.setCreateOption(QRType.EMAIL)
            actionCreate()
        }
        mBinding.text.setOnClickListener {
            viewModel.setCreateOption(QRType.TEXT)
            actionCreate()
        }
        mBinding.calendar.setOnClickListener {
            viewModel.setCreateOption(QRType.EVENT)
            actionCreate()
        }
        mBinding.contact.setOnClickListener {
            viewModel.setCreateOption(QRType.CONTACT)
            actionCreate()
        }
        mBinding.location.setOnClickListener {
            viewModel.setCreateOption(QRType.LOCATION)
            actionCreate()
        }
        mBinding.sms.setOnClickListener {
            viewModel.setCreateOption(QRType.SMS)
            actionCreate()
        }
        mBinding.url.setOnClickListener {
            viewModel.setCreateOption(QRType.URL)
            actionCreate()
        }
        mBinding.wifi.setOnClickListener {
            viewModel.setCreateOption(QRType.WIFI)
            actionCreate()
        }

    }

    private fun actionCreate() {
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.currentItem = next.currentItem + 1
    }
}
