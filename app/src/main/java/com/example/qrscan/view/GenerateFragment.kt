package com.example.qrscan.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentGenerateBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GenerateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GenerateFragment : BaseFragment<FragmentGenerateBinding>(){
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGenerateBinding {
        return FragmentGenerateBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.createnow.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem = next.currentItem + 2
        }
        (activity as? MainActivity)?.showBottomNav(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(true)
    }

}