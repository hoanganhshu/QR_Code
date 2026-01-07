package com.example.qrscan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.MainActivity
import com.example.qrscan.databinding.FragmentOnBoard3Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardFragment3.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardFragment3 : BaseFragment<FragmentOnBoard3Binding>(){
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoard3Binding {
        return FragmentOnBoard3Binding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.next.setOnClickListener {
            (activity as? MainActivity)?.goToMainFlow()

        }
        mBinding.skip.setOnClickListener {

        }
    }
    override fun onResume() {
        super.onResume()
        (activity as? BottomNavController)?.requestBottomNav(false)
    }

}

