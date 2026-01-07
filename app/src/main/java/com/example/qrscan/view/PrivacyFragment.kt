package com.example.qrscan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
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
          (activity as? MainActivity)?.navigateMain(SettingFragment())
      }
    }

    override fun onResume() {
        super.onResume()
        Log.e("NAV_DEBUG", "PrivacyFragment onResume", Throwable())

    }

}