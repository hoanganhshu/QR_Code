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
        (activity as? MainActivity)?.showBottomNav(false)
        setUpOptionCreate()
        mBinding.btnback.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem = next.currentItem - 2
        }

    }

    private fun setUpOptionCreate() {
        mBinding.phone.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.phone))
            actionCreate()
        }
        mBinding.email.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.email))
            actionCreate()
        }
        mBinding.text.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.text))
            actionCreate()
        }
        mBinding.calendar.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.calendar))
            actionCreate()
        }
        mBinding.contact.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.contact))
            actionCreate()
        }
        mBinding.location.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.location))
            actionCreate()
        }
        mBinding.sms.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.sms))
            actionCreate()
        }
        mBinding.url.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.url))
            actionCreate()
        }
        mBinding.wifi.setOnClickListener {
            viewModel.setCreateOption(getString(R.string.wifi))
            actionCreate()
        }

        mBinding.barcode.setOnClickListener {

            viewModel.setCreateOption("barcode")
            actionCreate()
        }
    }

    private fun actionCreate() {
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.currentItem = next.currentItem + 1
    }
}
