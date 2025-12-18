package com.example.qrscan.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qrscan.BaseFragment
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentHistoryScanBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryScanFragment : BaseFragment<FragmentHistoryScanBinding>(){
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryScanBinding {
        return FragmentHistoryScanBinding.inflate(layoutInflater,container,false)
    }

}