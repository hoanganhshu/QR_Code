package com.example.qrscan.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentPrivacyBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrivacyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>(){
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPrivacyBinding {
        return FragmentPrivacyBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      mBinding.btnback.setOnClickListener {
          val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
          next.isUserInputEnabled = false
          next.currentItem = next.currentItem -1
      }
    }

}